package com.iuturakulov.vkvoicesuperappkit.model;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.iuturakulov.vkvoicesuperappkit.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordFragment extends Fragment implements View.OnClickListener {

    public static boolean isRecording = false;
    private ImageButton tempBtnRecord;
    private TextView currFileName;
    private MediaRecorder mediaRecorder;
    private String recordFile;
    private Chronometer timer;

    public RecordFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tempBtnRecord = view.findViewById(R.id.record_btn);
        timer = view.findViewById(R.id.record_timer);
        currFileName = view.findViewById(R.id.record_filename);
        tempBtnRecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.record_btn) {
            if (isRecording) {
                stopRecording();
                tempBtnRecord.setImageDrawable(getResources().getDrawable(R.drawable.mic_off, null));
                isRecording = false;
            } else {
                // Check permission to record audio
                if (checkPermissions()) {
                    startRecording();
                    tempBtnRecord.setImageDrawable(getResources().getDrawable(R.drawable.mic_on, null));
                    isRecording = true;
                }
            }
        }
    }

    private void stopRecording() {
        timer.stop();
        currFileName.setText(String.format("Recording Stopped, File Saved : %s", recordFile));
        //Stop media recorder and set it to null for further use to record new audio
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void startRecording() {
        // Start timer from 0
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        // Get app external directory path
        String recordPath = requireActivity().getExternalFilesDir("/").getAbsolutePath();
        // Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();
        // Initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
        recordFile = String.format("Recording_%s.3gp", formatter.format(now));
        currFileName.setText(String.format("Current name of file is: %s", recordFile));
        // Setup Media Recorder for recording
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Start Recording
        mediaRecorder.start();
    }

    private boolean checkPermissions() {
        String perm_record = Manifest.permission.RECORD_AUDIO;
        if (ActivityCompat.checkSelfPermission(requireContext(), perm_record) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        int PERMISSION_CODE = 21;
        ActivityCompat.requestPermissions(requireActivity(), new String[]{perm_record}, PERMISSION_CODE);
        return false;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            stopRecording();
        }
    }
}
