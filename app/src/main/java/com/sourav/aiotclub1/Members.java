package com.sourav.aiotclub1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Members extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Member> memberList;
    MemberAdapter memberAdapter;
    ShimmerMemberAdapter shimmerAdapter;
    DatabaseReference databaseReference;

    boolean isShimmer = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_members);


        ImageView back = findViewById(R.id.memberback);
        if (back != null) {
            back.setOnClickListener(v -> finish());
        }

        recyclerView = findViewById(R.id.memberRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        shimmerAdapter = new ShimmerMemberAdapter();
        recyclerView.setAdapter(shimmerAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("members");


        new Handler().postDelayed(() -> fetchMembers(), 1500);
    }

    private void fetchMembers() {
        memberList = new ArrayList<>();
        memberAdapter = new MemberAdapter(this, memberList);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Member member = dataSnapshot.getValue(Member.class);
                    if (member != null) {
                        memberList.add(member);
                    }
                }


                recyclerView.setAdapter(memberAdapter);
                memberAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Members.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

