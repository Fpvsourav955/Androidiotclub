package com.sourav.aiotclub1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventDetailsFragment extends Fragment {

    private MaterialCheckBox cbDay1, cbDay2, cbDay3;
    private TextView tvDay1, tvDay2, tvDay3;

    public EventDetailsFragment() {

    }

    public static EventDetailsFragment newInstance(String param1, String param2) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);


        cbDay1 = view.findViewById(R.id.day1checkbox);
        cbDay2 = view.findViewById(R.id.day2checkbox);
        cbDay3 = view.findViewById(R.id.day3checkbox);


        tvDay1 = view.findViewById(R.id.day1);
        tvDay2 = view.findViewById(R.id.day2);
        tvDay3 = view.findViewById(R.id.day3);


        updateEventUI();

        return view;
    }


    private String getStatus(String eventDateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date eventDate = sdf.parse(eventDateTime);
            Date now = new Date();

            if (now.before(eventDate)) return "Upcoming";
            else if (now.equals(eventDate) || now.after(eventDate)) return "Completed";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "Upcoming";
    }


    private void updateEventUI() {
        String EVENT1_DATETIME = "2025-10-22 18:30";
        updateCheckAndText(cbDay1, tvDay1, getStatus(EVENT1_DATETIME));
        String EVENT2_DATETIME = "2025-10-23 18:30";
        updateCheckAndText(cbDay2, tvDay2, getStatus(EVENT2_DATETIME));
        String EVENT3_DATETIME = "2025-10-24 18:30";
        updateCheckAndText(cbDay3, tvDay3, getStatus(EVENT3_DATETIME));
    }

    @SuppressLint("SetTextI18n")
    private void updateCheckAndText(MaterialCheckBox checkBox, TextView textView, String status) {
        switch (status) {
            case "Upcoming":
                checkBox.setEnabled(false);
                checkBox.setChecked(false);
                textView.setText("Upcoming");
                break;
            case "Completed":
                checkBox.setEnabled(false);
                checkBox.setChecked(true);
                textView.setText("Completed");
                break;

        }
    }
}
