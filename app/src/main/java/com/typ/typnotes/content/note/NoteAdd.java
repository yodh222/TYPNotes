package com.typ.typnotes.content.note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.typ.typnotes.R;
import com.typ.typnotes.Session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NoteAdd extends AppCompatActivity {

    private EditText judul,data;
    private String strJudul,strData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add);
        getSupportActionBar().hide();

        judul =findViewById(R.id.NoteTitle);
        data = findViewById(R.id.NoteContent);

        ImageView saveButton = findViewById(R.id.saveButton);
        ImageView backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddNote().execute();
                finish();
            }
        });
    }

    private class AddNote extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return performAddNote();
        }

        private String performAddNote() {
            try {
                SessionManager sessionManager = new SessionManager(NoteAdd.this);
                String cookieSession = sessionManager.getSession();

                URL url = new URL("https://a224-180-178-94-67.ngrok-free.app/Storage");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Cookie", "User=" + cookieSession);
                urlConnection.setDoOutput(true);

                strJudul = judul.getText().toString();
                strData = data.getText().toString();

                String setRequest = "req=addData&judul=" + strJudul+"&data="+strData;
                try (OutputStream outputStream = urlConnection.getOutputStream()) {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    writer.write(setRequest);
                    writer.flush();
                }

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            getResponse(s);
        }

        private void getResponse(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String info = jsonObject.getString("Info");
                    if ("Berhasil menambahkan note".equals(info)){
                        Toast.makeText(NoteAdd.this,"Note berhasil di ditambahkan!",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(NoteAdd.this,"Note gagal di ditambahkan!",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}