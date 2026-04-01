package com.sourav.aiotclub1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage, txtTimestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtTimestamp = itemView.findViewById(R.id.txtTimestamp);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message msg = messages.get(position);
        holder.txtMessage.setText(msg.getText());
        holder.txtTimestamp.setText(sdf.format(new Date(msg.getTimestamp())));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(Message msg) {
        messages.add(msg);
        notifyItemInserted(messages.size() - 1);
    }

    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
    }
}
