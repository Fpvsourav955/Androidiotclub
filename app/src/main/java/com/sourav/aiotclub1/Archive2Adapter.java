package com.sourav.aiotclub1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class Archive2Adapter extends RecyclerView.Adapter<Archive2Adapter.EventViewHolder> {

    private Context context;
    private List<Archive1Model> eventList;

    public Archive2Adapter(Context context, List<Archive1Model> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.archive_image_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Archive1Model event = eventList.get(position);
        holder.eventText.setText(event.getDescription());

        Glide.with(context)
                .load(event.getImageUrl())
                .error(R.drawable.error_placeholder)
                .into(holder.eventImage);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        ImageView eventImage;
        TextView eventText;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventText = itemView.findViewById(R.id.eventText);
        }
    }
}
