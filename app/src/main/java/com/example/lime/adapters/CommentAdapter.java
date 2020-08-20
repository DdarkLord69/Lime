package com.example.lime.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lime.EditPost;
import com.example.lime.R;
import com.example.lime.models.Comment;
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
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.Day;
import org.ocpsoft.prettytime.units.Hour;
import org.ocpsoft.prettytime.units.Minute;
import org.ocpsoft.prettytime.units.Month;
import org.ocpsoft.prettytime.units.Week;
import org.ocpsoft.prettytime.units.Year;

import java.util.List;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentHolder> {

    public CommentAdapter(@NonNull FirestoreRecyclerOptions<Comment> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final CommentHolder holder, final int position, @NonNull Comment model) {

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
        holder.tvUid.setText("@" + model.getUserID());
        holder.tvContent.setText(model.getCommentContent());
        holder.tvLikes.setText(String.valueOf(model.getLikedBy().size()));
        if(model.getLikedBy().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            holder.btnLike.setBackgroundResource(R.drawable.ic_liked);
        } else {
            holder.btnLike.setBackgroundResource(R.drawable.ic_unliked);
        }
        if("moments ago".equals(p.format(model.getTimestamp()))) {
            holder.tvTimeSincePosted.setText("now");
        } else {
            holder.tvTimeSincePosted.setText(p.format(model.getTimestamp()));
        }

        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(model.getUserID())) {
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
                                    EditPost.whichContent = "commentContent";
                                    v.getContext().startActivity(new Intent(v.getContext(), EditPost.class));

                                    return true;
                                case R.id.delete_post:
                                    getSnapshots().getSnapshot(position).getReference().getParent().getParent().update("comments", FieldValue.increment(-1));
                                    getSnapshots().getSnapshot(position).getReference().delete();
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
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);

        return new CommentHolder(v);
    }

    class CommentHolder extends RecyclerView.ViewHolder {
        TextView tvDisplayName;
        TextView tvUid;
        TextView tvContent;
        TextView tvTimeSincePosted;
        TextView tvLikes;
        ImageView ivProfilePic;
        Button btnLike;
        Button btnOptionsMenu;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            tvDisplayName = itemView.findViewById(R.id.tvDisplayName);
            tvUid = itemView.findViewById(R.id.tvUid);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTimeSincePosted = itemView.findViewById(R.id.tvTimeSincePosted);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnOptionsMenu = itemView.findViewById(R.id.btnOptionsMenu);

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DocumentReference postRef = getSnapshots().getSnapshot(getAdapterPosition()).getReference();
                    final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    postRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            List<String> likedBy = documentSnapshot.toObject(Comment.class).getLikedBy();
                            if(likedBy.contains(uid)) {
                                postRef.update("likedBy", FieldValue.arrayRemove(uid));
                            } else {
                                postRef.update("likedBy", FieldValue.arrayUnion(uid));
                            }
                        }
                    });
                }
            });
        }
    }
}
