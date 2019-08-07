package com.example.myapplicationtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.myapplicationtest.data.RecordingCall;

public class SingleRecordActivity extends SingleFragmentActivity implements AboutRecordFragment.Callbacks {

    private static final String EXTRA_RECORD_NAME = "com.example.myapplicationtest.record_id";
    private static RecordingCall mRecording;

    private static Intent intent;

    public static Intent newIntent(Context packageContext, RecordingCall mrecord) {
        intent = new Intent(packageContext, SingleRecordActivity.class);
        mRecording = mrecord;
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return AboutRecordFragment.newInstance(mRecording);
    }

    @Override
    public void onRecordUpdated(RecordingCall recordingCall) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String recordName = (String) getIntent().getSerializableExtra(EXTRA_RECORD_NAME);
        setTitle(recordName);
    }
}
