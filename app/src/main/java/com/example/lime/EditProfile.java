package com.example.lime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    ImageView ivProfilePic;
    EditText etDisplayName;
    Button btnChooseImg;
    Button btnSave;

    private Uri imageUri;
    StorageReference storageReference;
    String uploadedimgurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        imageUri = null;
        storageReference = FirebaseStorage.getInstance().getReference("userProfilePics");

        ivProfilePic = findViewById(R.id.ivProfilePic);
        etDisplayName = findViewById(R.id.etDisplayName);
        btnChooseImg = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSaveChanges);

        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newDisplayName = etDisplayName.getText().toString();
                if(TextUtils.isEmpty(newDisplayName)) {
                    etDisplayName.setError("Name cannot be empty");
                    return;
                }
                if(imageUri == null) {
                    FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .update("displayName", newDisplayName);
                    CollectionReference pcr = FirebaseFirestore.getInstance().collection("posts");
                    pcr.whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for(QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                                        qds.getReference().update("displayName", newDisplayName);
                                    }
                                }
                            });
                } else {
                    StorageReference fileRef = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + Timestamp.now().toDate().getTime() + "." + getFileExtension(imageUri));
                    fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    uploadedimgurl = uri.toString();
                                    DocumentReference userDocRef = FirebaseFirestore.getInstance().collection("users")
                                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    userDocRef.update("displayName", newDisplayName);
                                    userDocRef.update("profilePicUrl", uploadedimgurl);
                                    CollectionReference pcr = FirebaseFirestore.getInstance().collection("posts");
                                    pcr.whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    for(QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                                                        qds.getReference().update("displayName", newDisplayName);
                                                        qds.getReference().update("profilePicUrl", uploadedimgurl);
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                finish();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            Picasso.get().load(imageUri).into(ivProfilePic);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}