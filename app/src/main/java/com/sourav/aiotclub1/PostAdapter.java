package com.sourav.aiotclub1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final Context context;
    private final List<Post> postList;
    private boolean isSharing = false;
    private final LoadingDialog loadingDialog;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        this.loadingDialog = new LoadingDialog((Activity) context);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.description.setText(post.getDescription());
        holder.profileName.setText(post.getProfileName());

        Glide.with(context).load(post.getMediaUrl()).into(holder.mediaImage);
        Glide.with(context).load(post.getProfileImage()).into(holder.profileImage);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String currentUserId = currentUser.getUid();
        String postId = post.getPostId();
        String tags = post.getTags();
        holder.tagcount.setText(tags != null ? tags : "");

        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("posts");

        holder.likeSelector.setOnClickListener(v -> {
            DatabaseReference likeRef = likesRef.child(postId).child("likes").child(currentUserId);

            likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        likeRef.removeValue();
                        holder.likeSelector.setSelected(false);
                    } else {
                        likeRef.setValue(true);
                        holder.likeSelector.setSelected(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });

        likesRef.child(postId).child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                holder.likeCount.setText(String.valueOf(count));

                if (snapshot.hasChild(currentUserId)) {
                    holder.likeSelector.setImageResource(R.drawable.icons8filledheart100);
                } else {
                    holder.likeSelector.setImageResource(R.drawable.icons8heart100);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        holder.tagcount.setText(post.getTags() != null ? post.getTags() : "");
        holder.commentButton.setOnClickListener(v -> {
            if (holder.commentLayout.getVisibility() == View.VISIBLE) {
                holder.commentLayout.setVisibility(View.GONE);
            } else {
                holder.commentLayout.setVisibility(View.VISIBLE);
                loadComments(postId, holder);
            }
        });

        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("comments");

        commentRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.commentCounter.setText(snapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        holder.share.setOnClickListener(v -> {
            String description = post.getDescription();
            String postLink = post.getPostLink();
            String mediaUrl = post.getMediaUrl();

            if (mediaUrl == null || mediaUrl.isEmpty()) {

                shareTextOnly(description, postLink);
            } else {
                shareImageAndText(mediaUrl, description, postLink);
            }
        });

        holder.sendCommentButton.setOnClickListener(v -> {
            String commentText = holder.commentEditText.getText().toString().trim();
            if (!commentText.isEmpty()) {
                String commentId = commentRef.push().getKey();
                if (commentId != null) {
                    Comment comment = new Comment(currentUserId, commentText, System.currentTimeMillis());
                    commentRef.child(commentId).setValue(comment).addOnSuccessListener(unused -> holder.commentEditText.setText(""));
                }
            }
        });
        DatabaseReference adminsRef = FirebaseDatabase.getInstance().getReference().child("Admins");
        adminsRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && Boolean.TRUE.equals(snapshot.getValue(Boolean.class))) {
                    holder.menuDelete.setVisibility(View.VISIBLE);
                } else {
                    holder.menuDelete.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.menuDelete.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            new android.app.AlertDialog.Builder(context)
                    .setTitle("Delete Post")
                    .setMessage("Are you sure you want to delete this post?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Post postToDelete = postList.get(adapterPosition);
                        String postIdToDelete = postToDelete.getPostId();

                        FirebaseDatabase.getInstance().getReference()
                                .child("posts")
                                .child(postIdToDelete)
                                .removeValue()
                                .addOnSuccessListener(unused -> {
                                    String mediaUrl = postToDelete.getMediaUrl();
                                    if (mediaUrl != null && !mediaUrl.isEmpty()) {
                                        FirebaseStorage.getInstance().getReferenceFromUrl(mediaUrl).delete();
                                    }
                                    postList.remove(adapterPosition);
                                    notifyItemRemoved(adapterPosition);
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        holder.postDate.setText(sdf.format(new Date(post.getTimestamp())));
    }
    private void shareTextOnly(String description, String link) {
        String appLink = "https://play.google.com/store/apps/details?id=com.sourav.aiotclub1";
        String shareText = description + "\n" + link + "\n\nDownload our app: " + appLink;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        context.startActivity(Intent.createChooser(shareIntent, "Share post via"));
    }


    private void shareImageAndText(String imageUrl, String description, String link) {
        if (isSharing) return;
        isSharing = true;
        loadingDialog.startLoadingDiloag();


        new android.os.Handler().postDelayed(() -> {
            Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource,
                                                    @Nullable Transition<? super Bitmap> transition) {
                            try {
                                File cachePath = new File(context.getCacheDir(), "shared_images");
                                if (!cachePath.exists()) {
                                    cachePath.mkdirs();
                                }

                                File imageFile = new File(cachePath, "shared_image_" + System.currentTimeMillis() + ".png");
                                FileOutputStream stream = new FileOutputStream(imageFile);
                                resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                stream.flush();
                                stream.close();

                                Uri contentUri = FileProvider.getUriForFile(
                                        context,
                                        context.getPackageName() + ".fileprovider",
                                        imageFile
                                );

                                if (contentUri != null) {
                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                    shareIntent.setType("image/*");
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                                    String appLink = "https://play.google.com/store/apps/details?id=com.sourav.aiotclub1";
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, description + "\n" + link + "\n\nDownload our app: " + appLink);

                                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                    loadingDialog.dismissDialog();
                                    isSharing = false;
                                    context.startActivity(Intent.createChooser(shareIntent, "Share post via"));
                                } else {
                                    throw new Exception("Content URI is null");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                loadingDialog.dismissDialog();
                                isSharing = false;
                                Toast.makeText(context, "Share failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            loadingDialog.dismissDialog();
                            isSharing = false;
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            loadingDialog.dismissDialog();
                            isSharing = false;
                            Toast.makeText(context, "Image load failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }, 200);
    }




    private void loadComments(String postId, PostViewHolder holder) {
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("posts").child(postId).child("comments");
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Comment> commentList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Comment comment = ds.getValue(Comment.class);
                    if (comment != null) {
                        commentList.add(comment);
                    }
                }
                CommentAdapter adapter = new CommentAdapter(context, commentList);
                holder.commentRecycler.setLayoutManager(new LinearLayoutManager(context));
                holder.commentRecycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView mediaImage, profileImage, likeSelector, menuDelete;
        TextView description, profileName, postDate, likeCount, tagcount, commentCounter;
        LinearLayout commentLayout;
        EditText commentEditText;
        AppCompatImageView sendCommentButton, commentButton,share;
        RecyclerView commentRecycler;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            tagcount = itemView.findViewById(R.id.tags);
            menuDelete = itemView.findViewById(R.id.menudelete);
            mediaImage = itemView.findViewById(R.id.postImage);
            profileImage = itemView.findViewById(R.id.profileImage);
            description = itemView.findViewById(R.id.postDescription);
            profileName = itemView.findViewById(R.id.profileName);
            postDate = itemView.findViewById(R.id.postDate);
            likeSelector = itemView.findViewById(R.id.likeselector);
            likeCount = itemView.findViewById(R.id.likecounter);
            share = itemView.findViewById(R.id.share);
            commentButton = itemView.findViewById(R.id.commentButton);
            commentCounter = itemView.findViewById(R.id.commentcounter);
            commentLayout = itemView.findViewById(R.id.commentLayout);
            commentEditText = itemView.findViewById(R.id.commentEditText);
            sendCommentButton = itemView.findViewById(R.id.sendCommentButton);
            commentRecycler = itemView.findViewById(R.id.commentRecycler);
        }
    }
}
