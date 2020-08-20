package com.example.lime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lime.models.Post;
import com.example.lime.models.UsersData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class NewPost extends AppCompatActivity {

    private EditText etContent;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        etContent = findViewById(R.id.etPostContent);
        fab = findViewById(R.id.btnCreatePost);

        final CollectionReference postCollRef = FirebaseFirestore.getInstance().collection("posts");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String content = etContent.getText().toString().trim();
                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                final DocumentReference userDocRef = FirebaseFirestore.getInstance().collection("users").document(uid);

                if(TextUtils.isEmpty(content)) {
                    etContent.setError("Post is empty");
                    return;
                }

                userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UsersData u = documentSnapshot.toObject(UsersData.class);

                        Post post = new Post(uid, u.getDisplayName(), u.getProfilePicUrl(), content, Timestamp.now().toDate(), new ArrayList<String>(), 0);
                        postCollRef.document(uid + Timestamp.now().toDate().getTime()).set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(NewPost.this, "New Post Added", Toast.LENGTH_SHORT).show();
                                userDocRef.update("postCount", FieldValue.increment(1));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(NewPost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }
}