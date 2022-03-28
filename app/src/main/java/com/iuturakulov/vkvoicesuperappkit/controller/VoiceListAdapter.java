package com.iuturakulov.vkvoicesuperappkit.controller;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iuturakulov.vkvoicesuperappkit.R;

import java.io.File;
import java.io.IOException;

public class VoiceListAdapter extends RecyclerView.Adapter<VoiceListAdapter.AudioViewHolder> {

    private final File[] allFiles;
    private final onItemListClick onItemListClick;
    private TimeParser timeAgo;

    public VoiceListAdapter(File[] allFiles, onItemListClick onItemListClick) {
        this.allFiles = allFiles;
        this.onItemListClick = onItemListClick;
    }

    public static String convertToNormalTime(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        if (hours > 0) {
            finalTimerString = hours + ":";
        }
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_voice_component, parent, false);
        timeAgo = new TimeParser();
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        holder.list_title.setText(allFiles[position].getName());
        holder.list_date.setText(timeAgo.getTimeAgo(allFiles[position].lastModified()));
        System.out.println(allFiles[position].getAbsoluteFile().getAbsolutePath());
        holder.list_duration.setText(getDuration(allFiles[position].getAbsoluteFile()));
    }

    private String getDuration(File file) {
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(file.getAbsolutePath());
            mp.prepare();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        int duration = mp.getDuration();
        mp.release();
        return convertToNormalTime(duration);
    }

    @Override
    public int getItemCount() {
        return allFiles.length;
    }

    public interface onItemListClick {
        void onClickListener(File file, int position);
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView list_title;
        private final TextView list_date;
        private final TextView list_duration;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            list_duration = itemView.findViewById(R.id.recording_duration);
            list_title = itemView.findViewById(R.id.recording_title);
            list_date = itemView.findViewById(R.id.recording_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListClick.onClickListener(allFiles[getAdapterPosition()], getAdapterPosition());
        }
    }
}
