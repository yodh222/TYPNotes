package com.typ.typnotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.typ.typnotes.content.Content;
import com.typ.typnotes.Session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);

        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginTask().execute();
            }
        });


        TextView signUp = findViewById(R.id.btnSignup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Signup.class);
                startActivity(intent);
            }
        });
    }
    private boolean doubleBackToExitPressedOnce = false;
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            return;
        }


        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Ketuk sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private class LoginTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return performLogin();
        }

        @Override
        protected void onPostExecute(String result) {
            handleLoginResult(result);
        }

        private String performLogin() {
            try {
                URL url = new URL("https://a224-180-178-94-67.ngrok-free.app/User");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                String postData = "req=login&email=" + email + "&password=" + password;

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream os = urlConnection.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                InputStream is = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                is.close();

                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        private void handleLoginResult(String result) {
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String session = jsonResponse.optString("Session", "");

                    saveSessionToSharedPreferences(session);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private void saveSessionToSharedPreferences(String session) {
            if ("User login Failed".equals(session)) {
                Toast.makeText(Login.this, "Email atau Password anda salah", Toast.LENGTH_SHORT).show();
            } else {
                SessionManager sessionManager = new SessionManager(Login.this);
                sessionManager.saveSession(session);

                Toast.makeText(Login.this, "Login successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this,Content.class);
                startActivity(intent);
            }
        }

    }
}