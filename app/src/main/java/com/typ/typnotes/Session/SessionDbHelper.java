package com.typ.typnotes.Session;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SessionDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "session.db";
    private static final int DATABASE_VERSION = 1;

    public SessionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE session (id INTEGER PRIMARY KEY AUTOINCREMENT, session_data TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement jika ada pembaruan versi database
    }
}

