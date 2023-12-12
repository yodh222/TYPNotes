package com.typ.typnotes.content.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.typ.typnotes.Session.SessionManager;
import com.typ.typnotes.content.adapter.NoteAdapter;
import com.typ.typnotes.content.adapter.NoteItemData;
import com.typ.typnotes.content.adapter.TodoAdapter;
import com.typ.typnotes.content.adapter.TodoItemData;

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

public class GetTodo extends AsyncTask<String, Void, String> {

    private Context context;
    public static TodoAdapter todoAdapter;
    private RecyclerView recyclerView;
    public static List<TodoItemData> list;

    public GetTodo(Context context, TodoAdapter todoAdapter, RecyclerView recyclerView, List<TodoItemData> list) {
        this.context = context;
        this.todoAdapter = todoAdapter;
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

            URL url = new URL("https://a224-180-178-94-67.ngrok-free.app/Todo");
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
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        getResponse(s);
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
                    String data = dataObject.getString("data");
                    boolean status = dataObject.getBoolean("status"); // Menggunakan getBoolean

                    TodoItemData todoItemData = new TodoItemData();
                    todoItemData.id = id;
                    todoItemData.data = data;
                    todoItemData.status = status;
                    list.add(todoItemData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.todoAdapter = new TodoAdapter(context, list);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            this.recyclerView.setAdapter(this.todoAdapter);
        }
    }

}
