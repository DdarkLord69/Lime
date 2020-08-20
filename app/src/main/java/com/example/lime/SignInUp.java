package com.example.lime;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lime.models.UsersData;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignInUp extends AppCompatActivity {

    private int AUTHUI_REQUEST_CODE = 10001;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCollectionRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_up);

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            Toast.makeText(this, "Welcome Back", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_launcher_foreground)
                .setTheme(R.style.AppTheme)
                .build();

        startActivityForResult(intent, AUTHUI_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AUTHUI_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                IdpResponse idpResponse = IdpResponse.fromResultIntent(data);

                if(idpResponse.isNewUser()) {
                    Toast.makeText(this, "New User created", Toast.LENGTH_SHORT).show();

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String displayName = user.getDisplayName();
                    String profilepicurl = "https://i.imgur.com/cdMxjUZ.png";
                    List<String> followers = new ArrayList<>();
                    List<String> following = new ArrayList<>();

                    UsersData userData = new UsersData(user.getUid(), displayName, profilepicurl, followers, following, 0);
                    userCollectionRef.document(user.getUid()).set(userData);

                    //startActivity(new Intent(getApplicationContext(), ???.class));
                } else {
                    Toast.makeText(this, "Welcome Back", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        } else {
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
            if(idpResponse == null) {
                Toast.makeText(this, "onActivityResult: user has cancelled sign in", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "onActivityResult:" + idpResponse.getError(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}