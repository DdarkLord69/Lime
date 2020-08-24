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
import com.example.lime.models.UsersData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersFragmentAdapter extends RecyclerView.Adapter<UsersFragmentAdapter.UsersViewHolder> {

    private ArrayList<UsersData> usersList;
    private OnItemClickListener listener;

    public UsersFragmentAdapter(ArrayList<UsersData> usersList) {
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_in_list_item, parent, false);

        return new UsersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        UsersData currentItem = usersList.get(position);

        Picasso.get().load(currentItem.getProfilePicUrl()).into(holder.ivDp);
        holder.tvDisplayName.setText(currentItem.getDisplayName());
        holder.tvUid.setText("@" + currentItem.getUserId());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public void filterList(ArrayList<UsersData> filteredList) {
        usersList = filteredList;
        notifyDataSetChanged();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivDp;
        public TextView tvDisplayName;
        public TextView tvUid;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);

            ivDp = itemView.findViewById(R.id.ivDp);
            tvDisplayName = itemView.findViewById(R.id.tvDisplayName);
            tvUid = itemView.findViewById(R.id.tvUid);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getAdapterPosition() != RecyclerView.NO_POSITION && listener != null)
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
