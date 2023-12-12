package com.typ.typnotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.typ.typnotes.content.Content;
import com.typ.typnotes.Session.SessionManager;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
    }
    @Override
    protected void onStart() {
        super.onStart();
        SessionManager sessionCheck = new SessionManager(this);
        String session = sessionCheck.getSession();

        Intent i;
        if (!TextUtils.isEmpty(session)) {
            i = new Intent(this, Content.class);
        }else{
            i = new Intent(this, Login.class);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(i);
                finish();
            }
        }, 1400);
    }
}