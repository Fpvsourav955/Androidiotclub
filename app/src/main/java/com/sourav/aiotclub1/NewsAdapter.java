package com.sourav.aiotclub1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final List<News> newsList;
    private final Context context;
    private boolean isDeleteMode = false;
    public NewsAdapter(List<News> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        News news = newsList.get(position);

        String formattedTitle = formatTitleWithLineBreaks(news.getTitle(), 4);
        holder.title.setText(formattedTitle);

        holder.time.setText(news.getFormattedTime());



        Glide.with(context)
                .load(news.getImageUrl())
                .into(holder.image);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            holder.likeBtn.setEnabled(false);
            return;
        }

        if (isDeleteMode) {
            holder.deleteBtn.setVisibility(View.VISIBLE);
        } else {
            holder.deleteBtn.setVisibility(View.GONE);
        }

        String userId = currentUser.getUid();
        String newsKey = news.getNewsKey();
        if (newsKey == null) {

            holder.likeBtn.setEnabled(false);
            return;
        }

        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("likes")
                .child(newsKey)
                .child(userId);

        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean liked = snapshot.exists();
                holder.likeBtn.setSelected(liked);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NewsDetailActivity.class);
            intent.putExtra("title", news.getTitle());
            intent.putExtra("description", news.getDescription());
            intent.putExtra("timestamp", news.getTimestamp());
            intent.putExtra("imageUrl", news.getImageUrl());
            intent.putExtra("newsKey", news.getNewsKey());
            context.startActivity(intent);
        });
        holder.deleteBtn.setOnClickListener(v -> {
            if (deleteListener != null) {

                String imagePath = news.getImagePath();
                if (imagePath != null && !imagePath.isEmpty()) {
                    StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(imagePath);
                    imageRef.delete().addOnSuccessListener(aVoid -> {

                        deleteListener.onDeleteClick(news, position);
                    }).addOnFailureListener(e -> {

                        deleteListener.onDeleteClick(news, position); // Optional: proceed even if image deletion fails
                    });
                } else {
                    deleteListener.onDeleteClick(news, position);
                }

            }
        });



        holder.likeBtn.setOnClickListener(v -> {
            boolean isSelected = holder.likeBtn.isSelected();
            if (isSelected) {
                likeRef.removeValue();
                holder.likeBtn.setSelected(false);
            } else {
                likeRef.setValue(true);
                holder.likeBtn.setSelected(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title, time;
        ImageView image, likeBtn;
        ImageButton deleteBtn;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.newsTitle);
            time = itemView.findViewById(R.id.newsTime);
            image = itemView.findViewById(R.id.newsImage);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
    public void setDeleteMode(boolean deleteMode) {
        this.isDeleteMode = deleteMode;
        notifyDataSetChanged();
    }
    public interface OnNewsDeleteClickListener {
        void onDeleteClick(News news, int position);
    }

    private OnNewsDeleteClickListener deleteListener;

    public void setOnNewsDeleteClickListener(OnNewsDeleteClickListener listener) {
        this.deleteListener = listener;
    }



    private String formatTitleWithLineBreaks(String input, int wordsPerLine) {
        String[] words = input.trim().split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            builder.append(words[i]);
            if ((i + 1) % wordsPerLine == 0 && i != words.length - 1) {
                builder.append("\n");
            } else if (i != words.length - 1) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }
}
