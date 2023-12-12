package com.typ.typnotes.Session;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SessionManager {
    private SessionDbHelper dbHelper;

    public SessionManager(Context context) {
        dbHelper = new SessionDbHelper(context);
    }

    public boolean saveSession(String sessionData) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("session_data", sessionData);

        long result = db.insert("session", null, values);

        db.close();

        return result != -1;
    }

    public String getSession() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {"session_data"};
        Cursor cursor = db.query("session", projection, null, null, null, null, null);

        String sessionData = "";

        while (cursor.moveToNext()) {
            sessionData = cursor.getString(cursor.getColumnIndexOrThrow("session_data"));
        }

        cursor.close();
        db.close();

        return sessionData;
    }
}

