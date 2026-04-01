package com.sourav.aiotclub1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.airbnb.lottie.LottieAnimationView;


public class WelcomePage extends AppCompatActivity {
    private LinearLayout[] layouts;
    private AppCompatButton getstart;
    private TextView textViewAnimated;
    private final String[] words = {"Design", "•", "Build", "•", "Test", "•", "Rebuild", "•", "Repeat"};
    private int charIndex = 0;
    private int wordIndex = 0;
    TextView textView15, aboutClubText;
    String clubTitleText = "Android & IOT Club";
    String aboutClubDesc = "Learn, Build & innovate with our Android & IOT club Community app.";

    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isFirstTimeLaunch()) {

            startActivity(new Intent(WelcomePage.this, MainActivity.class));
            finish();
            return;
        }
        setFirstTimeLaunch(false);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_welcome_page);

        textView15 = findViewById(R.id.textView15);
        aboutClubText = findViewById(R.id.aboutclub);
        textViewAnimated = findViewById(R.id.textViewAnimated);
        getstart=findViewById(R.id.appCompatButton2);
        getstart.setOnClickListener(v->{
            Intent intent = new Intent(WelcomePage.this,MainActivity.class);
            startActivity(intent);
            finish();
        });

        textView15.setText("");
        aboutClubText.setText("");
        textViewAnimated.setText("");

        layouts = new LinearLayout[]{
                findViewById(R.id.layout1),
                findViewById(R.id.layout2),
                findViewById(R.id.layout3),
                findViewById(R.id.layout4),
                findViewById(R.id.layout5),
                findViewById(R.id.layout6)
        };




        animateLayouts(0);
        animateText(textView15, clubTitleText, 60, null);
        animateText(aboutClubText, aboutClubDesc, 40, null);

    }
    private void animateLayouts(int index) {
        if (index >= layouts.length) {
            animateWords();

            return;
        }

        LinearLayout layout = layouts[index];
        layout.setVisibility(View.VISIBLE);
        layout.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));

        handler.postDelayed(() -> animateLayouts(index + 1), 350);
    }

    private void animateWords() {
        textViewAnimated.setVisibility(View.VISIBLE);
        textViewAnimated.setText("");
        wordIndex = 0;
        handler.postDelayed(wordAdder, 0);
    }


    private boolean isFirstTimeLaunch() {
        return getSharedPreferences("MyPrefs", MODE_PRIVATE)
                .getBoolean("isFirstLaunch", true);
    }

    private void setFirstTimeLaunch(boolean isFirstTime) {
        getSharedPreferences("MyPrefs", MODE_PRIVATE)
                .edit()
                .putBoolean("isFirstLaunch", isFirstTime)
                .apply();
    }


    private void animateText(final TextView textView, final String text, int delayPerChar, Runnable onComplete) {
        textView.setText("");
        Handler handler = new Handler();
        for (int i = 0; i <= text.length(); i++) {
            final int finalI = i;
            handler.postDelayed(() -> {
                textView.setText(text.substring(0, finalI));
                if (finalI == text.length() && onComplete != null) {
                    onComplete.run();
                }
            }, i * delayPerChar);
        }
    }

    private final Runnable wordAdder = new Runnable() {
        @Override
        public void run() {
            if (wordIndex < words.length) {
                String currentText = textViewAnimated.getText().toString();
                textViewAnimated.setText(currentText + (currentText.isEmpty() ? "" : " ") + words[wordIndex]);
                wordIndex++;
                handler.postDelayed(this, 400);
            }
        }
    };
}