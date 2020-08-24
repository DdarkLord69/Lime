package com.example.lime.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lime.OthersProfileActivity;
import com.example.lime.R;
import com.example.lime.adapters.UserinListAdapter;
import com.example.lime.adapters.UsersFragmentAdapter;
import com.example.lime.models.UserinList;
import com.example.lime.models.UsersData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UsersFragment extends Fragment {

    private ArrayList<UsersData> usersList;
    private ArrayList<UsersData> usersList1;
    private ArrayList<UsersData> suggested;

    private List<String> myFollowing;
    private List<String> theirFollowing;

    private EditText etSearch;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewSugg;

    private UsersFragmentAdapter adapter;
    private UsersFragmentAdapter adapterSugg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_users, container, false);

        etSearch = v.findViewById(R.id.etUserSearch);
        recyclerView = v.findViewById(R.id.recycler_view_users_search);
        recyclerViewSugg = v.findViewById(R.id.recycler_view_sugg);
        usersList = new ArrayList<>();
        usersList1 = new ArrayList<>();
        suggested = new ArrayList<>();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        setUpRecyclerView();

        setUpSuggestions();

        return v;
    }

    private void setUpRecyclerView() {

        FirebaseFirestore.getInstance().collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                    usersList.add(qds.toObject(UsersData.class));
                }

                adapter = new UsersFragmentAdapter(usersList);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                adapter.setOnItemClickListener(new UsersFragmentAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        OthersProfileActivity.currentUid = usersList.get(position).getUserId();
                        startActivity(new Intent(getContext(), OthersProfileActivity.class));
                    }
                });

                recyclerView.setAdapter(adapter);

            }
        });
    }

    private void setUpSuggestions() {
        FirebaseFirestore.getInstance().collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                adapterSugg = new UsersFragmentAdapter(suggested);
                recyclerViewSugg.setLayoutManager(new LinearLayoutManager(getContext()));

                adapterSugg.setOnItemClickListener(new UsersFragmentAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        OthersProfileActivity.currentUid = suggested.get(position).getUserId();
                        startActivity(new Intent(getContext(), OthersProfileActivity.class));
                    }
                });

                recyclerViewSugg.setAdapter(adapterSugg);

                for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                    usersList1.add(qds.toObject(UsersData.class));
                }

                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        myFollowing = documentSnapshot.toObject(UsersData.class).getFollowing();
                        for(UsersData u : usersList1) {
                            theirFollowing = u.getFollowing();
                            theirFollowing.retainAll(myFollowing);
                            if(theirFollowing.size() >= 2 && !u.getUserId().
                                    equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && !myFollowing.contains(u.getUserId())) {
                                suggested.add(u);
                                adapterSugg.notifyDataSetChanged();
                            }
                        }
                    }
                });
            }
        });
    }

    private void filter(String text) {
        ArrayList<UsersData> filteredList = new ArrayList<>();

        for(UsersData u : usersList) {
            if(u.getDisplayName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(u);
            }
        }

        adapter.filterList(filteredList);
    }
}
