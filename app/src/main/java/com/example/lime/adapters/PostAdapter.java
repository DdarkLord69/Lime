package com.example.lime.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lime.EditPost;
import com.example.lime.OthersProfileActivity;
import com.example.lime.PostComments;
import com.example.lime.R;
import com.example.lime.models.Post;
import com.example.lime.timeformats.CustomDayTimeFormat;
import com.example.lime.timeformats.CustomHourTimeFormat;
import com.example.lime.timeformats.CustomMinuteTimeFormat;
import com.example.lime.timeformats.CustomMonthTimeFormat;
import com.example.lime.timeformats.CustomWeekTimeFormat;
import com.example.lime.timeformats.CustomYearTimeFormat;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
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

import java.util.List;

public class PostAdapter extends FirestoreRecyclerAdapter<Post, PostAdapter.PostHolder> {

    public PostAdapter(@NonNull FirestoreRecyclerOptions<Post> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final PostHolder holder, final int position, @NonNull final Post model) {

        final PrettyTime p = new PrettyTime();

        p.registerUnit(new Minute(), new CustomMinuteTimeFormat());
        p.registerUnit(new Hour(), new CustomHourTimeFormat());
        p.registerUnit(new Day(), new CustomDayTimeFormat());
        p.registerUnit(new Week(), new CustomWeekTimeFormat());
        p.registerUnit(new Month(), new CustomMonthTimeFormat());
        p.registerUnit(new Year(), new CustomYearTimeFormat());

        p.setReference(Timestamp.now().toDate());

        Picasso.get().load(model.getProfilePicUrl()).into(holder.ivProfilePic);
        holder.tvDisplayName.setText(model.getDisplayName());
        holder.tvUid.setText("@" + model.getUserId());
        holder.tvContent.setText(model.getPostContent());
        holder.tvLikes.setText(String.valueOf(model.getLikedBy().size()));
        if(model.getLikedBy().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            holder.btnLike.setBackgroundResource(R.drawable.ic_liked);
        } else {
            holder.btnLike.setBackgroundResource(R.drawable.ic_unliked);
        }
        if("moments ago".equals(p.format(model.getDateAndTimePosted()))) {
            holder.tvTimeSincePosted.setText("now");
        } else {
            holder.tvTimeSincePosted.setText(p.format(model.getDateAndTimePosted()));
        }
        holder.tvComments.setText(String.valueOf(model.getComments()));

        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(model.getUserId())) {
            holder.btnOptionsMenu.setVisibility(View.VISIBLE);
            holder.btnOptionsMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), holder.btnOptionsMenu);
                    popupMenu.inflate(R.menu.popup_options_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit_post:
                                    EditPost.content = holder.tvContent.getText().toString();
                                    EditPost.postRef = getSnapshots().getSnapshot(position).getReference();
                                    EditPost.whichContent = "postContent";
                                    v.getContext().startActivity(new Intent(v.getContext(), EditPost.class));

                                    return true;
                                case R.id.delete_post:
                                    getSnapshots().getSnapshot(position).getReference().delete();
                                    FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("postCount", FieldValue.increment(-1));
                                    getSnapshots().getSnapshot(position).getReference().collection("comments").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for(QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                                                qds.getReference().delete();
                                            }
                                        }
                                    });
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

    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);

        return new PostHolder(v);
    }

    class PostHolder extends RecyclerView.ViewHolder {
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

        public PostHolder(@NonNull final View itemView) {
            super(itemView);

            tvDisplayName = itemView.findViewById(R.id.tvDisplayName);
            tvUid = itemView.findViewById(R.id.tvUid);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTimeSincePosted = itemView.findViewById(R.id.tvTimeSincePosted);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            btnLike = itemView.findViewById(R.id.btnLike);
            tvComments = itemView.findViewById(R.id.tvComments);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnOptionsMenu = itemView.findViewById(R.id.btnOptionsMenu);

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DocumentReference postRef = getSnapshots().getSnapshot(getAdapterPosition()).getReference();
                    final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    postRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            List<String> likedBy = documentSnapshot.toObject(Post.class).getLikedBy();
                            if(likedBy.contains(uid)) {
                                postRef.update("likedBy", FieldValue.arrayRemove(uid));
                            } else {
                                postRef.update("likedBy", FieldValue.arrayUnion(uid));
                            }
                        }
                    });
                }
            });

            btnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    getSnapshots().getSnapshot(getAdapterPosition()).getReference().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Post post = documentSnapshot.toObject(Post.class);
                            PostComments.model = post;
                            PostComments.basePostRef = getSnapshots().getSnapshot(getAdapterPosition()).getReference();
                            v.getContext().startActivity(new Intent(v.getContext(), PostComments.class));
                        }
                    });
                }
            });

            tvDisplayName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uid = tvUid.getText().toString();
                    OthersProfileActivity.currentUid = uid.substring(1);
                    v.getContext().startActivity(new Intent(v.getContext(), OthersProfileActivity.class));
                }
            });
        }
    }
}
