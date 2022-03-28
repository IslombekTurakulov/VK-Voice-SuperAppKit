package com.iuturakulov.vkvoicesuperappkit.model;

import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iuturakulov.vkvoicesuperappkit.R;
import com.iuturakulov.vkvoicesuperappkit.controller.TimeParser;
import com.iuturakulov.vkvoicesuperappkit.controller.VoiceListAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class VoiceListFragment extends Fragment implements VoiceListAdapter.onItemListClick, VoiceListAdapter.onLongItemListClick {
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private File fileToPlay = null;
    private ImageButton btnStart;
    private TextView fileNameOfPlayer;
    private SeekBar seekBarOfPlayer;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;
    private RecyclerView audioList;

    public VoiceListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voice_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        audioList = view.findViewById(R.id.audio_list_view);
        btnStart = view.findViewById(R.id.player_play_btn);
        fileNameOfPlayer = view.findViewById(R.id.player_filename);
        seekBarOfPlayer = view.findViewById(R.id.player_seekbar);
        initializeListOfAudioItems(audioList);
        btnStart.setOnClickListener(v -> {
            if (isPlaying) {
                pauseAudio();
            } else {
                if (fileToPlay != null) {
                    resumeAudio();
                }
            }
        });

        seekBarOfPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() < 100) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                    resumeAudio();
                } else {
                    pauseAudio();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (fileToPlay != null) {
                    int progress = seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                    resumeAudio();
                }
            }
        });

    }

    private void initializeListOfAudioItems(RecyclerView audioList) {
        String path = requireActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        File[] allFiles = directory.listFiles();
        VoiceListAdapter voiceListAdapter = new VoiceListAdapter(allFiles, this, this);
        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(getContext()));
        audioList.setAdapter(voiceListAdapter);
    }

    @Override
    public void onClickListener(File file, int position) {
        if (file == null) {
            return;
        }
        fileToPlay = file;
        if (isPlaying) {
            stopAudio();
        }
        playAudio(fileToPlay);
    }

    private void pauseAudio() {
        if (fileToPlay == null) {
            return;
        }
        String path = requireActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        File[] allFiles = directory.listFiles();
        if (Objects.requireNonNull(allFiles).length > audioList.getChildCount()) {
            initializeListOfAudioItems(audioList);
        }
        mediaPlayer.pause();
        btnStart.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24, null));
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void resumeAudio() {
        mediaPlayer.start();
        btnStart.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_baseline_pause_24, null));
        isPlaying = true;

        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }

    private void stopAudio() {
        btnStart.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24, null));
        isPlaying = false;
        mediaPlayer.stop();
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void playAudio(File fileToPlay) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnStart.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_baseline_pause_24, null));
        fileNameOfPlayer.setText(fileToPlay.getName());
        isPlaying = true;
        mediaPlayer.setOnCompletionListener(mp -> {
            stopAudio();
        });
        seekBarOfPlayer.setMax(mediaPlayer.getDuration());
        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                seekBarOfPlayer.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 500);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isPlaying) {
            stopAudio();
        }
    }

    @Override
    public void onClickListener(File file, TextView title, TextView lastModified, int position) {
        String strPattern = "^[a-zA-Z0-9._ -]+\\.(mp3|3gp|wav)$";
        final EditText input = new EditText(requireContext());
        input.setTextColor(Color.BLACK);
        input.setText(file.getName());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle("Введите название файла")
                .setMessage("Введите название с расширением")
                .setPositiveButton(
                        "Yes", (dialog, whichButton) -> {
                            String newFileName = input.getText().toString();
                            if (newFileName.matches(strPattern)) {
                                Toast.makeText(getContext(),
                                        "Успешно!", Toast.LENGTH_SHORT).show();
                                File newFile = new File(file.getAbsolutePath(), newFileName);
                                try {
                                    Files.move(file.toPath(), newFile.toPath());
                                    initializeListOfAudioItems(audioList);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(getContext(),
                                        "Неправильное имя файла!", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("Cancel", null).setView(input);
        alertDialog.show();
    }
}