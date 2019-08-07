package com.example.myapplicationtest.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public SQLBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + RecorderDbSchema.CrimeTable.NAME
                + "(" +" _id integer primary key autoincrement, "
                + RecorderDbSchema.CrimeTable.Cols.TITLE + ", "
                + RecorderDbSchema.CrimeTable.Cols.DATE + ", "
                + RecorderDbSchema.CrimeTable.Cols.isCallIsOut + ", "
                + RecorderDbSchema.CrimeTable.Cols.PATHFILE + ", "
                + RecorderDbSchema.CrimeTable.Cols.PHONENUMBER + ", "
                + RecorderDbSchema.CrimeTable.Cols.TIMER + ", "
                + RecorderDbSchema.CrimeTable.Cols.PERSON +")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}