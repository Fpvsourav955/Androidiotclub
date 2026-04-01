package com.sourav.aiotclub1;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.List;

public class VideoPagerAdapter extends RecyclerView.Adapter<VideoPagerAdapter.VideoViewHolder> {

    private final Context context;
    private final List<String> videoUrls;
    private final List<ExoPlayer> players = new ArrayList<>();

    public VideoPagerAdapter(Context context, List<String> videoUrls) {
        this.context = context;
        this.videoUrls = videoUrls;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_card, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.bind(videoUrls.get(position));
    }

    @Override
    public int getItemCount() {
        return videoUrls.size();
    }

    /** Play only one video at the given position */
    public void playVideoAt(int position) {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setPlayWhenReady(i == position);
        }
    }

    /** Release all players when done */
    public void releasePlayers() {
        for (ExoPlayer player : players) {
            player.release();
        }
        players.clear();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        PlayerView playerView;
        ExoPlayer player;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.playerView);
        }

        void bind(String url) {
            player = new ExoPlayer.Builder(context).build();
            players.add(player);

            playerView.setPlayer(player);
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
            player.setMediaItem(mediaItem);
            player.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
            player.prepare();
            player.setPlayWhenReady(false);
        }
    }
}
