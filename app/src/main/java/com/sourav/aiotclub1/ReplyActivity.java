package com.sourav.aiotclub1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReplyActivity extends AppCompatActivity {

    private int likeCount = 0;
    private int dislikeCount = 0;
    private RecyclerView replyRecyclerView;
    private ReplyAdapter replyAdapter;
    private List<ReplyModel> replyList = new ArrayList<>();
    private AppCompatImageView likeBtn;
    private AppCompatImageView dislikeBtn;
    private TextView likeCountTextView;
    private TextView dislikeCountTextView;

    private EditText replyInput;
    private AppCompatButton sendBtn;
    private DatabaseReference repliesRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_reply);

        replyInput = findViewById(R.id.replyInput);
        sendBtn = findViewById(R.id.sendBtn);
        replyRecyclerView = findViewById(R.id.replyRecyclerView);
        replyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        replyAdapter = new ReplyAdapter(replyList);
        replyRecyclerView.setAdapter(replyAdapter);



        Intent intent = getIntent();
        String answerId = intent.getStringExtra("answerId");
        String likeCountStr = intent.getStringExtra("likeCount");
        String dislikeCountStr = intent.getStringExtra("dislikeCount");
        String answerText = intent.getStringExtra("answerText");
        String username = intent.getStringExtra("username");
        String profileImageUrl = intent.getStringExtra("profileImageUrl");
        long timestamp = intent.getLongExtra("timestamp", 0);
        repliesRef = FirebaseDatabase.getInstance()
                .getReference("answers")
                .child(answerId)
                .child("replies");

        loadReplies();
        TextView answerTextView = findViewById(R.id.answerview);
        likeBtn = findViewById(R.id.likeBtn);
        dislikeBtn = findViewById(R.id.dislikebtn);
        likeCountTextView = findViewById(R.id.totalLikesTextView);
        dislikeCountTextView = findViewById(R.id.totalUnlikesTextView);
        TextView usernameTextView = findViewById(R.id.userName);
        ImageView profileImageView = findViewById(R.id.userImage);
        TextView timestampView = findViewById(R.id.timeStamp);


        try {
            if (likeCountStr != null) likeCount = Integer.parseInt(likeCountStr);
            if (dislikeCountStr != null) dislikeCount = Integer.parseInt(dislikeCountStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            likeCount = 0;
            dislikeCount = 0;
        }


        repliesRef = FirebaseDatabase.getInstance()
                .getReference("answers")
                .child(answerId)
                .child("replies");


        sendBtn.setOnClickListener(v -> submitReply());


        likeCountTextView.setText(String.valueOf(likeCount));
        dislikeCountTextView.setText(String.valueOf(dislikeCount));
        answerTextView.setText(answerText != null ? answerText : "");
        usernameTextView.setText(username != null ? username : "");
        Glide.with(this).load(profileImageUrl).into(profileImageView);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());
        if (timestamp != 0) {
            timestampView.setText(sdf.format(new Date(timestamp)));
        } else {
            timestampView.setText("");
        }


        likeBtn.setSelected(false);
        dislikeBtn.setSelected(false);
        likeBtn.setOnClickListener(v -> handleLikeClick());
        dislikeBtn.setOnClickListener(v -> handleDislikeClick());
    }

    private void handleLikeClick() {
        boolean liked = likeBtn.isSelected();
        boolean disliked = dislikeBtn.isSelected();

        if (liked) {
            likeCount = Math.max(0, likeCount - 1);
            likeBtn.setSelected(false);
        } else {
            likeCount++;
            likeBtn.setSelected(true);
            if (disliked) {
                dislikeCount = Math.max(0, dislikeCount - 1);
                dislikeBtn.setSelected(false);
            }
        }
        updateCounts();
    }

    private void handleDislikeClick() {
        boolean disliked = dislikeBtn.isSelected();
        boolean liked = likeBtn.isSelected();

        if (disliked) {
            dislikeCount = Math.max(0, dislikeCount - 1);
            dislikeBtn.setSelected(false);
        } else {
            dislikeCount++;
            dislikeBtn.setSelected(true);
            if (liked) {
                likeCount = Math.max(0, likeCount - 1);
                likeBtn.setSelected(false);
            }
        }
        updateCounts();
    }

    private void updateCounts() {
        likeCountTextView.setText(String.valueOf(likeCount));
        dislikeCountTextView.setText(String.valueOf(dislikeCount));
    }

    private void submitReply() {
        String replyText = replyInput.getText().toString().trim();
        if (replyText.isEmpty()) {
            replyInput.setError("Please write a reply");
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String profileImageUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()
                : "";

        String replyId = repliesRef.push().getKey();
        long timestamp = System.currentTimeMillis();

        ReplyModel reply = new ReplyModel(userId, userName, profileImageUrl, replyText, timestamp);

        repliesRef.child(replyId).setValue(reply)
                .addOnSuccessListener(aVoid -> {
                    replyInput.setText("");
                    Toast.makeText(this, "Reply sent!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send reply.", Toast.LENGTH_SHORT).show();
                });
    }
    private void loadReplies() {
        repliesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                replyList.clear();
                for (DataSnapshot replySnap : snapshot.getChildren()) {
                    ReplyModel reply = replySnap.getValue(ReplyModel.class);
                    if (reply != null) {
                        replyList.add(reply);
                    }
                }
                replyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReplyActivity.this, "Failed to load replies", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
