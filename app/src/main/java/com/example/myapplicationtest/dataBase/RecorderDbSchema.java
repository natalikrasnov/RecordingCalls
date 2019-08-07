package com.example.myapplicationtest.dataBase;

public class RecorderDbSchema {
    public static final class CrimeTable {
        public static final String NAME = "recordingCalls";
        public static final class Cols {
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String isCallIsOut = "isOutCall";
            public static final String PATHFILE = "pathFile";
            public static final String PHONENUMBER = "phoneNumber";
            public static final String TIMER = "timer";
            public static final String PERSON = "person";
        }
    }

}
