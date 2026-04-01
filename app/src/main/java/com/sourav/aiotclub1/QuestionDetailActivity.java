package com.sourav.aiotclub1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class QuestionDetailActivity extends AppCompatActivity {
    private LoadingDialog loadingDialog;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_question_detail);

        AppCompatImageView sendButton = findViewById(R.id.sendAnswerButton);
        EditText answerInput = findViewById(R.id.answerEditText);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        loadingDialog = new LoadingDialog(QuestionDetailActivity.this);
        AppCompatImageView googleTranslateBtn = findViewById(R.id.googletranstate);

        googleTranslateBtn.setOnClickListener(v -> {
            Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.translate");
            if (intent != null) {

                startActivity(intent);
            } else {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.translate")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.translate")));
                }
            }
        });

        String questionId = getIntent().getStringExtra("questionId");
        assert questionId != null;
        DatabaseReference answersRef = database.getReference("answers").child(questionId);


        String question = getIntent().getStringExtra("questionText");
        String username = getIntent().getStringExtra("username");
        long timestamp = getIntent().getLongExtra("timestamp", 0);
        String profileImageUrl = getIntent().getStringExtra("profileImageUrl");


        TextView questionText = findViewById(R.id.questionText);
        TextView askedBy = findViewById(R.id.askedBy);
        TextView questionDate = findViewById(R.id.questionDate);
        CircleImageView askedImage = findViewById(R.id.askedImage);
        findViewById(R.id.devback).setOnClickListener(v -> finish());
        RecyclerView recyclerView = findViewById(R.id.answersRecyclerView);
        List<AnswerModel> answerList = new ArrayList<>();
        AnswerAdapter adapter = new AnswerAdapter(this, answerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        loadingDialog.startLoadingDiloag();

        answersRef.addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingDialog.dismissDialog();
                answerList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    AnswerModel answer = ds.getValue(AnswerModel.class);
                    if (answer != null) {
                        answer.setAnswerId(ds.getKey());
                        answerList.add(answer);
                    }
                }
                adapter.notifyDataSetChanged();
                loadingDialog.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismissDialog();

            }

        });



        questionText.setText(question);
        if (username != null && username.length() > 15 && username.matches("^\\d{2}.*\\s.*")) {
            int spaceIndex = username.indexOf(" ");
            if (spaceIndex != -1) {
                String firstPart = username.substring(0, spaceIndex);
                String secondPart = username.substring(spaceIndex + 1);
                askedBy.setText(firstPart + "\n" + secondPart);
            } else {
                askedBy.setText(username);
            }
        } else {
            askedBy.setText(username);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());
        String formattedDate = "• " + sdf.format(new Date(timestamp));
        questionDate.setText(formattedDate);



        sendButton.setOnClickListener(v -> {
            String answerText = answerInput.getText().toString().trim();
            if (!answerText.isEmpty()) {

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    String currentUsername = currentUser.getDisplayName();
                    if (currentUsername == null || currentUsername.isEmpty()) {
                        currentUsername = currentUser.getEmail(); // fallback to email
                    }

                    String currentProfileImage = (currentUser.getPhotoUrl() != null) ? currentUser.getPhotoUrl().toString() : "";

                    long currentTime = System.currentTimeMillis();
                    DatabaseReference newAnswerRef = answersRef.push();
                    String answerId = newAnswerRef.getKey();

                    AnswerModel answer = new AnswerModel(answerText, currentUsername, currentProfileImage, currentTime);
                    answer.setAnswerId(answerId);

                    newAnswerRef.setValue(answer)
                            .addOnSuccessListener(aVoid -> answerInput.setText(""))
                            .addOnFailureListener(Throwable::printStackTrace);
                } else {
                    Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                }
            }
        });

        assert profileImageUrl != null;
        if (!profileImageUrl.isEmpty()) {
            Glide.with(this).load(profileImageUrl).into(askedImage);
        }



    }
}