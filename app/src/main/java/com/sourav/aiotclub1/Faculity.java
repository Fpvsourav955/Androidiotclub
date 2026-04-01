package com.sourav.aiotclub1;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowInsetsControllerCompat;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Faculity extends AppCompatActivity {
    ConstraintLayout facultyview1,facultyview2;

    private DatabaseReference databaseReference;

    private ShimmerFrameLayout shimmer1, shimmer2,shimmer3;
    private CardView cardView1, cardView2,cardView3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_faculity);



        ImageView devback= findViewById(R.id.devback);
        devback.setOnClickListener(v->
                finish());
        facultyview1=findViewById(R.id.facultyview1);
        facultyview2=findViewById(R.id.facultyview2);
        shimmer1 = findViewById(R.id.shimmer1);
        shimmer2 = findViewById(R.id.shimmer2);
        shimmer3 = findViewById(R.id.shimmer3);

        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        cardView3 = findViewById(R.id.cardView3);

        shimmer1.startShimmer();
        shimmer2.startShimmer();
        shimmer3.startShimmer();
        TextView facultyName1 = findViewById(R.id.facultyname1);
        TextView facultyTitle1 = findViewById(R.id.facultytitle1);
        CircleImageView facultyImage1 = findViewById(R.id.circleImageView);

        TextView facultyName2 = findViewById(R.id.facultyname2);
        TextView facultyTitle2 = findViewById(R.id.facultytitle2);
        CircleImageView facultyImage2 = findViewById(R.id.circleImageView1);
        TextView facultyName3 = findViewById(R.id.facultyname3);
        TextView facultyTitle3 = findViewById(R.id.facultytitle3);
        CircleImageView facultyImage3 = findViewById(R.id.circleImageView2);
        databaseReference = FirebaseDatabase.getInstance().getReference("faculty");
        loadFaculty("faculty1", facultyName1, facultyTitle1, facultyImage1);

        loadFaculty("faculty2", facultyName2, facultyTitle2, facultyImage2);
        loadFaculty("faculty3", facultyName3, facultyTitle3, facultyImage3);

        facultyview1.setOnClickListener(V ->{
            Intent intent=new Intent(Faculity.this, Faculty1.class);
            startActivity(intent);
        });
        facultyview2.setOnClickListener(V ->{
            Intent intent1=new Intent(Faculity.this, Faculty2.class);
            startActivity(intent1);
        });
        new Handler().postDelayed(this::showFacultyContent, 1500);
    }

    private void showFacultyContent() {

        shimmer1.stopShimmer();
        shimmer2.stopShimmer();
        shimmer3.stopShimmer();

        shimmer1.setVisibility(GONE);
        shimmer2.setVisibility(GONE);
        shimmer3.setVisibility(GONE);


        cardView1.setVisibility(View.VISIBLE);
        cardView2.setVisibility(View.VISIBLE);
        cardView3.setVisibility(View.VISIBLE);

    }

    private void loadFaculty(String facultyNode, TextView nameView, TextView titleView, CircleImageView imageView) {
        databaseReference.child(facultyNode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String title = snapshot.child("title").getValue(String.class);
                String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                if (name != null) nameView.setText(name);
                if (title != null) titleView.setText(title);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Picasso.get().load(imageUrl).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Faculity.this, "Failed to load " + facultyNode, Toast.LENGTH_SHORT).show();
            }
        });
    }
}