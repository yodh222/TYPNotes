package com.typ.typnotes.content.api;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

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

public class DeleteTodo extends AsyncTask<String, Void, String> {
    private Context context;
    private String id;

    public DeleteTodo(Context context, String id) {
        this.context = context;
        this.id = id;
    }

    @Override
    protected String doInBackground(String... strings) {
        return performDeleteTodo();
    }

    private String performDeleteTodo() {
        try {
            SessionManager sessionManager = new SessionManager(context);
            String cookieSession = sessionManager.getSession();

            URL url = new URL("https://a224-180-178-94-67.ngrok-free.app/Todo");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Cookie", "User=" + cookieSession);
            urlConnection.setDoOutput(true);

            String setRequest = "req=deleteData&idTodo=" + id;
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
                if ("Berhasil menghapus todo list".equals(info)){
                    Toast.makeText(context,"Tugas berhasil di dihapus!",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context,"Tugas gagal di dihapus!",Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}