package com.example.myapplicationtest;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplicationtest.data.RecordingCall;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class TellService extends Service {

    private static RecordingCall mRecordingCall;
    private static MediaRecorder recorder;
    private static File audiofile;
    private static boolean recordstarted = false;

    private static final String ACTION_IN = "android.intent.action.PHONE_STATE";
    private static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("service", "destroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("StartService", "TService");
        return super.onStartCommand(intent, flags, startId);
    }

    private static void startRecording(@NotNull String number) {
       if( mRecordingCall != null && recordstarted )return;
        mRecordingCall = new RecordingCall(number);
        mRecordingCall.setName();
        File sampleDir = new File(Environment.getExternalStorageDirectory(), "/TestRecordingDasa1");
        if (!sampleDir.exists()) {
            try {
                sampleDir.mkdirs();
                sampleDir.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String file_name = mRecordingCall.getName();
        try {
            audiofile = File.createTempFile(file_name, ".3gp", sampleDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        mRecordingCall.setPathOfFile(audiofile.getAbsolutePath());
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        recordstarted = true;
    }

    private static void stopRecording(@NotNull Context context) {
        if (recordstarted) {
            recorder.stop();
            recorder.release();
            recordstarted = false;
            Date currentDate = new Date();
            float currentTimeInMinutes = (currentDate.getMinutes() + currentDate.getSeconds() * 60) / 60;
            mRecordingCall.setTimer(currentTimeInMinutes - ((mRecordingCall.getDate().getMinutes() + (mRecordingCall.getDate().getSeconds() * 60) / 60)));
            RecordingCall.addToList(mRecordingCall, context);
            mRecordingCall = null;
        }
    }
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;

    public abstract static class PhonecallReceiver extends BroadcastReceiver {
        //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations
        private static Date callStartTime;
        private static String savedNumber;  //because the passed incoming is only valid in ringing
//
//        Bundle bundle;
//        String state;
//        String inCall, outCall;
//        public boolean wasRinging = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            if(!QueryPreferences.isAlarmOn(context))return;
//
//            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL"))
//                savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
//                if (intent.getAction().equals(ACTION_IN)) {
//                    Log.i("TAG", "onReceive: IN");
//                    if ((bundle = intent.getExtras()) != null) {
//                        state = bundle.getString(TelephonyManager.EXTRA_STATE);
//                        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//                            inCall = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
//                            wasRinging = true;
//                            Toast.makeText(context, "IN : " + inCall, Toast.LENGTH_LONG).show();
//                        }if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//                          //  if (wasRinging == true) {
//                                Toast.makeText(context, "ANSWERED", Toast.LENGTH_LONG).show();
//                                isIncoming = true;
//                                startRecording( bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER), new Date());
//                    //        }
//                        } if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
//                            wasRinging = false;
//                            Toast.makeText(context, "REJECT || DISCO", Toast.LENGTH_LONG).show();
//                            stopRecording(new Date(),context,true);
//                        }
//                    }
//                } else if (intent.getAction().equals(ACTION_OUT)) {
//                    Log.i("TAG", "onReceive: OUT");
//                    if ((bundle = intent.getExtras()) != null) {
//                        outCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//                        Toast.makeText(context, "OUT : " + outCall, Toast.LENGTH_LONG).show();
//                        isIncoming = false;
//                        startRecording(savedNumber, new Date());
//                        if ((bundle = intent.getExtras()) != null) {
//                            state = bundle.getString(TelephonyManager.EXTRA_STATE);
//                            if (state != null) {
//                                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
//                                    wasRinging = false;
//                                    Toast.makeText(context, "REJECT || DISCO", Toast.LENGTH_LONG).show();
//                                    stopRecording(new Date(),context,false);
//
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//    }
//}
            //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.

            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
                if(mRecordingCall!=null && recordstarted) {
                    mRecordingCall.setPhoneNumber(savedNumber);
                }
            } else {
                String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if(mRecordingCall!=null && recordstarted){
                    mRecordingCall.setPhoneNumber(number);
                }
                int state = 0;
                if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    state = TelephonyManager.CALL_STATE_IDLE;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    state = TelephonyManager.CALL_STATE_RINGING;
                }
                onCallStateChanged(context, state, number);
            }
        }
        //Derived classes should override these to respond to specific events of interest
        protected abstract void onIncomingCallReceived(Context ctx, String number, Date start);
        protected abstract void onIncomingCallAnswered(Context ctx, String number, Date start);
        protected abstract void onIncomingCallEnded(Context ctx, String number, Date start, Date end);
        protected abstract void onOutgoingCallStarted(Context ctx, String number, Date start);
        protected abstract void onOutgoingCallEnded(Context ctx, String number, Date start, Date end);
        protected abstract void onMissedCall(Context ctx, String number, Date start);
        //Deals with actual events
        //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
        //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
        public void onCallStateChanged(Context context, int state, String number) {
//            if (lastState == state && !recordstarted) {
//                //No change, debounce extras
//                return;
//            }
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    callStartTime = new Date();
                    savedNumber = number;
                    onIncomingCallReceived(context, number, callStartTime);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                    if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                        if(mRecordingCall!=null){
                            mRecordingCall.setIscallOut(true);
                            stopRecording(context);
                            break;
                        }
                        callStartTime = new Date();
                        startRecording(savedNumber);
                        mRecordingCall.setIscallOut(true);
                        onOutgoingCallStarted(context, savedNumber, callStartTime);
                    } else {
                        callStartTime = new Date();
                        startRecording(savedNumber);
                        mRecordingCall.setIscallOut(false);
                        onIncomingCallAnswered(context, savedNumber, callStartTime);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if(mRecordingCall==null) break;
                    //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                    if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                        //Ring but no pickup-  a miss
                        onMissedCall(context, savedNumber, callStartTime);
                    } else if (!mRecordingCall.getIscallOut()) {
                        stopRecording(context);
                        onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                    } else {
                        stopRecording(context);
                        onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                    }
                    break;
            }
            lastState = state;
            Log.i("  ", "onCallStateChanged: "+state);
        }
    }

    public static class CallReceiver extends PhonecallReceiver {

        @Override
        protected void onIncomingCallReceived(Context ctx, String number, Date start) {
            Log.d("onIncomingCallReceived", number + " " + start.toString());
            Toast.makeText(ctx, "onIncomingCallReceived", Toast.LENGTH_LONG).show();
        }
        @Override
        protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
            Log.d("onIncomingCallAnswered", number + " " + start.toString());
            Toast.makeText(ctx, "onIncomingCallAnswered", Toast.LENGTH_LONG).show();
        }
        @Override
        protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
            Log.d("onIncomingCallEnded", number + " " + start.toString() + "\t" + end.toString());
            Toast.makeText(ctx, "onIncomingCallEnded", Toast.LENGTH_LONG).show();
        }
        @Override
        protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
            Log.d("onOutgoingCallStarted", number + " " + start.toString());
            Toast.makeText(ctx, "onOutgoingCallStarted", Toast.LENGTH_LONG).show();
        }
        @Override
        protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
            Log.d("onOutgoingCallEnded", number + " " + start.toString() + "\t" + end.toString());
            Toast.makeText(ctx, "nOutgoingCallEnded", Toast.LENGTH_LONG).show();

        }
        @Override
        protected void onMissedCall(Context ctx, String number, Date start) {
            Log.d("onMissedCall", number + " " + start.toString());
            Toast.makeText(ctx, "onMissedCall", Toast.LENGTH_LONG).show();

        }
    }
}



