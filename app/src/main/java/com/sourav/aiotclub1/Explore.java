package com.sourav.aiotclub1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Explore extends AppCompatActivity {
    ImageView articleback;
    DatabaseReference  article1Ref, article2Ref, article3Ref,article4Ref,article5Ref;
    private  LoadingDialog loadingDialog;
    TextView dateViews, dateViews2, titleView, titleView2,titleView3,titleView4, descriptionView, descriptionView2;
    TextView dateViews1,dateViews4,dateViews3, titleView1, descriptionView1,descriptionView3,descriptionView4;
     ImageView imageView, imageView1, imageView2,imageView3,imageView4;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_explore);
        articleback=findViewById(R.id.articleback);
        loadingDialog = new LoadingDialog(Explore.this);
        loadingDialog.startLoadingDiloag();
        new android.os.Handler().postDelayed(() -> loadingDialog.dismissDialog(), 1500);



        View article1view = findViewById(R.id.article1view);
        View article2view = findViewById(R.id.article2view);
        View article3view = findViewById(R.id.article3view);
        View article4view = findViewById(R.id.article4view);
        View article5view = findViewById(R.id.article5view);

        ImageButton article1menu = findViewById(R.id.article1menu);
        ImageButton article2menu = findViewById(R.id.article2menu);
        ImageButton article3menu = findViewById(R.id.article3menu);
        ImageButton article4menu = findViewById(R.id.article4menu);
        ImageButton article5menu = findViewById(R.id.article5menu);

        dateViews = findViewById(R.id.dateView);
        titleView = findViewById(R.id.title1);
        descriptionView = findViewById(R.id.description1);
        imageView = findViewById(R.id.article1);

        dateViews1 = findViewById(R.id.dateView1);
        titleView1 = findViewById(R.id.title2);
        descriptionView1 = findViewById(R.id.description2);
        imageView1 = findViewById(R.id.article2);

        dateViews2 = findViewById(R.id.dateView2);
        titleView2 = findViewById(R.id.title3);
        descriptionView2 = findViewById(R.id.description3);
        imageView2 = findViewById(R.id.article3);

        dateViews3 = findViewById(R.id.dateView3);
        titleView3 = findViewById(R.id.title4);
        descriptionView3 = findViewById(R.id.description4);
        imageView3 = findViewById(R.id.article4);

        dateViews4 = findViewById(R.id.dateView4);
        titleView4 = findViewById(R.id.title5);
        descriptionView4 = findViewById(R.id.description5);
        imageView4 = findViewById(R.id.article5);
        article1Ref = FirebaseDatabase.getInstance().getReference("articles/article1");
        article2Ref = FirebaseDatabase.getInstance().getReference("articles/article2");
        article3Ref = FirebaseDatabase.getInstance().getReference("articles/article3");
        article4Ref = FirebaseDatabase.getInstance().getReference("articles/article4");
        article5Ref = FirebaseDatabase.getInstance().getReference("articles/article5");
        article1menu.setOnClickListener(v -> showCustomDialog(v, 1));
        article2menu.setOnClickListener(v -> showCustomDialog(v, 2));
        article3menu.setOnClickListener(v -> showCustomDialog(v, 3));
        article4menu.setOnClickListener(v -> showCustomDialog(v, 4));
        article5menu.setOnClickListener(v -> showCustomDialog(v, 5));

        fetchDataFromFirebase();
        fetchDataFromFirebase1();
        fetchDataFromFirebase2();
        fetchDataFromFirebase3();
        fetchDataFromFirebase4();

        
        insetsController.setAppearanceLightStatusBars(false);
        articleback.setOnClickListener(v ->{
            Intent intent = new Intent(Explore.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        article1view.setOnClickListener(v -> {
            Intent intent1=new Intent(Explore.this, ViewActivity1.class);
            startActivity(intent1);
        });
        article2view.setOnClickListener(v -> {
            Intent intent2=new Intent(Explore.this, ViewActivity2.class);
            startActivity(intent2);
        });
        article3view.setOnClickListener(v -> {
            Intent intent3=new Intent(Explore.this, ViewActivity3.class);
            startActivity(intent3);
        });
        article4view.setOnClickListener(v -> {
            Intent intent4=new Intent(Explore.this, ViewActivity4.class);
            startActivity(intent4);
        });
        article5view.setOnClickListener(v -> {
            Intent intent5=new Intent(Explore.this, ViewActivity5.class);
            startActivity(intent5);
        });
    }
    private void fetchDataFromFirebase() {
        article1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String date = snapshot.child("date").getValue(String.class);
                    String title = snapshot.child("title").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    if (dateViews != null) dateViews.setText(date);
                    if (titleView != null) titleView.setText(title);
                    if (descriptionView != null) descriptionView.setText(description);

                    if (imageView != null && imageUrl != null) {
                        Glide.with(Explore.this).load(imageUrl).into(imageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Explore.this, "Failed to load Article 1", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showCustomDialog(View anchorView, int articleNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_article_menu, null);

        TextView viewOption = dialogView.findViewById(R.id.view_option);
        TextView detailsOption = dialogView.findViewById(R.id.details_option);

        AlertDialog dialog = builder.setView(dialogView).create();

        viewOption.setOnClickListener(v -> {
            Intent intent = switch (articleNumber) {
                case 1 -> new Intent(this, ViewActivity1.class);
                case 2 -> new Intent(this, ViewActivity2.class);
                case 3 -> new Intent(this, ViewActivity3.class);
                case 4 -> new Intent(this, ViewActivity4.class);
                case 5 -> new Intent(this, ViewActivity5.class);
                default -> null;
            };
            if (intent != null) startActivity(intent);
            dialog.dismiss();
        });

        detailsOption.setOnClickListener(v -> {
            Intent intent = switch (articleNumber) {
                case 1 -> new Intent(this, DetailsActivity1.class);
                case 2 -> new Intent(this, DetailsActivity2.class);
                case 3 -> new Intent(this, DetailsActivity3.class);
                case 4 -> new Intent(this, DetailsActivity4.class);
                case 5 -> new Intent(this, DetailsActivity5.class);
                default -> null;
            };
            if (intent != null) startActivity(intent);
            dialog.dismiss();
        });

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void fetchDataFromFirebase1() {
        article2Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String date = snapshot.child("date").getValue(String.class);
                    String title = snapshot.child("title").getValue(String.class);
                    String description = snapshot.child("description1").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    if (dateViews1 != null) dateViews1.setText(date);
                    if (titleView1 != null) titleView1.setText(title);
                    if (descriptionView1 != null) descriptionView1.setText(description);

                    if (imageView1 != null && imageUrl != null) {
                        Glide.with(Explore.this).load(imageUrl).into(imageView1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Explore.this, "Failed to load Article 2", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchDataFromFirebase2() {
        article3Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String date = snapshot.child("date").getValue(String.class);
                    String title = snapshot.child("title").getValue(String.class);
                    String description = snapshot.child("description2").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    if (dateViews2 != null) dateViews2.setText(date);
                    if (titleView2 != null) titleView2.setText(title);
                    if (descriptionView2 != null) descriptionView2.setText(description);

                    if (imageView2 != null && imageUrl != null) {
                        Glide.with(Explore.this).load(imageUrl).into(imageView2);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Explore.this, "Failed to load Article 3", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchDataFromFirebase3() {
        article4Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String date = snapshot.child("date3").getValue(String.class);
                    String title = snapshot.child("title3").getValue(String.class);
                    String description = snapshot.child("description3").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl3").getValue(String.class);

                    if (dateViews3 != null) dateViews3.setText(date);
                    if (titleView3 != null) titleView3.setText(title);
                    if (descriptionView3 != null) descriptionView3.setText(description);

                    if (imageView3 != null && imageUrl != null) {
                        Glide.with(Explore.this).load(imageUrl).into(imageView3);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Explore.this, "Failed to load Article 4", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchDataFromFirebase4() {
        article5Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String date = snapshot.child("date4").getValue(String.class);
                    String title = snapshot.child("title4").getValue(String.class);
                    String description = snapshot.child("description4").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl4").getValue(String.class);

                    if (dateViews4 != null) dateViews4.setText(date);
                    if (titleView4 != null) titleView4.setText(title);
                    if (descriptionView4 != null) descriptionView4.setText(description);

                    if (imageView4 != null && imageUrl != null) {
                        Glide.with(Explore.this).load(imageUrl).into(imageView4);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Explore.this, "Failed to load Article 5", Toast.LENGTH_SHORT).show();
            }
        });
    }

}