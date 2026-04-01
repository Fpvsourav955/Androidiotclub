package com.sourav.aiotclub1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class FeedbackFragment extends Fragment {

    private DatabaseReference databaseRef;

    public FeedbackFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseRef = FirebaseDatabase.getInstance().getReference("feedbacks");

        AppCompatButton btnSubmit = view.findViewById(R.id.btn_submit);
        EditText nameInput = view.findViewById(R.id.wr_name);
        EditText rollInput = view.findViewById(R.id.wr_roll);
        RatingBar overallRating = view.findViewById(R.id.rating_overall);
        RadioGroup rgContent = view.findViewById(R.id.radio_content_quality);
        RadioGroup rgInstructor = view.findViewById(R.id.radio_instructor);
        RadioGroup rgVenue = view.findViewById(R.id.radio_venue);
        RadioGroup rgDay = view.findViewById(R.id.radio_day);
        RadioGroup rgRecommend = view.findViewById(R.id.radio_recommend);
        EditText suggestionsInput = view.findViewById(R.id.edit_suggestions);


        Calendar allowedTime = Calendar.getInstance();
        allowedTime.set(Calendar.YEAR, 2025);
        allowedTime.set(Calendar.MONTH, Calendar.OCTOBER);
        allowedTime.set(Calendar.DAY_OF_MONTH, 24);
        allowedTime.set(Calendar.HOUR_OF_DAY, 18);
        allowedTime.set(Calendar.MINUTE, 0);
        allowedTime.set(Calendar.SECOND, 0);


        btnSubmit.setOnClickListener(v -> {
            Calendar currentTime = Calendar.getInstance();

            if (currentTime.before(allowedTime)) {

                Toast.makeText(getContext(), "Feedback will be enabled after 24th Oct 6 PM", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = nameInput.getText().toString().trim();
            String roll = rollInput.getText().toString().trim();

            if (name.isEmpty() || roll.isEmpty()) {
                Toast.makeText(getContext(), "Please enter name and roll number!", Toast.LENGTH_SHORT).show();
                return;
            }

            int overall = (int) overallRating.getRating();
            String content = getSelectedRadioText(rgContent);
            String instructor = getSelectedRadioText(rgInstructor);
            String venue = getSelectedRadioText(rgVenue);
            String day = getSelectedRadioText(rgDay);
            String recommend = getSelectedRadioText(rgRecommend);
            String suggestions = suggestionsInput.getText().toString().trim();


            String key = databaseRef.push().getKey();
            Feedback feedback = new Feedback(name, roll, overall, content, instructor, venue, day, recommend, suggestions);
            databaseRef.child(key).setValue(feedback)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Feedback Submitted!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private String getSelectedRadioText(RadioGroup rg) {
        int selectedId = rg.getCheckedRadioButtonId();
        if (selectedId != -1) {
            return ((RadioButton) getView().findViewById(selectedId)).getText().toString();
        } else {
            return "";
        }
    }
}
