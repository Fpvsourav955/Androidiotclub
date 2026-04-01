package com.sourav.aiotclub1;

import android.annotation.SuppressLint;
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

public class ViewActivity2 extends AppCompatActivity {
    private TextView articleTitle, articleContent, articleContent1, articleContent2, articleContent3, articleAuthor, date;
    private ImageView articleImage;
    private DatabaseReference articleRef;
    private  LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);


        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_view2);
        loadingDialog = new LoadingDialog(ViewActivity2.this);
        articleTitle = findViewById(R.id.articleTitle);
        articleContent = findViewById(R.id.articleContent);
        articleContent1 = findViewById(R.id.articleContent1);
        articleContent2 = findViewById(R.id.articleContent2);
        articleContent3 = findViewById(R.id.articleContent3);
        articleImage = findViewById(R.id.articleImage);
        articleAuthor = findViewById(R.id.articleAuthor);
        date = findViewById(R.id.date);
        articleRef = FirebaseDatabase.getInstance()
                .getReference("articles")
                .child("article2");
        loadingDialog.startLoadingDiloag();
        articleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingDialog.dismissDialog();
                Article article = snapshot.getValue(Article.class);
                if (article != null) {
                    articleTitle.setText(article.title);
                    articleContent.setText(article.articleContent);
                    articleContent1.setText(article.articleContent1);
                    articleContent2.setText(article.articleContent2);
                    articleContent3.setText(article.articleContent3);
                    articleAuthor.setText(article.authorname);
                    date.setText(" • " + article.date);


                    Glide.with(ViewActivity2.this)
                            .load(article.imageUrl1)
                            .placeholder(R.drawable.commingsoonposter)
                            .into(articleImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismissDialog();
                Toast.makeText(ViewActivity2.this, "Failed to load article", Toast.LENGTH_SHORT).show();
            }
        });

    }
}