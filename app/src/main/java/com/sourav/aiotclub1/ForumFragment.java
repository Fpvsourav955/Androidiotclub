package com.sourav.aiotclub1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForumFragment extends Fragment {
    private QuestionAdapter adapter;
  
    private List<QuestionModel> questionList;
    private DatabaseReference questionRef;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public ForumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForumFragment newInstance(String param1, String param2) {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.questionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        questionList = new ArrayList<>();
        adapter = new QuestionAdapter(questionList,true);
        recyclerView.setAdapter(adapter);

        questionRef = FirebaseDatabase.getInstance().getReference("questions");

        loadQuestions();

        FloatingActionButton fab = view.findViewById(R.id.fabAskQuestion);
        fab.setOnClickListener(v -> startActivity(new Intent(getContext(), Question.class)));

        return view;
    }

    private void loadQuestions() {
        
        questionRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    QuestionModel question = data.getValue(QuestionModel.class);
                    if (question != null) {
                        questionList.add(0, question);
                        question.setId(data.getKey());
                    }
                }
                adapter.notifyDataSetChanged();
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                
                Toast.makeText(getContext(), "Failed to load: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}