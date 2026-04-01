package com.sourav.aiotclub1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class AboutClub extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);


        setContentView(R.layout.activity_about_club);

        ConstraintLayout rootLayout = findViewById(R.id.rootLayout);
        rootLayout.setAlpha(0f);
        rootLayout.animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(200)
                .start();

        findViewById(R.id.chipInstagram).setOnClickListener(v -> openUrl("https://www.instagram.com/android.iot_gietu?utm_source=ig_web_button_share_sheet&igsh=MWZvZDQ1NXkxb2U4Mw=="));

        findViewById(R.id.chipLinkedIn).setOnClickListener(v -> openUrl("https://www.linkedin.com/company/androidandiot/"));

}
    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}