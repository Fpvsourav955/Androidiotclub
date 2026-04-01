package com.sourav.aiotclub1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        holder.commentText.setText(comment.getCommentText());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        String dateStr = sdf.format(new Date(comment.getTimestamp()));
        holder.commentTime.setText(dateStr);

        FirebaseDatabase.getInstance().getReference("users")
                .child(comment.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = snapshot.child("name").getValue(String.class);
                        String profileImage = snapshot.child("profile").getValue(String.class);

                        holder.commentUsername.setText(username != null ? username : "user");

                        if (profileImage != null && !profileImage.isEmpty()) {
                            Glide.with(context)
                                    .load(profileImage)
                                    .into(holder.commentProfileImage);
                        } else {
                            holder.commentProfileImage.setImageResource(R.drawable.icons8testaccount96);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView commentProfileImage;
        TextView commentUsername, commentText, commentTime;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentProfileImage = itemView.findViewById(R.id.commentProfileImage);
            commentUsername = itemView.findViewById(R.id.commentUsername);
            commentText = itemView.findViewById(R.id.commentText);
            commentTime = itemView.findViewById(R.id.commentTime);
        }
    }
}
