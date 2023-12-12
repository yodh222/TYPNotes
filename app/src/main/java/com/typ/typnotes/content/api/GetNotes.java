package com.typ.typnotes.content.api;

import android.content.Context;
import android.os.AsyncTask;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.typ.typnotes.Login;
import com.typ.typnotes.Session.SessionManager;
import com.typ.typnotes.content.Content;
import com.typ.typnotes.content.adapter.NoteAdapter;
import com.typ.typnotes.content.adapter.NoteItemData;

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
import java.util.List;

public class GetNotes extends AsyncTask<String, Void,String> {
    private Context context;
    private NoteAdapter noteAdapter;
    private RecyclerView recyclerView;
    private List<NoteItemData> list;

    public GetNotes(Context context, NoteAdapter noteAdapter, RecyclerView recyclerView, List<NoteItemData> list) {
        this.context = context;
        this.noteAdapter = noteAdapter;
        this.recyclerView = recyclerView;
        this.list = list;
    }

    @Override
    protected String doInBackground(String... strings) {
        return getData();
    }

    private String getData(){
        try {
            SessionManager sessionManager = new SessionManager(context);
            String sessionCookie = sessionManager.getSession();

            URL url = new URL("https://a224-180-178-94-67.ngrok-free.app/Storage");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Cookie", "User=" + sessionCookie);
            urlConnection.setDoOutput(true);

            String requestBody = "req=getData";
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        getResponse(result);
    }
    private void getResponse(String result) {
         list = new ArrayList<>();
        if (result != null) {
            try {
                JSONObject jsonResponse = new JSONObject(result);
                JSONArray dataArray = jsonResponse.getJSONArray("Data");

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    String id = dataObject.getString("id");
                    String judul = dataObject.getString("judul");
                    String data = dataObject.getString("data");
                    String prevData = dataObject.getString("prev_data");

                    NoteItemData noteItemData = new NoteItemData();
                    noteItemData.id = id;
                    noteItemData.judul = judul;
                    noteItemData.data = data;
                    noteItemData.prev_data = prevData;
                    list.add(noteItemData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.noteAdapter=new NoteAdapter(context,list);
            this.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
            this.recyclerView.setAdapter(this.noteAdapter);
        }
    }


}
