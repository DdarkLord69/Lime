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

import com.example.lime.NewImagePost;
import com.example.lime.R;
import com.example.lime.adapters.ImagePostAdapter;
import com.example.lime.adapters.PostAdapter;
import com.example.lime.models.ImagePost;
import com.example.lime.models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PhotosFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference postCollRef = db.collection("imagePosts");
    private CollectionReference userCollRef = db.collection("users");

    private ImagePostAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photos, container, false);

        recyclerView = v.findViewById(R.id.recycler_view);
        fab = v.findViewById(R.id.button_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewImagePost();
            }
        });

        return v;
    }

    private void setUpRecyclerView() {
        Query query = postCollRef.orderBy("dateAndTimePosted", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ImagePost> options = new FirestoreRecyclerOptions.Builder<ImagePost>()
                .setQuery(query, ImagePost.class)
                .build();

        adapter = new ImagePostAdapter(options);

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

    private void addNewImagePost() {
        startActivity(new Intent(getContext(), NewImagePost.class));
    }
}
