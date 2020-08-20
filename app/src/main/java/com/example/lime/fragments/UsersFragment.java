package com.example.lime.fragments;

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

import com.example.lime.R;
import com.example.lime.adapters.UserinListAdapter;
import com.example.lime.adapters.UsersFragmentAdapter;
import com.example.lime.models.UserinList;
import com.example.lime.models.UsersData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UsersFragment extends Fragment {

    private ArrayList<UsersData> usersList;

    private EditText etSearch;
    private RecyclerView recyclerView;

    private UsersFragmentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_users, container, false);

        etSearch = v.findViewById(R.id.etUserSearch);
        recyclerView = v.findViewById(R.id.recycler_view_users_search);
        usersList = new ArrayList<>();

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

                recyclerView.setAdapter(adapter);

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
