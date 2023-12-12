package com.typ.typnotes.content.note;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class NoteEdit extends AppCompatActivity {
    private EditText judul,content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        getSupportActionBar().hide();

        judul = findViewById(R.id.judul);
        content = findViewById(R.id.content);

        Intent intent = getIntent();
        String title = intent.getStringExtra("Judul");
        String data = intent.getStringExtra("Data");

        judul.setText(title);
        content.setText(data);
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
                new EditNote().execute();
            }
        });
    }

    private class EditNote extends AsyncTask<String, Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            return performEditNote();
        }

        private String performEditNote(){
            try {
                SessionManager sessionManager = new SessionManager(NoteEdit.this);
                String sessionCookie = sessionManager.getSession();

                URL url = new URL("https://a224-180-178-94-67.ngrok-free.app/Storage");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Cookie", "User="+sessionCookie);

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                String title = judul.getText().toString();
                String data = content.getText().toString();
                String idNote = getIntent().getStringExtra("Id");

                String requestBody = "req=updateData&judul="+title+"&data="+data+"&idNote="+idNote;
                try (OutputStream outputStream = urlConnection.getOutputStream()) {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    writer.write(requestBody);
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

            }catch (IOException e){
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
                    if ("Berhasil edit note".equals(info)){
                        Toast.makeText(NoteEdit.this,"Note berhasil di edit!",Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(NoteEdit.this,"Note gagal di edit!",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}