package com.example.myapplicationtest.data;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordingCall {

    private String name;
    private String phoneNumber;
    private String pathOfFile;
    private Date date;
    private String dateAsString;
    private float timer;
    private boolean iscallOut = false;
    private String personNAme;
    private static List<RecordingCall> listOfRecord;
    private static RecordingCallLab recordingCallLab;

    public RecordingCall(@NotNull String phoneNumber,@NotNull String date) {
        this.phoneNumber = phoneNumber;
        this.dateAsString = date;
    }

    public RecordingCall(@NotNull String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.date = new Date();
        setDate(date);
    }

    private void setDate(Date date) {
        dateAsString = date.toLocaleString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName() {
        setListOfRecordIfWasNULL();
        name = "record"+(listOfRecord.isEmpty()?"1":getListOfRecord().indexOf(getListOfRecord().get(getListOfRecord().size() - 1))+2);
    }

    public String getName() {
        return name;
    }

    public String getPersonNAme() {
        return personNAme;
    }

    public void setPersonNAme(String personNAme) {
        this.personNAme = personNAme;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPathOfFile() {
        return pathOfFile;
    }

    public void setPathOfFile(String pathOfFile) {
        this.pathOfFile = pathOfFile;
    }

    public Date  getDate() {
        return date;
    }

    public String getDateAsString(){return dateAsString;}

    public float getTimer() {
        return timer;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setTimer(float timer) {
        this.timer = timer*1000;
    }

    public boolean getIscallOut() {
        return iscallOut;
    }

    public void setIscallOut(boolean iscallOut) {
        this.iscallOut = iscallOut;
    }

    public static List<RecordingCall> getListOfRecord() {
        return listOfRecord;
    }

    public static boolean setListOfRecordIfWasNULL() {
        if(RecordingCall.getListOfRecord()==null) {
            RecordingCall.listOfRecord = new ArrayList<>();
            return true;
        }
        return false;
    }

    public static void setListOfRecord(Context context) {
        recordingCallLab=RecordingCallLab.get(context);
        RecordingCall.listOfRecord=recordingCallLab.getREcordingCalls();
        setListOfRecordIfWasNULL();
    }

    public static void addToList(@NotNull RecordingCall recordingCall, Context context){
        setListOfRecord(context);
        if(recordingCall.name==null) recordingCall.setName();
        if(recordingCall.personNAme==null) recordingCall.setPersonNAme(recordingCall.getNameFromContacts(context));
        recordingCallLab.addRecordingCall(recordingCall);
        listOfRecord.add(recordingCall);
    }

    private String getNameFromContacts(@NotNull Context context) {
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.getCount()>0) {
            while (cursor.moveToNext()){
                int name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String mname = cursor.getString(name);
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    Cursor pCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (phone.equals(phoneNumber)) {
                            cursor.close();
                            pCur.close();
                            return mname;
                        }
                    }
                    pCur.close();
                }
            }
            cursor.close();
        }
        return phoneNumber;
    }

}
