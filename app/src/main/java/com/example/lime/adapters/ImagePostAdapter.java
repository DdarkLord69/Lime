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
import com.example.lime.R;
import com.example.lime.models.ImagePost;
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

public class ImagePostAdapter extends FirestoreRecyclerAdapter<ImagePost, ImagePostAdapter.ImagePostHolder> {

    public ImagePostAdapter(@NonNull FirestoreRecyclerOptions<ImagePost> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ImagePostHolder holder, final int position, @NonNull ImagePost model) {

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
        Picasso.get().load(model.getPostImageUrl()).into(holder.ivContent);
        holder.tvCaption.setText(model.getCaption());
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

        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(model.getUserId())) {
            holder.btnOptionsMenu.setVisibility(View.VISIBLE);
            holder.btnOptionsMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), holder.btnOptionsMenu);
                    popupMenu.inflate(R.menu.popup_options_menu_two);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete_post:
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
    public ImagePostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);

        return new ImagePostHolder(v);
    }

    public class ImagePostHolder extends RecyclerView.ViewHolder {
        TextView tvDisplayName;
        TextView tvUid;
        ImageView ivContent;
        TextView tvCaption;
        TextView tvTimeSincePosted;
        TextView tvLikes;
        ImageView ivProfilePic;
        Button btnLike;
        Button btnOptionsMenu;

        public ImagePostHolder(@NonNull View itemView) {
            super(itemView);

            tvDisplayName = itemView.findViewById(R.id.tvDisplayName);
            tvUid = itemView.findViewById(R.id.tvUid);
            ivContent = itemView.findViewById(R.id.ivContent);
            tvCaption = itemView.findViewById(R.id.tvContent);
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
        }
    }
}
