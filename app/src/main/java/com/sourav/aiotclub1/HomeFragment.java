package com.sourav.aiotclub1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import android.animation.ValueAnimator;
import android.widget.TextView;
public class HomeFragment extends Fragment {

    private ViewPager2 viewPager2;
    private ShimmerFrameLayout shimmerFrameLayout;

    private final Handler slideHandler = new Handler();
    private Runnable sliderRunnable;
    private SlideAdapter slideAdapter;

    private final List<SlideItem> slideItems = new ArrayList<>();

    private DatabaseReference sliderRef, article1Ref, article2Ref, article3Ref;

    private TextView dateViews, dateViews1, dateViews2;
    private TextView titleView, titleView1, titleView2;
    private TextView descriptionView, descriptionView1, descriptionView2;
    private ImageView imageView, imageView1, imageView2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ImageButton article1menu = view.findViewById(R.id.article1menu);
        ImageButton article2menu = view.findViewById(R.id.article2menu);
        ImageButton article3menu = view.findViewById(R.id.article3menu);

        viewPager2 = view.findViewById(R.id.viewPager);

        dateViews = view.findViewById(R.id.dateView);
        titleView = view.findViewById(R.id.title1);
        descriptionView = view.findViewById(R.id.description1);
        imageView = view.findViewById(R.id.article1);
        TextView membersCount = view.findViewById(R.id.communityMembersCount);
        TextView projectsCount = view.findViewById(R.id.communityProjectsCount);
        TextView eventsCount = view.findViewById(R.id.communityEventsCount);
        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout);
        FrameLayout mainLayout = view.findViewById(R.id.drawerLayout);

        shimmerFrameLayout.startShimmer();
        mainLayout.setVisibility(View.GONE);
        animateCountUp(membersCount, 1000);
        animateCountUp(projectsCount, 100);
        animateCountUp(eventsCount, 25);


        dateViews1 = view.findViewById(R.id.dateView1);
        titleView1 = view.findViewById(R.id.title2);
        descriptionView1 = view.findViewById(R.id.description2);
        imageView1 = view.findViewById(R.id.article2);

        dateViews2 = view.findViewById(R.id.dateView2);
        titleView2 = view.findViewById(R.id.title3);
        descriptionView2 = view.findViewById(R.id.description3);
        imageView2 = view.findViewById(R.id.article3);


        Button explore = view.findViewById(R.id.explore);
        explore.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Explore.class);
            startActivity(intent);
        });

        article1menu.setOnClickListener(v -> showCustomDialog(v, 1));
        article2menu.setOnClickListener(v -> showCustomDialog(v, 2));
        article3menu.setOnClickListener(v -> showCustomDialog(v, 3));

        slideAdapter = new SlideAdapter(slideItems);
        if (viewPager2 != null) {
            viewPager2.setAdapter(slideAdapter);
        } else {
            Toast.makeText(getContext(), "Error: viewPager2 not found!", Toast.LENGTH_LONG).show();
        }

        sliderRef = FirebaseDatabase.getInstance().getReference("slideImages");
        article1Ref = FirebaseDatabase.getInstance().getReference("articles/article1");
        article2Ref = FirebaseDatabase.getInstance().getReference("articles/article2");
        article3Ref = FirebaseDatabase.getInstance().getReference("articles/article3");

        fetchDataFromFirebase();
        fetchDataFromFirebase1();
        fetchDataFromFirebase2();
        setupViewPager();
        fetchImagesFromFirebase();
        new Handler().postDelayed(() -> {

            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
        }, 3000);
        return view;
    }

    private void showCustomDialog(View anchorView, int articleNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_article_menu, null);

        TextView viewOption = dialogView.findViewById(R.id.view_option);
        TextView detailsOption = dialogView.findViewById(R.id.details_option);

        AlertDialog dialog = builder.setView(dialogView).create();

        viewOption.setOnClickListener(v -> {
            Intent intent = switch (articleNumber) {
                case 1 -> new Intent(getActivity(), ViewActivity1.class);
                case 2 -> new Intent(getActivity(), ViewActivity2.class);
                case 3 -> new Intent(getActivity(), ViewActivity3.class);
                default -> null;
            };
            if (intent != null) startActivity(intent);
            dialog.dismiss();
        });

        detailsOption.setOnClickListener(v -> {
            Intent intent = switch (articleNumber) {
                case 1 -> new Intent(getActivity(), DetailsActivity1.class);
                case 2 -> new Intent(getActivity(), DetailsActivity2.class);
                case 3 -> new Intent(getActivity(), DetailsActivity3.class);
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

    private void fetchDataFromFirebase() {
        article1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || getView() == null) return;

                if (snapshot.exists()) {
                    String date = snapshot.child("date").getValue(String.class);
                    String title = snapshot.child("title").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    if (dateViews != null) dateViews.setText(date);
                    if (titleView != null) titleView.setText(title);
                    if (descriptionView != null) descriptionView.setText(description);

                    if (imageView != null && imageUrl != null) {
                        Glide.with(requireContext()).load(imageUrl).into(imageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Failed to load Article 1", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void fetchDataFromFirebase1() {
        article2Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || getView() == null) return;

                if (snapshot.exists()) {
                    String date = snapshot.child("date").getValue(String.class);
                    String title = snapshot.child("title").getValue(String.class);
                    String description = snapshot.child("description1").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    if (dateViews1 != null) dateViews1.setText(date);
                    if (titleView1 != null) titleView1.setText(title);
                    if (descriptionView1 != null) descriptionView1.setText(description);
                    if (imageView1 != null && imageUrl != null) {
                        Glide.with(requireContext()).load(imageUrl).into(imageView1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Failed to load Article 2", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void fetchDataFromFirebase2() {
        article3Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || getView() == null) return;

                if (snapshot.exists()) {
                    String date = snapshot.child("date").getValue(String.class);
                    String title = snapshot.child("title").getValue(String.class);
                    String description = snapshot.child("description2").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    if (dateViews2 != null) dateViews2.setText(date);
                    if (titleView2 != null) titleView2.setText(title);
                    if (descriptionView2 != null) descriptionView2.setText(description);

                    if (imageView2 != null && imageUrl != null && isAdded()) {
                        Glide.with(requireContext()).load(imageUrl).into(imageView2);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Failed to load Article 3", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupViewPager() {
        if (viewPager2 != null) {
            viewPager2.setAdapter(slideAdapter);
        } else {
            Toast.makeText(getContext(), "Error: viewPager not found!", Toast.LENGTH_LONG).show();
        }
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(5);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        viewPager2.setPageTransformer(transformer);

        sliderRunnable = () -> {
            if (!slideItems.isEmpty()) {
                int nextItem = (viewPager2.getCurrentItem() + 1) % slideItems.size();
                viewPager2.setCurrentItem(nextItem, true);
            }
        };

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                slideHandler.removeCallbacks(sliderRunnable);
                slideHandler.postDelayed(sliderRunnable, 3000);
            }
        });
    }

    private void fetchImagesFromFirebase() {
        sliderRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || getView() == null) return;

                slideItems.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String imageUrl = dataSnapshot.getValue(String.class);
                    slideItems.add(new SlideItem(imageUrl));
                }
                slideAdapter.notifyDataSetChanged();
                showContentAfterLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Failed to load slider images", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void showContentAfterLoading() {
        View rootView = getView();
        if (rootView == null || !isAdded()) {

            return;
        }

        ShimmerFrameLayout shimmerLayout = rootView.findViewById(R.id.shimmerLayout);
        View contentLayout = rootView.findViewById(R.id.drawerLayout);

        if (shimmerLayout != null) {
            shimmerLayout.stopShimmer();
            shimmerLayout.setVisibility(View.GONE);
        }

        if (contentLayout != null) {
            contentLayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        slideHandler.postDelayed(sliderRunnable, 2500);
    }
    private void animateCountUp(final TextView textView, int targetValue) {
        ValueAnimator animator = ValueAnimator.ofInt(0, targetValue);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                textView.setText(animatedValue + "+");
            }
        });
        animator.start();
    }
}