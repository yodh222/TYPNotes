package com.typ.typnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Signup extends AppCompatActivity {
    private EditText usernameEditText, emailEditText, passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();

        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);

        Button btnSignUp = findViewById(R.id.signup);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SignUpTask().execute();
            }
        });
    }

    private class SignUpTask extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... voids) {
            return performSignUp();
        }

        @Override
        protected void onPostExecute(String result) {
            handleSignUpResult(result);
        }

        private String performSignUp() {
            try {
                URL url = new URL("https://a224-180-178-94-67.ngrok-free.app/User");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String username = usernameEditText.getText().toString();

                String postData = "req=signup&email=" + email + "&password=" + password + "&username=" + username;

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
                Log.d("TestingBg",response.toString());
                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        private void handleSignUpResult(String result) {
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);

                    String res = jsonResponse.optString("Info", "");

                    if("User inserted successfully".equals(res)){
                        finish();
                    }else{
                        Toast.makeText(Signup.this,res,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}