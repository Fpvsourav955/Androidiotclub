package com.sourav.aiotclub1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {

    private Context context;

    private  LoadingDialog loadingDialog;
    private List<AnswerModel> answerList;
    private DatabaseReference answersRef;


    public AnswerAdapter(Context context, List<AnswerModel> answerList) {
        this.context = context;
        this.answerList = answerList;
        this.answersRef = FirebaseDatabase.getInstance().getReference("answers");
        loadingDialog = new LoadingDialog((Activity) context);
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.answer_item, parent, false);
        return new AnswerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        AnswerModel answer = answerList.get(position);



        holder.userName.setText(answer.getUserName());
        holder.answerText.setText(answer.getAnswerText());


        holder.googletranstate.setOnClickListener(v -> {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.translate");
            if (intent != null) {
                // Google Translate app is installed, launch it
                context.startActivity(intent);
            } else {
                // App not installed, open Play Store page
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=com.google.android.apps.translate")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    // Play Store app not found, open in browser
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.translate")));
                }
            }
        });

        String profileUrl = answer.getProfileImageUrl();
        if (profileUrl != null && !profileUrl.isEmpty()) {
            Glide.with(context).load(profileUrl).into(holder.profileImage);
        } else {
            holder.profileImage.setImageResource(R.drawable.icons8testaccount96);
        }
        holder.replybtn.setOnClickListener(v -> {

            Intent intent = new Intent(context, ReplyActivity.class);
            // Pass data via intent extras
            intent.putExtra("answerId", answer.getAnswerId());
            intent.putExtra("answerText", answer.getAnswerText());
            intent.putExtra("username", answer.getUserName());
            intent.putExtra("profileImageUrl", answer.getProfileImageUrl());
            intent.putExtra("timestamp", answer.getTimestamp());
            intent.putExtra("likeCount", holder.likes.getText().toString());
            intent.putExtra("dislikeCount", holder.unlikes.getText().toString());
            context.startActivity(intent);
        });

        holder.report.setOnClickListener(v -> {

            String[] reportOptions = {"Uninformative", "Irrelevant", "Offensive", "Misleading"};


            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
            builder.setTitle("Report Answer")
                    .setItems(reportOptions, (dialog, which) -> {

                        String selectedReport = reportOptions[which];
                        submitReportToFirebase(answer.getAnswerId(), selectedReport);
                        Toast.makeText(context, "Reported as " + selectedReport, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


        holder.time.setText(DateFormat.format("dd MMM yyyy hh:mm a", new Date(answer.getTimestamp())));

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String answerId = answer.getAnswerId();

        if (answerId == null) return;

        DatabaseReference likesRef = answersRef.child(answerId).child("likes");
        DatabaseReference dislikesRef = answersRef.child(answerId).child("dislikes");

        String rawUsername = answer.getUserName();
        if (rawUsername != null && rawUsername.length() > 15 && rawUsername.matches("^\\d{2}.*\\s.*")) {
            int spaceIndex = rawUsername.indexOf(" ");
            if (spaceIndex != -1) {
                String firstPart = rawUsername.substring(0, spaceIndex);
                String secondPart = rawUsername.substring(spaceIndex + 1);
                holder.userName.setText(firstPart + "\n" + secondPart);
            } else {
                holder.userName.setText(rawUsername); // fallback
            }
        } else {
            holder.userName.setText(rawUsername);
        }


        holder.answerText.setText(answer.getAnswerText());

        loadingDialog.startLoadingDiloag();
        likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot likesSnapshot) {
                int likeCount = (int) likesSnapshot.getChildrenCount();
                holder.likes.setText(String.valueOf(likeCount));
                loadingDialog.dismissDialog();

                dislikesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dislikesSnapshot) {
                        int dislikeCount = (int) dislikesSnapshot.getChildrenCount();
                        holder.unlikes.setText(String.valueOf(dislikeCount));

                        boolean userLiked = likesSnapshot.hasChild(userId);
                        boolean userDisliked = dislikesSnapshot.hasChild(userId);

                        updateReactionButtons(holder.likeBtn, holder.dislikeBtn, userLiked, userDisliked);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingDialog.dismissDialog();
                        Log.e("AnswerAdapter", "Error reading dislikes: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AnswerAdapter", "Error reading likes: " + error.getMessage());
            }
        });

        holder.likeBtn.setOnClickListener(v -> {
            likesRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot likeSnapshot) {
                    dislikesRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dislikeSnapshot) {
                            if (likeSnapshot.exists() || dislikeSnapshot.exists()) {
                                Toast.makeText(context, "You already reacted to this answer.", Toast.LENGTH_SHORT).show();
                            } else {
                                likesRef.child(userId).setValue(true).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Liked!", Toast.LENGTH_SHORT).show();
                                        int currentLikes = Integer.parseInt(holder.likes.getText().toString());
                                        holder.likes.setText(String.valueOf(currentLikes + 1));


                                        holder.likeBtn.setSelected(true);
                                        holder.dislikeBtn.setSelected(false);
                                    } else {
                                        Toast.makeText(context, "Failed to like.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Database error.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Database error.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.dislikeBtn.setOnClickListener(v -> {
            dislikesRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dislikeSnapshot) {
                    likesRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot likeSnapshot) {
                            if (dislikeSnapshot.exists() || likeSnapshot.exists()) {
                                Toast.makeText(context, "You already reacted to this answer.", Toast.LENGTH_SHORT).show();
                            } else {
                                dislikesRef.child(userId).setValue(true).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Disliked!", Toast.LENGTH_SHORT).show();
                                        int currentDislikes = Integer.parseInt(holder.unlikes.getText().toString());
                                        holder.unlikes.setText(String.valueOf(currentDislikes + 1));

                                        holder.dislikeBtn.setSelected(true);
                                        holder.likeBtn.setSelected(false);
                                    } else {
                                        Toast.makeText(context, "Failed to dislike.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Database error.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Database error.", Toast.LENGTH_SHORT).show();
                }
            });
        });


    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }
    private void updateReactionButtons(ImageView likeBtn, ImageView dislikeBtn, boolean liked, boolean disliked) {
        if (liked) {
            likeBtn.setSelected(true);
            dislikeBtn.setSelected(false);
            likeBtn.setEnabled(true);
            dislikeBtn.setEnabled(true);
        } else if (disliked) {
            dislikeBtn.setSelected(true);
            likeBtn.setSelected(false);
            likeBtn.setEnabled(true);
            dislikeBtn.setEnabled(true);
        } else {
            likeBtn.setSelected(false);
            dislikeBtn.setSelected(false);
            likeBtn.setEnabled(true);
            dislikeBtn.setEnabled(true);
        }
    }
    private void submitReportToFirebase(String answerId, String reportType) {
        if (answerId == null) return;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DatabaseReference reportRef = answersRef.child(answerId).child("reports").child(userId);

        reportRef.setValue(new Report(reportType, System.currentTimeMillis(), userId))
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(context, "Failed to send report.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class Report {
        public String type;
        public long timestamp;
        public String reporterId;

        public Report() { }
        public Report(String type, long timestamp, String reporterId) {
            this.type = type;
            this.timestamp = timestamp;
            this.reporterId = reporterId;
        }
    }

    public static class AnswerViewHolder extends RecyclerView.ViewHolder {
        TextView userName, answerText, time, likes, unlikes;
        ImageView profileImage, likeBtn, dislikeBtn,  replybtn,googletranstate,report;


        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            report=itemView.findViewById(R.id.report);
            replybtn = itemView.findViewById(R.id.reply);
            answerText = itemView.findViewById(R.id.answerview);
            googletranstate=itemView.findViewById(R.id.googletranstate);
            profileImage = itemView.findViewById(R.id.userImage);
            time = itemView.findViewById(R.id.timeStamp);
            likes = itemView.findViewById(R.id.totalLikesTextView);
            unlikes = itemView.findViewById(R.id.totalUnlikesTextView);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            dislikeBtn = itemView.findViewById(R.id.dislikebtn);
        }
    }
}