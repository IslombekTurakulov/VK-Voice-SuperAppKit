package com.iuturakulov.vkvoicesuperappkit.model;

import static com.iuturakulov.vkvoicesuperappkit.model.MainActivity.initializeSpeechRecognition;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.iuturakulov.vkvoicesuperappkit.R;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.ui.SpeechProgressView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class RecordFragment extends Fragment implements View.OnClickListener {
    private TextView fileNameTV;
    private ImageView btn_record;
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private String recordFile;
    private EditText text;
    private Chronometer recordTimer;
    private SpeechProgressView progressView;

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
        btn_record = view.findViewById(R.id.record_btn);
        recordTimer = view.findViewById(R.id.record_timer);
        fileNameTV = view.findViewById(R.id.record_filename);
        text = view.findViewById(R.id.editTextTextPersonName);
        progressView = view.findViewById(R.id.progress);
        // text.setInputType(InputType.TYPE_NULL);
        btn_record.setOnClickListener(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.record_btn) {
            if (isRecording) {
                stopRecording();
                btn_record.setImageDrawable(getResources().getDrawable(R.drawable.mic_off, null));
                isRecording = false;
            } else {
                if (checkPermissions()) {
                    startRecording();
                    initializeSpeechRecognition();
                    btn_record.setImageDrawable(getResources().getDrawable(R.drawable.mic_on, null));
                    isRecording = true;
                }
            }
        }
    }

    private void startRecording() {
        try {
            Speech.getInstance().startListening(new SpeechDelegate() {
                @Override
                public void onStartOfSpeech() {
                    Log.i("speech", "speech recognition is now active");
                }

                @Override
                public void onSpeechRmsChanged(float value) {
                    Log.d("speech", "rms is now: " + value);
                }

                @Override
                public void onSpeechPartialResults(List<String> results) {
                    StringBuilder str = new StringBuilder();
                    for (String res : results) {
                        str.append(res).append(" ");
                    }
                    Log.i("speech", "partial result: " + str.toString().trim());
                }

                @Override
                public void onSpeechResult(String result) {
                    Log.i("speech", "result: " + result);
                }
            });
        } catch (SpeechRecognitionNotAvailable | GoogleVoiceTypingDisabledException exc) {
            Log.e("speech", "Speech recognition is not available on this device!");
        }
        recordTimer.setBase(SystemClock.elapsedRealtime());
        recordTimer.start();
        SimpleDateFormat date = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss", Locale.ROOT);
        Date now = new Date();
        String recordPath = Objects.requireNonNull(requireActivity()).getExternalFilesDir("/").getAbsolutePath();
        recordFile = String.format("Recording_%s.3gp", date.format(now));
        fileNameTV.setText(String.format("????????????, ?????? ??????????: %s", recordFile));
        initializeRecorder(recordPath);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    private void initializeRecorder(String recordPath) {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(String.format("%s/%s", recordPath, recordFile));
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }

    private void stopRecording() {
        recordTimer.setBase(SystemClock.elapsedRealtime());
        recordTimer.stop();
        fileNameTV.setText(String.format("???????????? ??????????????????, ???????? ????????????????: %s", recordFile));
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private boolean checkPermissions() {
        String recPermission = Manifest.permission.RECORD_AUDIO;
        if (ActivityCompat.checkSelfPermission(requireContext(), recPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            int codePermission = 21;
            ActivityCompat.requestPermissions(requireActivity(), new String[]{recPermission}, codePermission);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            String strPattern = "^[a-zA-Z0-9._ -]+\\.(mp3|3gp|wav)$";
            final EditText input = new EditText(requireContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext())
                    .setTitle("?????????????? ???????????????? ??????????")
                    .setMessage("?????????????? ????????????????")
                    .setPositiveButton(
                            "Yes", (dialog, whichButton) -> {
                                String newFileName = input.getText().toString();
                                if (newFileName.matches(strPattern)) {
                                    Toast.makeText(getContext(),
                                            "??????????????!", Toast.LENGTH_SHORT).show();
                                    recordFile = newFileName;
                                    dialog.cancel();
                                } else {
                                    Toast.makeText(getContext(),
                                            "???????????????????????? ?????? ??????????!", Toast.LENGTH_SHORT).show();
                                }
                            }).setNegativeButton("Cancel", null).setView(input);
            alertDialog.show();
            stopRecording();
        }
    }
}