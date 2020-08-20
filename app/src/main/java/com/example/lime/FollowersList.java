package com.example.lime;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.lime.adapters.PostAdapter;
import com.example.lime.adapters.UserinListAdapter;
import com.example.lime.models.Post;
import com.example.lime.models.UserinList;
import com.example.lime.models.UsersData;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class FollowersList extends AppCompatActivity {

    public static String ersoring;
    public static String whoseUid;
    public static List<String> followers;
    public static String dN;

    private RecyclerView recyclerView;
    private UserinListAdapter adapter;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers_list);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        tvTitle = findViewById(R.id.tvTitleFoll);
        recyclerView = findViewById(R.id.recycler_view_foll);

        if(ersoring.equals("ers")) {
            tvTitle.setText(dN + "'s Followers");
        } else {
            tvTitle.setText("Users followed by " + dN);
        }

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        CollectionReference ucr = FirebaseFirestore.getInstance().collection("users");

        Query query;
        if(followers.isEmpty()) {
            query = ucr.whereEqualTo("userId", "imtypingarandomtextcuzijustwantthistoreturnanamptyarray");
        } else {
            query = ucr.whereIn("userId", followers);
        }

        FirestoreRecyclerOptions<UsersData> options = new FirestoreRecyclerOptions.Builder<UsersData>()
                .setQuery(query, UsersData.class)
                .build();

        adapter = new UserinListAdapter(options);

        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
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
}