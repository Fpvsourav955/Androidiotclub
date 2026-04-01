package com.sourav.aiotclub1;

import android.os.Bundle;
import android.view.Window;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton buttonDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawerLayout = findViewById(R.id.drawerLayout);
        EdgeToEdge.enable(this);


        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);

        if (buttonDrawerToggle != null) {
            buttonDrawerToggle.setOnClickListener(v -> {
                if (drawerLayout != null) {
                    drawerLayout.open();
                }
            });
        }
    }
}
