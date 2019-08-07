package com.example.myapplicationtest.dataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.myapplicationtest.data.RecordingCall;

public class RecorderCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public RecorderCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public RecordingCall getRecord() {
        String phoneNumber = getString(getColumnIndex(RecorderDbSchema.CrimeTable.Cols.PHONENUMBER));
        String title = getString(getColumnIndex(RecorderDbSchema.CrimeTable.Cols.TITLE));
        String date = getString(getColumnIndex(RecorderDbSchema.CrimeTable.Cols.DATE));
        int isCallIsOutString = getInt(getColumnIndex(RecorderDbSchema.CrimeTable.Cols.isCallIsOut));
        String personNAme = getString(getColumnIndex(RecorderDbSchema.CrimeTable.Cols.PERSON));
        String pathFile = getString(getColumnIndex(RecorderDbSchema.CrimeTable.Cols.PATHFILE));
        float timer = getFloat(getColumnIndex(RecorderDbSchema.CrimeTable.Cols.TIMER));

        RecordingCall recordingCall = new RecordingCall(phoneNumber, date);
        recordingCall.setIscallOut(isCallIsOutString == 0);
        recordingCall.setTimer(timer);
        recordingCall.setName(title);
        recordingCall.setPathOfFile(pathFile);
        recordingCall.setPersonNAme(personNAme);
        return recordingCall;
    }
}
