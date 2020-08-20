package com.example.lime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lime.adapters.PostAdapter;
import com.example.lime.models.Post;
import com.example.lime.models.UsersData;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OthersProfileActivity extends AppCompatActivity {

    private static final String TAG = "OthersProfileActivity";

    TextView tvDnauid;
    TextView newUid;
    ImageView ivDP;
    TextView tvposts;
    TextView tvfollowers;
    TextView tvfollowing;
    TextView followersTitle;
    TextView followingTitle;
    Button btnFollow;

    private PostAdapter adapter;
    private RecyclerView recyclerView;

    public static String currentUid;
    DocumentReference userDocRef = FirebaseFirestore.getInstance().collection("users").document(currentUid);

    private List<String> localFollowers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        tvDnauid = findViewById(R.id.tvDisplayNameandUid);
        newUid = findViewById(R.id.newestuid);
        ivDP = findViewById(R.id.ivProfilepic);
        tvposts = findViewById(R.id.posts_count);
        tvfollowers = findViewById(R.id.followers_count);
        tvfollowing = findViewById(R.id.following_count);
        followersTitle = findViewById(R.id.followers_title);
        followingTitle = findViewById(R.id.following_title);
        recyclerView = findViewById(R.id.recycler_view_profile);
        btnFollow = findViewById(R.id.btnFollow);

        if(currentUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            btnFollow.setVisibility(View.INVISIBLE);
        }

        setUpRecyclerView();

        userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()) {
                    UsersData u = value.toObject(UsersData.class);

                    tvDnauid.setText(Html.fromHtml("<b>" + u.getDisplayName() + " </b>"));
                    newUid.setText("@" + u.getUserId());
                    Picasso.get().load(u.getProfilePicUrl()).into(ivDP);
                    tvposts.setText(String.valueOf(u.getPostCount()));
                    tvfollowers.setText(String.valueOf(u.getFollowers().size()));
                    tvfollowing.setText(String.valueOf(u.getFollowing().size()));
                    if(u.getFollowers().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        btnFollow.setText("Unfollow");
                    } else {
                        btnFollow.setText("Follow");
                    }
                }
            }
        });

        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UsersData u = documentSnapshot.toObject(UsersData.class);

                tvDnauid.setText(Html.fromHtml("<b>" + u.getDisplayName() + " </b>"));
                newUid.setText("@" + u.getUserId());
                Picasso.get().load(u.getProfilePicUrl()).into(ivDP);
                tvposts.setText(String.valueOf(u.getPostCount()));
                tvfollowers.setText(String.valueOf(u.getFollowers().size()));
                tvfollowing.setText(String.valueOf(u.getFollowing().size()));
                if(u.getFollowers().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    btnFollow.setText("Unfollow");
                } else {
                    btnFollow.setText("Follow");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OthersProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String clientuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        UsersData u = documentSnapshot.toObject(UsersData.class);

                        if(u.getFollowers().contains(clientuid)) {
                            btnFollow.setText("Follow");
                            userDocRef.update("followers", FieldValue.arrayRemove(clientuid));
                            FirebaseFirestore.getInstance().collection("users").document(clientuid)
                                    .update("following", FieldValue.arrayRemove(currentUid));
                        } else {
                            btnFollow.setText("Unfollow");
                            userDocRef.update("followers", FieldValue.arrayUnion(clientuid));
                            FirebaseFirestore.getInstance().collection("users").document(clientuid)
                                    .update("following", FieldValue.arrayUnion(currentUid));
                        }
                    }
                });
            }
        });

        tvfollowers.setOnClickListener(followerListener);
        followersTitle.setOnClickListener(followerListener);
        tvfollowing.setOnClickListener(followingListener);
        followingTitle.setOnClickListener(followingListener);

    }

    private void setUpRecyclerView() {
        CollectionReference pcr = FirebaseFirestore.getInstance().collection("posts");
        //Query query = pcr.orderBy("dateAndTimePosted", Query.Direction.DESCENDING);
        Query query = pcr.whereEqualTo("userId", currentUid).orderBy("dateAndTimePosted", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        adapter = new PostAdapter(options);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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

    View.OnClickListener followerListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            FirebaseFirestore.getInstance().collection("users").document(currentUid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UsersData u = documentSnapshot.toObject(UsersData.class);
                            FollowersList.ersoring = "ers";
                            FollowersList.dN = u.getDisplayName();
                            FollowersList.followers = u.getFollowers();
                            FollowersList.whoseUid = currentUid;
                            startActivity(new Intent(v.getContext(), FollowersList.class));
                        }
                    });
        }
    };

    View.OnClickListener followingListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            FirebaseFirestore.getInstance().collection("users").document(currentUid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UsersData u = documentSnapshot.toObject(UsersData.class);
                            FollowersList.ersoring = "ing";
                            FollowersList.dN = u.getDisplayName();
                            FollowersList.followers = u.getFollowing();
                            FollowersList.whoseUid = currentUid;
                            startActivity(new Intent(v.getContext(), FollowersList.class));
                        }
                    });
        }
    };
}