package com.sourav.aiotclub1;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AttendanceFragment extends Fragment {

    MaterialCheckBox cbDay1, cbDay2, cbDay3;
    EditText etName, etRoll, etEmail;
    Button btnSubmit;

    private DatabaseReference attendanceRef;

    public AttendanceFragment() {
        // Required empty public constructor
    }

    public static AttendanceFragment newInstance(String param1, String param2) {
        AttendanceFragment fragment = new AttendanceFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attendanceRef = FirebaseDatabase.getInstance().getReference("Attendance");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_attendance, container, false);


        etName = view.findViewById(R.id.et_name);
        etRoll = view.findViewById(R.id.et_roll);
        etEmail = view.findViewById(R.id.et_email);


        cbDay1 = view.findViewById(R.id.cb_day1);
        cbDay2 = view.findViewById(R.id.cb_day2);
        cbDay3 = view.findViewById(R.id.cb_day3);

        // Submit Button
        btnSubmit = view.findViewById(R.id.btn_submit);

        // Ensure only one checkbox is selected
        MaterialCheckBox[] checkBoxes = {cbDay1, cbDay2, cbDay3};
        for (MaterialCheckBox cb : checkBoxes) {
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    for (MaterialCheckBox other : checkBoxes) {
                        if (other != buttonView) other.setChecked(false);
                    }
                }
            });
        }

        // Submit button click
        btnSubmit.setOnClickListener(v -> submitAttendance());

        return view;
    }
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }
    private int getEnabledDay() {
        String today = getCurrentDate();

        String DAY2_DATE = "2025-10-23";
        // YYYY-MM-DD format
        String DAY1_DATE = "2025-10-22";
        String DAY3_DATE = "2025-10-24";
        if (today.equals(DAY1_DATE)) return 1;
        else if (today.equals(DAY2_DATE)) return 2;
        else if (today.equals(DAY3_DATE)) return 3;
        else return 0; // no attendance allowed today
    }

    private void submitAttendance() {
        String name = etName.getText().toString().trim();
        String roll = etRoll.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(roll) || TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int enabledDay = getEnabledDay();
        if (enabledDay == 0) {
            Toast.makeText(getContext(), "Attendance not open today", Toast.LENGTH_SHORT).show();
            return;
        }


        int selectedDay = 0;
        if (cbDay1.isChecked()) selectedDay = 1;
        else if (cbDay2.isChecked()) selectedDay = 2;
        else if (cbDay3.isChecked()) selectedDay = 3;

        if (selectedDay != enabledDay) {
            Toast.makeText(getContext(), "You can only submit attendance for Day " + enabledDay + " today", Toast.LENGTH_SHORT).show();
            return;
        }


        final int selectedDayFinal = selectedDay;


        DatabaseReference studentRef = attendanceRef.child(roll);

        studentRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                Boolean day1 = false, day2 = false, day3 = false;
                if (task.getResult().exists()) {
                    day1 = task.getResult().child("day1").getValue(Boolean.class) != null
                            ? task.getResult().child("day1").getValue(Boolean.class) : false;
                    day2 = task.getResult().child("day2").getValue(Boolean.class) != null
                            ? task.getResult().child("day2").getValue(Boolean.class) : false;
                    day3 = task.getResult().child("day3").getValue(Boolean.class) != null
                            ? task.getResult().child("day3").getValue(Boolean.class) : false;
                }


                if ((selectedDayFinal == 1 && Boolean.TRUE.equals(day1)) ||
                        (selectedDayFinal == 2 && Boolean.TRUE.equals(day2)) ||
                        (selectedDayFinal == 3 && Boolean.TRUE.equals(day3))) {
                    Toast.makeText(getContext(), "Attendance already submitted for Day " + selectedDayFinal, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update only the selected day
                switch (selectedDayFinal) {
                    case 1: day1 = true; break;
                    case 2: day2 = true; break;
                    case 3: day3 = true; break;
                }

                // Create attendance object
                Attendance attendance = new Attendance(name, roll, email, Boolean.TRUE.equals(day1), Boolean.TRUE.equals(day2), Boolean.TRUE.equals(day3));

                // Save/update in Firebase under roll number
                studentRef.setValue(attendance)
                        .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Attendance submitted!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to submit attendance", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(getContext(), "Error accessing database", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Attendance model class
    public static class Attendance {
        public String name;
        public String roll;
        public String email;
        public boolean day1;
        public boolean day2;
        public boolean day3;

        public Attendance() { }

        public Attendance(String name, String roll, String email, boolean day1, boolean day2, boolean day3) {
            this.name = name;
            this.roll = roll;
            this.email = email;
            this.day1 = day1;
            this.day2 = day2;
            this.day3 = day3;
        }
    }
}
