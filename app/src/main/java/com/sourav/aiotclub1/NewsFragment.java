package com.sourav.aiotclub1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;


public class NewsFragment extends Fragment {

    private NewsAdapter adapter;
    private final List<News> newsList = new ArrayList<>();
    private FloatingActionButton mainFab, deleteNewsFab, addNewsFab;
    private NewsShimmerAdapter newsshimmerAdapter;
    private boolean isFabOpen = false;
    private boolean isDeleteModeActive = false;

    public NewsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.newsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        newsshimmerAdapter = new NewsShimmerAdapter();
        adapter = new NewsAdapter(newsList, getContext());
        recyclerView.setAdapter(newsshimmerAdapter);

        mainFab = rootView.findViewById(R.id.mainFab);
        deleteNewsFab = rootView.findViewById(R.id.deleteNewsFab);
        addNewsFab = rootView.findViewById(R.id.addNewsFab);
        hideSubFabs();
        FirebaseApp.initializeApp(requireContext());
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
        );


        mainFab.setOnClickListener(v -> {
            if (isFabOpen) {
                hideSubFabs();
            } else {
                showSubFabs();
            }
        });

        deleteNewsFab.setOnClickListener(v -> {
            isDeleteModeActive = !isDeleteModeActive;
            adapter.setDeleteMode(isDeleteModeActive);
            Toast.makeText(getContext(), isDeleteModeActive ? "Delete mode ON" : "Delete mode OFF", Toast.LENGTH_SHORT).show();
        });

        addNewsFab.setOnClickListener(v -> startActivity(new Intent(getContext(), AddNewsActivity.class)));

        adapter.setOnNewsDeleteClickListener((news, position) -> deleteNewsIfAdmin(news.getNewsKey(), news.getImageUrl()));

        checkIfAdmin();
        fetchNewsFromFirebase(recyclerView);

        return rootView;
    }

    private void showSubFabs() {
        addNewsFab.show();
        deleteNewsFab.show();
        mainFab.setImageResource(R.drawable.menuclose);
        isFabOpen = true;
    }

    private void hideSubFabs() {
        addNewsFab.hide();
        deleteNewsFab.hide();
        mainFab.setImageResource(R.drawable.ic_menufab);
        isFabOpen = false;
    }

    private void checkIfAdmin() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("Admins").child(uid);

        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAdmin = snapshot.exists();
                if (isAdmin) {
                    mainFab.setVisibility(View.VISIBLE);
                    hideSubFabs();
                } else {
                    mainFab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mainFab.setVisibility(View.GONE);
            }
        });
    }

    private void fetchNewsFromFirebase(RecyclerView recyclerView) {
        DatabaseReference newsRef = FirebaseDatabase.getInstance().getReference("news");
        recyclerView.setAdapter(newsshimmerAdapter);

        newsRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newsList.clear();
                for (DataSnapshot newsSnapshot : snapshot.getChildren()) {
                    News news = newsSnapshot.getValue(News.class);
                    if (news != null) {
                        news.setNewsKey(newsSnapshot.getKey());
                        newsList.add(news);
                    }
                }
                Collections.reverse(newsList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load news: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteNewsIfAdmin(String newsId, String imageUrl) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Please log in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();
        DatabaseReference adminsRef = FirebaseDatabase.getInstance().getReference("Admins");

        adminsRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        try {
                            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                            imageRef.getMetadata()
                                    .addOnSuccessListener(storageMetadata -> imageRef.delete()
                                            .addOnSuccessListener(unused -> deleteNewsFromDatabase(newsId))
                                            .addOnFailureListener(e -> {

                                                deleteNewsFromDatabase(newsId); // Still delete the news
                                            }))
                                    .addOnFailureListener(e -> {

                                        deleteNewsFromDatabase(newsId); // Proceed anyway
                                    });
                        } catch (Exception e) {

                            deleteNewsFromDatabase(newsId);
                        }
                    } else {
                        deleteNewsFromDatabase(newsId);
                    }


                } else {
                    Toast.makeText(getContext(), "You are not authorized to delete.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteNewsFromDatabase(String newsId) {
        DatabaseReference newsRef = FirebaseDatabase.getInstance().getReference("news").child(newsId);
        newsRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                int indexToRemove = -1;
                for (int i = 0; i < newsList.size(); i++) {
                    if (newsList.get(i).getNewsKey().equals(newsId)) {
                        indexToRemove = i;
                        break;
                    }
                }

                if (indexToRemove != -1) {
                    newsList.remove(indexToRemove);
                    adapter.notifyItemRemoved(indexToRemove);
                } else {
                    adapter.notifyDataSetChanged();
                }

                Toast.makeText(getContext(), "News deleted.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
