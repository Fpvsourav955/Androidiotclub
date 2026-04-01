package com.sourav.aiotclub1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Date;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;

import java.util.Locale;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {

    private List<ReplyModel> replyList;

    public ReplyAdapter(List<ReplyModel> replyList) {
        this.replyList = replyList;
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView username, replyText, timestamp;
        ImageView profileImage;

        public ReplyViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.replyUserName);
            replyText = itemView.findViewById(R.id.replyText);
            profileImage = itemView.findViewById(R.id.replyProfileImage);
            timestamp = itemView.findViewById(R.id.replyTimestamp);
        }
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reply, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        ReplyModel reply = replyList.get(position);
        holder.username.setText(reply.userName);
        holder.replyText.setText(reply.replyText);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());
        holder.timestamp.setText(sdf.format(new Date(reply.timestamp)));

        Glide.with(holder.profileImage.getContext())
                .load(reply.profileImageUrl)
                .into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }
}
