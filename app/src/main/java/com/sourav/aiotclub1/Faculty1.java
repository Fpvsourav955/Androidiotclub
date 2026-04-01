package com.sourav.aiotclub1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class Faculty1 extends AppCompatActivity {
    TextView anDescription;
    ImageView imag1;
    FirebaseFirestore db;
    ImageView insta, link, git, gmail;
    TextView name, description;
    private  LoadingDialog loadingDialog;
    CircleImageView facultyImg;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_faculty1);


        loadingDialog=new LoadingDialog(this);


        name = findViewById(R.id.textView);
        description = findViewById(R.id.facultydescription);
        facultyImg = findViewById(R.id.facultyimg1);
        insta = findViewById(R.id.facultyinsta);
        link = findViewById(R.id.facultylink);
        git = findViewById(R.id.facultygit);
        gmail = findViewById(R.id.facultygmail);
        imag1 = findViewById(R.id.backfaculty1);

        loadingDialog.startLoadingDiloag();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("faculty").child("faculty1");
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
                          Glide.with(Faculty1.this)
                                  .load(imageUrl)
                                  .placeholder(R.drawable.aiotimage2)
                                  .into(facultyImg);
                      }

                        insta.setOnClickListener(v -> openUrl(instagramUrl));
                        link.setOnClickListener(v -> openUrl(linkedinUrl));
                        git.setOnClickListener(v -> openUrl(githubUrl));
                        gmail.setOnClickListener(v -> openUrl(gmailUrl));
                        loadingDialog.dismissDialog();
                    }
                }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
                         loadingDialog.dismissDialog();
            Toast.makeText(Faculty1.this, "Failed to load data", Toast.LENGTH_SHORT).show();
        }
    });



        imag1.setOnClickListener(v -> {
            finish();
        });
    }

    private void openUrl(String url) {
        try {
            if (url != null && !url.trim().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Link is not available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to open link", Toast.LENGTH_SHORT).show();
        }
    }

}