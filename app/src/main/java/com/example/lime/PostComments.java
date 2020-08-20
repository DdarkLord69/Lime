package com.example.lime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lime.adapters.CommentAdapter;
import com.example.lime.adapters.PostAdapter;
import com.example.lime.models.Comment;
import com.example.lime.models.Post;
import com.example.lime.models.UsersData;
import com.example.lime.timeformats.CustomDayTimeFormat;
import com.example.lime.timeformats.CustomHourTimeFormat;
import com.example.lime.timeformats.CustomMinuteTimeFormat;
import com.example.lime.timeformats.CustomMonthTimeFormat;
import com.example.lime.timeformats.CustomWeekTimeFormat;
import com.example.lime.timeformats.CustomYearTimeFormat;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.Day;
import org.ocpsoft.prettytime.units.Hour;
import org.ocpsoft.prettytime.units.Minute;
import org.ocpsoft.prettytime.units.Month;
import org.ocpsoft.prettytime.units.Week;
import org.ocpsoft.prettytime.units.Year;

import java.util.ArrayList;
import java.util.List;

public class PostComments extends AppCompatActivity {

    public static DocumentReference basePostRef;
    public static Post model;

    private int initialCounter;

    private CommentAdapter adapter;
    private RecyclerView recyclerView;

    private EditText etAddComment;
    private FloatingActionButton fab;

    TextView tvDisplayName;
    TextView tvUid;
    TextView tvContent;
    TextView tvTimeSincePosted;
    TextView tvLikes;
    ImageView ivProfilePic;
    Button btnLike;
    TextView tvComments;
    Button btnComment;
    Button btnOptionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        etAddComment = findViewById(R.id.etAddComment);
        fab = findViewById(R.id.btn_add_comment);

        tvDisplayName = findViewById(R.id.tvDisplayName);
        tvUid = findViewById(R.id.tvUid);
        tvContent = findViewById(R.id.tvContent);
        tvTimeSincePosted = findViewById(R.id.tvTimeSincePosted);
        tvLikes = findViewById(R.id.tvLikes);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        btnLike = findViewById(R.id.btnLike);
        tvComments = findViewById(R.id.tvComments);
        btnComment = findViewById(R.id.btnComment);
        btnOptionsMenu = findViewById(R.id.btnOptionsMenu);

        final PrettyTime p = new PrettyTime();

        p.registerUnit(new Minute(), new CustomMinuteTimeFormat());
        p.registerUnit(new Hour(), new CustomHourTimeFormat());
        p.registerUnit(new Day(), new CustomDayTimeFormat());
        p.registerUnit(new Week(), new CustomWeekTimeFormat());
        p.registerUnit(new Month(), new CustomMonthTimeFormat());
        p.registerUnit(new Year(), new CustomYearTimeFormat());

        p.setReference(Timestamp.now().toDate());

        basePostRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Post post = value.toObject(Post.class);
                tvContent.setText(post.getPostContent());
                tvLikes.setText(String.valueOf(post.getLikedBy().size()));
                tvComments.setText(String.valueOf(post.getComments()));
            }
        });

        Picasso.get().load(model.getProfilePicUrl()).into(ivProfilePic);
        tvDisplayName.setText(model.getDisplayName());
        tvUid.setText("@" + model.getUserId());
        tvContent.setText(model.getPostContent());
        tvLikes.setText(String.valueOf(model.getLikedBy().size()));
        if(model.getLikedBy().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            btnLike.setBackgroundResource(R.drawable.ic_liked);
        } else {
            btnLike.setBackgroundResource(R.drawable.ic_unliked);
        }
        if("moments ago".equals(p.format(model.getDateAndTimePosted()))) {
            tvTimeSincePosted.setText("now");
        } else {
            tvTimeSincePosted.setText(p.format(model.getDateAndTimePosted()));
        }
        tvComments.setText(String.valueOf(model.getComments()));

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialCounter = model.getLikedBy().size();
                final DocumentReference postRef = basePostRef;
                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                postRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<String> likedBy = documentSnapshot.toObject(Post.class).getLikedBy();
                        if(likedBy.contains(uid)) {
                            postRef.update("likedBy", FieldValue.arrayRemove(uid));
                            btnLike.setBackgroundResource(R.drawable.ic_unliked);
                            initialCounter -= 1;
                            tvLikes.setText(String.valueOf(initialCounter));
                        } else {
                            postRef.update("likedBy", FieldValue.arrayUnion(uid));
                            btnLike.setBackgroundResource(R.drawable.ic_liked);
                            initialCounter += 1;
                            tvLikes.setText(String.valueOf(initialCounter));
                        }
                    }
                });

            }
        });

        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(model.getUserId())) {
            btnOptionsMenu.setVisibility(View.VISIBLE);
            btnOptionsMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), btnOptionsMenu);
                    popupMenu.inflate(R.menu.popup_options_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit_post:
                                    EditPost.content = tvContent.getText().toString();
                                    EditPost.postRef = basePostRef;
                                    EditPost.whichContent = "postContent";
                                    v.getContext().startActivity(new Intent(v.getContext(), EditPost.class));

                                    return true;
                                case R.id.delete_post:
                                    basePostRef.delete();
                                    FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("postCount", FieldValue.increment(-1));
                                    basePostRef.collection("comments").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for(QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                                                qds.getReference().delete();
                                            }
                                        }
                                    });
                                    Toast.makeText(PostComments.this, "Post Deleted", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    //displaying the popup
                    popupMenu.show();
                }
            });
        }

        recyclerView = findViewById(R.id.recycler_view_comments);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = basePostRef.collection("comments").orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        adapter = new CommentAdapter(options);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String comment1 = etAddComment.getText().toString();
                if(TextUtils.isEmpty(comment1)) {
                    etAddComment.setError("Empty comment");
                    return;
                }
                final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore.getInstance().collection("users").document(userid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UsersData u = documentSnapshot.toObject(UsersData.class);

                        etAddComment.setText("");
                        closeKeyboard();
                        Comment comment = new Comment(userid, u.getDisplayName(), u.getProfilePicUrl(), comment1, Timestamp.now().toDate(), new ArrayList<String>());
                        basePostRef.collection("comments").add(comment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(PostComments.this, "Comment Added", Toast.LENGTH_SHORT).show();
                                basePostRef.update("comments", FieldValue.increment(1));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PostComments.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}