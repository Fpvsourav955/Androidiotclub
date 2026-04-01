package com.sourav.aiotclub1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {
    private LinearLayout[] layouts;
    private Handler handler = new Handler();
    private final int ANIMATION_DELAY = 350;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        layouts = new LinearLayout[]{
                findViewById(R.id.layout1),
                findViewById(R.id.layout2),
                findViewById(R.id.layout3),
                findViewById(R.id.layout4),
                findViewById(R.id.layout5),
                findViewById(R.id.layout6)
        };

        animateLayouts(0);
    }

    private void animateLayouts(int index) {
        if (index >= layouts.length) {

            handler.postDelayed(() -> {
                Intent intent = new Intent(Splash.this, WelcomePage.class);
                startActivity(intent);
                finish();
            }, 2000);
            return;
        }

        LinearLayout layout = layouts[index];
        layout.setVisibility(View.VISIBLE);
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        layout.startAnimation(fadeIn);

        handler.postDelayed(() -> animateLayouts(index + 1), ANIMATION_DELAY);
    }
}
