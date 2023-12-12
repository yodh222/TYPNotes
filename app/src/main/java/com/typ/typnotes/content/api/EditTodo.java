package com.typ.typnotes.content.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.typ.typnotes.Session.SessionManager;
import com.typ.typnotes.content.note.NoteEdit;

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

public class EditTodo extends AsyncTask<String,Void,String> {
    private int id;
    private boolean status;
    private String data;
    private Context context;

    public EditTodo(Context context) {
        this.context = context;
    }

    public void setId(String id) {
        this.id = Integer.parseInt(id);
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    protected String doInBackground(String... strings) {
        return performEditNote();
    }

    private String performEditNote(){
        try {
            SessionManager sessionManager = new SessionManager(context);
            String sessionCookie = sessionManager.getSession();


            URL url = new URL("https://a224-180-178-94-67.ngrok-free.app/Todo");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Cookie", "User="+sessionCookie);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            String sts = String.valueOf(status);
            String requestBody;
            if (sts.isEmpty()){
                requestBody = "req=updateData&data="+data+"&idTodo="+id;
            } else {
                requestBody = "req=updateData&status="+String.valueOf(status)+"&idTodo="+id;
            }

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

        } catch (IOException e){
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
                if ("Berhasil edit todo list".equals(info)){
                    Toast.makeText(context,"Tugas berhasil di edit!",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context,"Tugas gagal di edit!",Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
