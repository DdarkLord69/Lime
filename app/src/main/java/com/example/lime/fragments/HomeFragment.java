package com.example.lime.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lime.EditPost;
import com.example.lime.NewPost;
import com.example.lime.OthersProfileActivity;
import com.example.lime.PostComments;
import com.example.lime.R;
import com.example.lime.adapters.PostAdapter;
import com.example.lime.models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference postCollRef = db.collection("posts");
    private CollectionReference userCollRef = db.collection("users");

    private PostAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = v.findViewById(R.id.recycler_view);
        fab = v.findViewById(R.id.button_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPost();
            }
        });

        return v;
    }

    private void setUpRecyclerView() {
        Query query = postCollRef.orderBy("dateAndTimePosted", Query.Direction.DESCENDING);

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

    private void addNewPost() {
        startActivity(new Intent(getContext(), NewPost.class));
    }
}
