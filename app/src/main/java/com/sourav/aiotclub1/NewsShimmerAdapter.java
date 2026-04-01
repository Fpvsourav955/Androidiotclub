package com.sourav.aiotclub1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsShimmerAdapter extends RecyclerView.Adapter<NewsShimmerAdapter.NewsShimmerViewHolder> {


    @NonNull
    @Override
    public NewsShimmerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_shimmer, parent, false);
        return new NewsShimmerAdapter.NewsShimmerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsShimmerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    static class NewsShimmerViewHolder extends RecyclerView.ViewHolder {
        public NewsShimmerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
