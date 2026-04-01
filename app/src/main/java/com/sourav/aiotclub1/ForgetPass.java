package com.sourav.aiotclub1;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgetPass extends AppCompatActivity {
    LottieAnimationView signin_animation;
    TextView loginsignup,signintext;
    RelativeLayout signinlayout;
    private  EditText emailEditText;
    private FirebaseAuth mAuth;
    public  static  final  int TIMER=2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        signinlayout =findViewById(R.id.sign_in);
        signintext =findViewById(R.id.sign_in_text);
        signin_animation=findViewById(R.id.signin_animation);

        emailEditText = findViewById(R.id.emailEditText);
        mAuth = FirebaseAuth.getInstance();


        signinlayout.setOnClickListener(view -> {
            signin_animation.setVisibility(View.VISIBLE);
            signin_animation.playAnimation();
            signintext.setVisibility(View.GONE);
            new Handler().postDelayed(this::resetButton, TIMER);

            String email = emailEditText.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Reset email sent. Check your inbox.", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        });

    }
        private void resetButton() {
            signin_animation.pauseAnimation();
            signin_animation.setVisibility(View.GONE);
            signintext.setVisibility(View.VISIBLE);
        }
}