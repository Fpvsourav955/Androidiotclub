package com.sourav.aiotclub1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import de.hdodenhof.circleimageview.CircleImageView;

public class DevTeam extends AppCompatActivity {
    ImageView devback;
    CircleImageView anshumanimage,souravimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView souravGit, souravLink, souravInsta;

        ImageView anshumanGit, anshumanLink, anshumanInsta;
        super.onCreate(savedInstanceState);


        EdgeToEdge.enable(this);


        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_dev_team);

        devback =findViewById(R.id.devback);
        devback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(DevTeam.this,MainActivity.class);
                startActivity(intent);

            }
        });
        souravGit = findViewById(R.id.souravgit);
        souravLink = findViewById(R.id.souravlink);
        souravInsta = findViewById(R.id.souravinsta);

        souravimage=findViewById(R.id.souravimage);
        anshumanimage=findViewById(R.id.anshumanimage);

        anshumanGit = findViewById(R.id.anshmangit);
        anshumanLink = findViewById(R.id.anshumanlink);
        anshumanInsta = findViewById(R.id.anshmaninsta);

        souravGit.setOnClickListener(v -> openUrl("https://github.com/Fpvsourav955"));
        souravLink.setOnClickListener(v -> openUrl("https://www.linkedin.com/in/sourav-kumar-pati-aa0833297?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app"));
        souravInsta.setOnClickListener(v -> openUrl("https://www.instagram.com/sourav.pati_?igsh=OGs5OWtza3h2d3F5"));

        anshumanGit.setOnClickListener(v -> openUrl("https://github.com/AnshumanMahanta"));
        anshumanLink.setOnClickListener(v -> openUrl("https://www.linkedin.com/in/anshuman-mahanta?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app"));
        anshumanInsta.setOnClickListener(v -> openUrl("https://www.instagram.com/anshumanv1?igsh=MXB3N2J1c3NvNmU4eQ=="));



    }
    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}