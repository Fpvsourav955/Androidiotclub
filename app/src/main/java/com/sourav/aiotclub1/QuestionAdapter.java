package com.sourav.aiotclub1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private final List<QuestionModel> questionList;
    private final boolean isAdmin;

    public QuestionAdapter(List<QuestionModel> questionList, boolean isAdmin) {
        this.questionList = questionList;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        QuestionModel model = questionList.get(position);

        FirebaseDatabase.getInstance().getReference("answers")
                .child(model.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.messageCount.setText(String.valueOf(snapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        holder.messageCount.setText("0");
                    }
                });
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String currentUID = currentUser.getUid();
        DatabaseReference adminRef = FirebaseDatabase.getInstance()
                .getReference("Admins")
                .child(currentUID);
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAdmin = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));

                if (isAdmin) {
                    holder.deleteButton.setVisibility(View.VISIBLE);
                } else {
                    holder.deleteButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Admin check failed", error.toException());
                holder.deleteButton.setVisibility(View.GONE); // fallback
            }
        });



        String rawUsername = model.getUsername();
        if (rawUsername != null && rawUsername.length() > 15 && rawUsername.matches("^\\d{2}.*\\s.*")) {
            int spaceIndex = rawUsername.indexOf(" ");
            holder.userName.setText(rawUsername.substring(0, spaceIndex) + "\n" + rawUsername.substring(spaceIndex + 1));
        } else {
            holder.userName.setText(rawUsername);
        }

        if (isAdmin) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                String questionId = model.getId();

                DatabaseReference questionRef = FirebaseDatabase.getInstance().getReference("questions").child(questionId);
                DatabaseReference answerRef = FirebaseDatabase.getInstance().getReference("answers").child(questionId);

                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Delete Question")
                        .setMessage("Are you sure you want to delete this question and all its answers?")
                        .setPositiveButton("Delete", (dialog, which) -> questionRef.removeValue().addOnSuccessListener(aVoid -> answerRef.removeValue().addOnSuccessListener(aVoid1 -> {
                            Toast.makeText(holder.itemView.getContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                            int currentPosition = holder.getAdapterPosition();
                            if (currentPosition != RecyclerView.NO_POSITION) {
                                questionList.remove(currentPosition);
                                notifyItemRemoved(currentPosition);
                                notifyItemRangeChanged(currentPosition, questionList.size());
                            }
                        })).addOnFailureListener(e -> Toast.makeText(holder.itemView.getContext(), "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()))
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }


        holder.questionText.setText(model.getQuestion());

        String formattedTime = new SimpleDateFormat("dd MMM, hh:mm a").format(new Date(model.getTimestamp()));
        holder.timeStamp.setText(formattedTime);

        if (model.getProfileImageUrl() != null && !model.getProfileImageUrl().isEmpty()) {
            Glide.with(holder.userImage.getContext())
                    .load(model.getProfileImageUrl())
                    .into(holder.userImage);
        } else {
            holder.userImage.setImageResource(R.drawable.icons8testaccount96);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), QuestionDetailActivity.class);
            intent.putExtra("questionId", model.getId());
            intent.putExtra("questionText", model.getQuestion());
            intent.putExtra("username", model.getUsername());
            intent.putExtra("timestamp", model.getTimestamp());
            intent.putExtra("profileImageUrl", model.getProfileImageUrl());
            v.getContext().startActivity(intent);
        });


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference likeRef = FirebaseDatabase.getInstance()
                .getReference("questions")
                .child(model.getId())
                .child("likes")
                .child(userId);

        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.starButton.setSelected(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        holder.starButton.setOnClickListener(v -> likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    likeRef.removeValue();
                    holder.starButton.setSelected(false);
                } else {
                    likeRef.setValue(true);
                    holder.starButton.setSelected(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(holder.itemView.getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public void updateList(List<QuestionModel> newList) {
        questionList.clear();
        questionList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView userName, timeStamp, questionText, messageCount;
        CircleImageView userImage;
        AppCompatImageView deleteButton, starButton;
        RecyclerView answersRecyclerView;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            timeStamp = itemView.findViewById(R.id.timeStamp);
            questionText = itemView.findViewById(R.id.question_answer);
            userImage = itemView.findViewById(R.id.userImage);
            starButton = itemView.findViewById(R.id.starlike);
            deleteButton = itemView.findViewById(R.id.deleteQuestion);
            messageCount = itemView.findViewById(R.id.messagecount);
            answersRecyclerView = itemView.findViewById(R.id.answersRecyclerView);
        }
    }
}
