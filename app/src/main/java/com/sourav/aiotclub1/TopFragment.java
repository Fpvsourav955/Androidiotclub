package com.sourav.aiotclub1;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TopFragment extends Fragment {

    private RecyclerView recyclerView;
    private androidx.appcompat.widget.AppCompatEditText editTextMessage1;
    private AppCompatImageView buttonSend;

    private MessageAdapter adapter;
    private List<Message> messageList;

    private DatabaseReference messagesRef;

    public TopFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_top, container, false);

        recyclerView = root.findViewById(R.id.recyclerMessages);
        editTextMessage1 = root.findViewById(R.id.editTextMessage);
        buttonSend = root.findViewById(R.id.sendAnswerButton);

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        messagesRef = FirebaseDatabase.getInstance().getReference("messages");

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                long now = System.currentTimeMillis();
                long dayInMillis = 24 * 60 * 60 * 1000;

                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    Message msg = messageSnapshot.getValue(Message.class);
                    if (msg != null) {
                        long messageTime = msg.getTimestamp();
                        if ((now - messageTime) >= dayInMillis) {
                            messageSnapshot.getRef().removeValue();
                        } else {
                            messageList.add(msg);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        editTextMessage1.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                buttonSend.performClick();
                return true;
            }
            return false;
        });

        buttonSend.setOnClickListener(v -> {
            String msg = Objects.requireNonNull(editTextMessage1.getText()).toString().trim();
            if (!TextUtils.isEmpty(msg)) {
                long now = System.currentTimeMillis();
                Message message = new Message(msg, now);


                messagesRef.push().setValue(message);

                editTextMessage1.setText("");
            }
        });

        return root;
    }

}
