package com.example.myapplicationtest;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplicationtest.data.RecordingCall;
import com.nispok.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class ListOfRecordingCalls extends VisibleFragment {
    private RecyclerView recyclerView;
    private ActivityAdapter mAdapter ;
    private boolean isRecordingAnable;
    private MediaPlayer player;

    public static ListOfRecordingCalls newInstance() {
       return new ListOfRecordingCalls();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRecordingAnable=QueryPreferences.isAlarmOn(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.on_of_menu, menu);
        MenuItem Item = menu.findItem(R.id.action_locate);
        menu.setGroupEnabled(Item.getGroupId(),true);
       Item.setIcon(isRecordingAnable? android.R.drawable.button_onoff_indicator_on : android.R.drawable.button_onoff_indicator_off);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_locate:
                isRecordingAnable = !isRecordingAnable;
                QueryPreferences.setAlarmOn(getContext(),isRecordingAnable);
                item.setIcon(isRecordingAnable?android.R.drawable.button_onoff_indicator_on:android.R.drawable.button_onoff_indicator_off);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_of_recording_fragment, container, false);
        recyclerView = v.findViewById(R.id.recycelListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecordingCall.setListOfRecord(getContext());
        updateUI();
        return v;
    }

    public void updateUI() {
        if (mAdapter == null) {
            mAdapter = new ActivityAdapter(RecordingCall.getListOfRecord());
            recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setmActivities(RecordingCall.getListOfRecord());
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
        private List<RecordingCall> mActivities;

        ActivityAdapter(List<RecordingCall> activities) {
            mActivities = activities;
        }

        public void setmActivities(List<RecordingCall> mActivities){
            this.mActivities = mActivities;
        }

        @NonNull
        @Override
        public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ActivityHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityHolder holder, int position) {
            RecordingCall resolveInfo = mActivities.get(position);
            holder.bindActivity(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }
    }

    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private RecordingCall mRecord;
        private TextView textViewPersonName, textViewDate;
        private ImageView imageView;

        ActivityHolder(@NonNull LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.single_item, parent, false));
            textViewPersonName =itemView.findViewById(R.id.textViewPersonName);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            imageView = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
        }

        void bindActivity(RecordingCall record) {
            mRecord = record;
            textViewPersonName.setText(mRecord.getPersonNAme());
            textViewDate.setText(mRecord.getDateAsString());
            mRecord.getIscallOut();
            imageView.setImageDrawable(getResources().getDrawable(mRecord.getIscallOut()?android.R.drawable.sym_call_outgoing:android.R.drawable.sym_call_incoming));
        }

        @Override
        public void onClick(View v) {
//            Intent intent= SingleRecordActivity.newIntent(getContext(),mRecord);
//            startActivity(intent);
            stopPlaying();
            startSnakeBar(mRecord);
        }
    }

    private void startSnakeBar(@NotNull RecordingCall mRecord) {
        Snackbar.with(getContext())
                        .duration((long) mRecord.getTimer() +4000)
                        .swipeToDismiss(true)
                        .text(mRecord.getName() )
                        .dismissOnActionClicked(false)
                        .actionLabel("start")
                        .actionListener(snackbar1 -> {
                             snackbar1.actionLabel(snackbar1.getActionLabel().equals("start") ? "stop" : "start" );
                             onPlay(player==null,mRecord);
                         }).show(getActivity());
    }

    private void onPlay(boolean start, RecordingCall mrecord) {
        if (start) {
            startPlaying(mrecord);
        } else {
            stopPlaying();
        }
    }

    private void startPlaying(@NotNull RecordingCall mRecordingCall) {
        if(mRecordingCall.getPathOfFile()==null) return;
        player = new MediaPlayer();
        try {
            player.setDataSource(mRecordingCall.getPathOfFile());
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("Media player: ", "prepare() failed");
        }
    }

    private void stopPlaying() {
        if(player!=null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopPlaying();
    }
}
