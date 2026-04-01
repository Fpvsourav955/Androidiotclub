package com.sourav.aiotclub1;

import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Question extends AppCompatActivity {

    private EditText questionInput;
    private Button sendButton;
    private FirebaseAuth mAuth;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_question);


        questionInput = findViewById(R.id.questionInput);
        sendButton = findViewById(R.id.sendButton);

        mAuth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog(Question.this);

        sendButton.setOnClickListener(v -> {
            loadingDialog.startLoadingDiloag();
            submitQuestion();
        });
    }

    private void submitQuestion() {
        String questionText = questionInput.getText().toString().trim();

        if (questionText.isEmpty()) {
            loadingDialog.dismissDialog();
            Toast.makeText(this, "Please enter a question", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            loadingDialog.dismissDialog();
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = currentUser.getDisplayName();
        String profileImageUrl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : "";

        Map<String, Object> questionData = new HashMap<>();
        questionData.put("question", questionText);
        questionData.put("username", username);
        questionData.put("profileImageUrl", profileImageUrl);
        questionData.put("timestamp", System.currentTimeMillis());

        FirebaseDatabase.getInstance().getReference("questions")
                .push()
                .setValue(questionData)
                .addOnSuccessListener(aVoid -> {
                    loadingDialog.dismissDialog();
                    Toast.makeText(this, "Question submitted!", Toast.LENGTH_SHORT).show();
                    questionInput.setText("");
                    finish();
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismissDialog();
                    Toast.makeText(this, "Failed to submit: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
