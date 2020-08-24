package com.example.lime.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lime.EditPost;
import com.example.lime.EditProfile;
import com.example.lime.FollowersList;
import com.example.lime.OthersProfileActivity;
import com.example.lime.PostComments;
import com.example.lime.R;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    TextView tvDnauid;
    TextView newUid;
    ImageView ivDP;
    TextView tvposts;
    TextView tvfollowers;
    TextView tvfollowing;
    TextView followersTitle;
    TextView followingTitle;
    Button btnEditProfile;

    private PostAdapter adapter;
    private RecyclerView recyclerView;

    String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DocumentReference userDocRef = FirebaseFirestore.getInstance().collection("users").document(currentUid);


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        tvDnauid = v.findViewById(R.id.tvDisplayNameandUid);
        newUid = v.findViewById(R.id.newestuid);
        ivDP = v.findViewById(R.id.ivProfilepic);
        tvposts = v.findViewById(R.id.posts_count);
        tvfollowers = v.findViewById(R.id.followers_count);
        tvfollowing = v.findViewById(R.id.following_count);
        followersTitle = v.findViewById(R.id.followers_title);
        followingTitle = v.findViewById(R.id.following_title);
        recyclerView = v.findViewById(R.id.recycler_view_profile);
        btnEditProfile = v.findViewById(R.id.btnEditProfile);

        userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                UsersData u = value.toObject(UsersData.class);

                tvDnauid.setText(Html.fromHtml("<b>" + u.getDisplayName() + " </b>"));
                newUid.setText("@" + u.getUserId());
                Picasso.get().load(u.getProfilePicUrl()).into(ivDP);
                tvposts.setText(String.valueOf(u.getPostCount()));
                tvfollowers.setText(String.valueOf(u.getFollowers().size()));
                tvfollowing.setText(String.valueOf(u.getFollowing().size()));
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditProfile.class));
            }
        });

        tvfollowers.setOnClickListener(followerListener);
        followersTitle.setOnClickListener(followerListener);
        tvfollowing.setOnClickListener(followingListener);
        followingTitle.setOnClickListener(followingListener);

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
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    private void setUpRecyclerView() {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference pcr = FirebaseFirestore.getInstance().collection("posts");
        //Query query = pcr.orderBy("dateAndTimePosted", Query.Direction.DESCENDING);
        Query query = pcr.whereEqualTo("userId", currentUid).orderBy("dateAndTimePosted", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        adapter = new PostAdapter(options);

        adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onProfileClick(DocumentSnapshot documentSnapshot, int position) {
                String uid = documentSnapshot.toObject(Post.class).getUserId();
                OthersProfileActivity.currentUid = uid;
                startActivity(new Intent(getContext(), OthersProfileActivity.class));
            }

            @Override
            public void onCommentClick(DocumentSnapshot documentSnapshot, int position) {
                documentSnapshot.getReference().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Post post = documentSnapshot.toObject(Post.class);
                        PostComments.model = post;
                        PostComments.basePostRef = documentSnapshot.getReference();
                        startActivity(new Intent(getContext(), PostComments.class));
                    }
                });
            }

            @Override
            public void onEdit(DocumentSnapshot documentSnapshot, int position) {
                EditPost.content = documentSnapshot.toObject(Post.class).getPostContent();
                EditPost.postRef = documentSnapshot.getReference();
                EditPost.whichContent = "postContent";
                startActivity(new Intent(getContext(), EditPost.class));
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpRecyclerView();
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
