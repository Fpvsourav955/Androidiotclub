package com.sourav.aiotclub1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventFragment extends Fragment {

    private ImageView eventImage,eventImage1, detailIcon,imageView3;
    private ImageView archiveImage1, archiveImage2;
    private TextView titleUpcoming, bullet1Upcoming, bullet2Upcoming, bullet3Upcoming;
    private TextView titleArchive1, bullet1Archive1, bullet2Archive1, bullet3Archive1;
    private TextView titleArchive2, bullet1Archive2, bullet2Archive2, bullet3Archive2;
    private  TextView titleUpcoming1, bullet1Upcoming1, bullet2Upcoming1, bullet3Upcoming1;
    private AppCompatButton registerButton;
    private LoadingDialog loadingDialog;

    private DatabaseReference eventRef, archiveRef1, archiveRef2,eventRef1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        eventImage = view.findViewById(R.id.upcomingeventimage);
        titleUpcoming = view.findViewById(R.id.upcomingeventTitle);
        bullet1Upcoming = view.findViewById(R.id.bulletText);
        bullet2Upcoming = view.findViewById(R.id.bulletText2);
        bullet3Upcoming = view.findViewById(R.id.bulletText3);
        registerButton = view.findViewById(R.id.appCompatButton);
        detailIcon = view.findViewById(R.id.imageView2);


        eventImage1=view.findViewById(R.id.upcomingeventimage1);
        titleUpcoming1 = view.findViewById(R.id.upcomingeventTitle1);
        bullet1Upcoming1 = view.findViewById(R.id.bulletText1);
        bullet2Upcoming1 = view.findViewById(R.id.bulletText2222);
        bullet3Upcoming1 = view.findViewById(R.id.bulletText33);
        AppCompatButton eventregister = view.findViewById(R.id.appCompatButton1);


        archiveImage1 = view.findViewById(R.id.archiveeventimage);
        titleArchive1 = view.findViewById(R.id.archiveeventTitle);
        bullet1Archive1 = view.findViewById(R.id.bulletText34);
        bullet2Archive1 = view.findViewById(R.id.bulletText21);
        bullet3Archive1 = view.findViewById(R.id.bulletText22);
        AppCompatButton archiveButton1 = view.findViewById(R.id.seeevent);

        AppCompatButton registerButton = view.findViewById(R.id.appCompatButton1);
        archiveImage2 = view.findViewById(R.id.archive2eventimage);
        titleArchive2 = view.findViewById(R.id.archive2eventTitle);
        bullet1Archive2 = view.findViewById(R.id.bulletText222);
        bullet2Archive2 = view.findViewById(R.id.bulletText211);
        bullet3Archive2 = view.findViewById(R.id.bulletText344);
        AppCompatButton archiveButton2 = view.findViewById(R.id.seeevent1);
        imageView3 = view.findViewById(R.id.imageView3);
        imageView3.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(),EventDetails1.class);
            startActivity(intent);
        });

        loadingDialog = new LoadingDialog((Activity) requireContext());

        eventRef1=FirebaseDatabase.getInstance().getReference("events").child("event2");
        eventRef = FirebaseDatabase.getInstance().getReference("events").child("event1");
        archiveRef1 = FirebaseDatabase.getInstance().getReference("events").child("archive1");
        archiveRef2 = FirebaseDatabase.getInstance().getReference("events").child("archive2");

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String formUrl = "https://forms.gle/vcwJWLgaXiGdKnmY6";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(formUrl));
                startActivity(intent);
            }
        });

        archiveButton1.setOnClickListener(v -> startActivity(new Intent(getContext(), Archive1.class)));
        archiveButton2.setOnClickListener(v -> startActivity(new Intent(getContext(), Archive2.class)));


        loadingDialog.startLoadingDiloag();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loadEventData(eventRef1, titleUpcoming1, bullet1Upcoming1, bullet2Upcoming1, bullet3Upcoming1, eventImage1, true);
            loadEventData(eventRef, titleUpcoming, bullet1Upcoming, bullet2Upcoming, bullet3Upcoming, eventImage, true);
            loadEventData(archiveRef1, titleArchive1, bullet1Archive1, bullet2Archive1, bullet3Archive1, archiveImage1, false);
            loadEventData(archiveRef2, titleArchive2, bullet1Archive2, bullet2Archive2, bullet3Archive2, archiveImage2, false);
        }, 1000);

        return view;
    }

    private void loadEventData(DatabaseReference ref,
                               TextView titleView, TextView bullet1, TextView bullet2, TextView bullet3,
                               ImageView imageView, boolean isUpcoming) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isUpcoming) loadingDialog.dismissDialog();

                if (snapshot.exists()) {
                    String title = snapshot.child("title").getValue(String.class);
                    String imgUrl = snapshot.child("imageUrl").getValue(String.class);
                    String pt1 = snapshot.child("point1").getValue(String.class);
                    String pt2 = snapshot.child("point2").getValue(String.class);
                    String pt3 = snapshot.child("point3").getValue(String.class);

                    if (title != null) {
                        title = title.replace("\\n", "\n");
                        titleView.setText(title);
                    }

                    bullet1.setText(pt1 != null ? "• " + pt1 : "• Not available");
                    bullet2.setText(pt2 != null ? "• " + pt2 : "• Not available");
                    bullet3.setText(pt3 != null ? "• " + pt3 : "• Not available");

                    if (imgUrl != null && !imgUrl.isEmpty()) {
                        Glide.with(requireContext()).load(imgUrl).into(imageView);
                    }

                    if (isUpcoming) {
                        String regLink = snapshot.child("registerlink").getValue(String.class);
                        registerButton.setOnClickListener(v -> {
                            if (regLink != null && !regLink.isEmpty()) {
                                Intent intent = new Intent(getContext(), Eventview.class);
                                intent.putExtra(Eventview.EXTRA_URL, regLink);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "Registration link not available", Toast.LENGTH_SHORT).show();
                            }
                        });

                        detailIcon.setOnClickListener(v ->
                                Toast.makeText(getContext(), "More details feature coming soon!", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else if (isUpcoming) {
                    Toast.makeText(getContext(), "No event data found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isUpcoming) loadingDialog.dismissDialog();
                Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
