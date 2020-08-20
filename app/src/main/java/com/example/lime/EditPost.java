package com.example.lime;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;

public class EditPost extends AppCompatActivity {

    public static DocumentReference postRef;
    public static String content;
    public static String whichContent;

    private TextView tvTitle;
    private EditText etContent;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        tvTitle = findViewById(R.id.textView3Edit);
        etContent = findViewById(R.id.etPostContentEdit);
        fab = findViewById(R.id.btnPublishEditedPost);

        if(whichContent.equals("postContent")) {
            tvTitle.setText("Edit your Post");
        } else {
            tvTitle.setText("Edit your Comment");
        }

        etContent.setText(content);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newContent = etContent.getText().toString();
                if(TextUtils.isEmpty(newContent)) {
                    etContent.setError("Empty body");
                    return;
                }
                postRef.update(whichContent, newContent).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(whichContent.equals("postContent")) {
                            Toast.makeText(EditPost.this, "Post edited", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditPost.this, "Comment edited", Toast.LENGTH_SHORT).show();
                            PostComments.model.setPostContent(newContent);
                        }
                        finish();
                    }
                });
            }
        });
    }
}