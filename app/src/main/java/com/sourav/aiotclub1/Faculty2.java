package com.sourav.aiotclub1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class Faculty2 extends AppCompatActivity {
    TextView description;
    ImageView backBtn;
    FirebaseFirestore db;
    ImageView insta, link, git, gmail;
    TextView name;
    private LoadingDialog loadingDialog;
    CircleImageView facultyImg;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_faculty2);

        description = findViewById(R.id.facultytext2);
        name = findViewById(R.id.textView);
        facultyImg = findViewById(R.id.facultyimg2);
        backBtn = findViewById(R.id.backfaculty2);

        insta = findViewById(R.id.souravinsta);
        link = findViewById(R.id.souravlink);
        git = findViewById(R.id.souravgit);
        gmail = findViewById(R.id.souravgmail);

        loadingDialog = new LoadingDialog(this);

        backBtn.setOnClickListener(v -> finish());

        loadingDialog.startLoadingDiloag();
        databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("faculty")
                .child("faculty2");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String profName = snapshot.child("name").getValue(String.class);
                    String profDesc = snapshot.child("description").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    String instagramUrl = snapshot.child("instagram").getValue(String.class);
                    String linkedinUrl = snapshot.child("linkedin").getValue(String.class);
                    String githubUrl = snapshot.child("github").getValue(String.class);
                    String gmailUrl = snapshot.child("gmail").getValue(String.class);

                    if (profName != null) name.setText(profName);
                    if (profDesc != null) description.setText(profDesc);

                    if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                        Glide.with(Faculty2.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.aiotimage2)
                                .into(facultyImg);
                    }

                    insta.setOnClickListener(v -> openUrl(instagramUrl));
                    link.setOnClickListener(v -> openUrl(linkedinUrl));
                    git.setOnClickListener(v -> openUrl(githubUrl));
                    gmail.setOnClickListener(v -> openUrl(gmailUrl));
                }
                loadingDialog.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismissDialog();
                Toast.makeText(Faculty2.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openUrl(String url) {
        if (url != null && !url.trim().isEmpty()) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (Exception e) {
                Toast.makeText(this, "Unable to open link", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Link is not available", Toast.LENGTH_SHORT).show();
        }
    }
}
