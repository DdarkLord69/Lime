package com.example.lime.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lime.OthersProfileActivity;
import com.example.lime.R;
import com.example.lime.models.UserinList;
import com.example.lime.models.UsersData;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class UserinListAdapter extends FirestoreRecyclerAdapter<UsersData, UserinListAdapter.UserinListHolder> {

    private OnItemClickListener listener;

    public UserinListAdapter(@NonNull FirestoreRecyclerOptions<UsersData> options) {
        super(options);
    }

    @NonNull
    @Override
    public UserinListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_in_list_item, parent, false);

        return new UserinListHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserinListHolder holder, int position, @NonNull UsersData model) {
        Picasso.get().load(model.getProfilePicUrl()).into(holder.ivDp);
        holder.tvDisplayName.setText(model.getDisplayName());
        holder.tvUid.setText("@" + model.getUserId());
    }

    class UserinListHolder extends RecyclerView.ViewHolder {

        TextView tvDisplayName;
        TextView tvUid;
        ImageView ivDp;

        public UserinListHolder(@NonNull View itemView) {
            super(itemView);

            tvDisplayName = itemView.findViewById(R.id.tvDisplayName);
            tvUid = itemView.findViewById(R.id.tvUid);
            ivDp = itemView.findViewById(R.id.ivDp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getSnapshots().getSnapshot(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
