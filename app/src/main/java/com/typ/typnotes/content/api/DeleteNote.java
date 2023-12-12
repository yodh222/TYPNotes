package com.typ.typnotes.content.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.typ.typnotes.Session.SessionManager;
import com.typ.typnotes.content.adapter.NoteAdapter;
import com.typ.typnotes.content.adapter.NoteItemData;
import com.typ.typnotes.content.note.NoteEdit;

import org.json.JSONArray;
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
import java.util.ArrayList;

public class DeleteNote extends AsyncTask<String, Void, String> {

    private Context context;

    private String id;

    public DeleteNote(Context context, String id) {
        this.context = context;
        this.id = id;
    }

    @Override
    protected String doInBackground(String... strings) {
        return performDeleteNote();
    }

    private String performDeleteNote() {
        try {
            SessionManager sessionManager = new SessionManager(context);
            String cookieSession = sessionManager.getSession();

            URL url = new URL("https://a224-180-178-94-67.ngrok-free.app/Storage");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Cookie", "User=" + cookieSession);
            urlConnection.setDoOutput(true);

            String setRequest = "req=deleteData&idNote=" + id;
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
            Log.d("TestingBg",response.toString());
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
                if ("Berhasil menghapus note".equals(info)){
                    Toast.makeText(context,"Catatan berhasil di dihapus!",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context,"Catatan gagal di dihapus!",Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
