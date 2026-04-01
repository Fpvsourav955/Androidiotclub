package com.sourav.aiotclub1;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewsDetailActivity extends AppCompatActivity {

    private TextView titleText, descriptionText, timestampText,authorNameText;
    private ImageView newsImage,authorImage;
    private DatabaseReference newsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        titleText = findViewById(R.id.newsTitleDetail);
        descriptionText = findViewById(R.id.newsDescriptionDetail);

        timestampText = findViewById(R.id.newsTimestampDetail);
        authorNameText = findViewById(R.id.authorName);
        authorImage = findViewById(R.id.authorImage);
        ImageView backBtn = findViewById(R.id.devback);
        backBtn.setOnClickListener(v -> finish());


        newsImage = findViewById(R.id.newsImageDetail);

        String newsKey = getIntent().getStringExtra("newsKey");
        if (newsKey == null) {
            Toast.makeText(this, "No news key provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        newsRef = FirebaseDatabase.getInstance().getReference("news").child(newsKey);

        newsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(NewsDetailActivity.this, "News item not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                String title = snapshot.child("title").getValue(String.class);
                String description = snapshot.child("description").getValue(String.class);
                Long timestampLong = snapshot.child("timestamp").getValue(Long.class);
                String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                String memberName  = snapshot.child("memberName").getValue(String.class);
                String memberProfileImageUrl = snapshot.child("memberProfileImageUrl").getValue(String.class);

                if (title != null) title = title.replace("\\n", "\n");
                if (description != null) description = description.replace("\\n", "\n");

                String timestamp = "";
                if (timestampLong != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                    timestamp = sdf.format(new Date(timestampLong));
                }

                titleText.setText(title);
                descriptionText.setText(description);
                timestampText.setText(timestamp);

                if (memberName != null) {
                    authorNameText.setText(memberName);
                } else {
                    authorNameText.setText("Unknown author");
                }


                Glide.with(NewsDetailActivity.this).load(imageUrl).into(newsImage);

                if (memberProfileImageUrl != null && !memberProfileImageUrl.isEmpty()) {
                    Glide.with(NewsDetailActivity.this)
                            .load(memberProfileImageUrl)
                            .placeholder(R.drawable.icons8testaccount96)
                            .into(authorImage);
                } else {
                    authorImage.setImageResource(R.drawable.icons8testaccount96);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NewsDetailActivity.this, "Error loading news: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
