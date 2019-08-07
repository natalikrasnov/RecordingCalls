package com.example.myapplicationtest;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplicationtest.data.RecordingCall;

import java.io.IOException;

public class AboutRecordFragment extends Fragment {

    private static final String ARG_RECORD_ID = "record_id";
    private static RecordingCall mRecordCall;
    private MediaPlayer player;
    private Callbacks mCallbacks;
    /*** Required interface for hosting activities*/
    public interface Callbacks {
        void onRecordUpdated(RecordingCall recordingCall);
    }

    public static AboutRecordFragment newInstance(RecordingCall mRecordCall) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECORD_ID, mRecordCall.getName());
        AboutRecordFragment fragment = new AboutRecordFragment();
        fragment.setArguments(args);
        AboutRecordFragment.mRecordCall = mRecordCall;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(mRecordCall.getPathOfFile());
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("Media player: ", "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.about_record_fragment, container, false);
        TextView fromCallNumber = view.findViewById(R.id.caller);
        TextView ToCallNumber = view.findViewById(R.id.reciver);
        TextView date = view.findViewById(R.id.date);
        TextView timer = view.findViewById(R.id.timer);
        ImageButton player = view.findViewById(R.id.playMedia);

        fromCallNumber.setText("your phone");
        ToCallNumber.setText(mRecordCall.getPhoneNumber());
        date.setText(mRecordCall.getDate().toString());
        timer.setText(mRecordCall.getTimer()+"");
        player.setOnClickListener(v -> onPlay(player!=null));
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
