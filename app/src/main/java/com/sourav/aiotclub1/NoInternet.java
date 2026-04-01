package com.sourav.aiotclub1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class NoInternet extends AppCompatActivity {

    private final Handler handler = new Handler();
    private final int CHECK_INTERVAL = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_no_internate);



        Button retryButton = findViewById(R.id.retry_button);

        retryButton.setOnClickListener(view -> {
            if (isConnected()) {
                finish();
            }
        });

        handler.postDelayed(checkInternetRunnable, CHECK_INTERVAL);
    }

    private final Runnable checkInternetRunnable = new Runnable() {
        @Override
        public void run() {
            if (isConnected()) {
                finish();
            } else {
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        }
    };

    private boolean isConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(checkInternetRunnable);
    }
}
