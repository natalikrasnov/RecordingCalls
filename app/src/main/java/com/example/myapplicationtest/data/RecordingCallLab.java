package com.example.myapplicationtest.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplicationtest.dataBase.RecorderCursorWrapper;
import com.example.myapplicationtest.dataBase.RecorderDbSchema;
import com.example.myapplicationtest.dataBase.SQLBaseHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecordingCallLab {
    private static RecordingCallLab mRecordingCallLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private RecordingCallLab(@NotNull Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SQLBaseHelper(mContext).getWritableDatabase();
    }

    public void addRecordingCall(RecordingCall c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(RecorderDbSchema.CrimeTable.NAME, null, values);
    }

    public static RecordingCallLab get(Context context) {
        if (mRecordingCallLab == null) mRecordingCallLab = new RecordingCallLab(context);
        return mRecordingCallLab;
    }

    public List<RecordingCall> getREcordingCalls() {
        List<RecordingCall> crimes = new ArrayList<>();
        RecorderCursorWrapper cursor= queryRecords();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getRecord());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }
        return crimes;
    }

    @NotNull
    private RecorderCursorWrapper queryRecords() {
        Cursor cursor = mDatabase.query(RecorderDbSchema.CrimeTable.NAME,
                null,
                // columns -null selects all columns
                null,
                null,
                null, // groupBy
                null, // having
                null  //orderBy
                );
        return new RecorderCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(@NotNull RecordingCall recordingCall) {
        ContentValues values = new ContentValues();
        values.put(RecorderDbSchema.CrimeTable.Cols.TITLE, recordingCall.getName());
        values.put(RecorderDbSchema.CrimeTable.Cols.DATE, recordingCall.getDateAsString());
        values.put(RecorderDbSchema.CrimeTable.Cols.isCallIsOut, recordingCall.getIscallOut()? 0:1);
        values.put(RecorderDbSchema.CrimeTable.Cols.PATHFILE, recordingCall.getPathOfFile());
        values.put(RecorderDbSchema.CrimeTable.Cols.PHONENUMBER, recordingCall.getPhoneNumber());
        values.put(RecorderDbSchema.CrimeTable.Cols.TIMER, recordingCall.getTimer());
        values.put(RecorderDbSchema.CrimeTable.Cols.PERSON, recordingCall.getPersonNAme());
        return values;
    }
}
