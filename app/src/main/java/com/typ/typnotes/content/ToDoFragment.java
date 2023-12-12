package com.typ.typnotes.content;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.typ.typnotes.R;
import com.typ.typnotes.Session.SessionManager;
import com.typ.typnotes.content.adapter.TodoAdapter;
import com.typ.typnotes.content.adapter.TodoItemData;
import com.typ.typnotes.content.api.DeleteTodo;
import com.typ.typnotes.content.api.EditTodo;
import com.typ.typnotes.content.api.GetTodo;
import com.typ.typnotes.content.note.NoteAdd;

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

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ToDoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToDoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ToDoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ToDoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ToDoFragment newInstance(String param1, String param2) {
        ToDoFragment fragment = new ToDoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private RecyclerView recyclerView;
    private TodoAdapter todoAdapter;
    private List<TodoItemData> todoList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button buttonAdd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        todoList = new ArrayList<>();
        todoAdapter = new TodoAdapter(getContext(), todoList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(todoAdapter);
        new GetTodo(getContext(), todoAdapter, recyclerView, todoList).execute();

        swipeRefreshLayout =view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetTodo(getContext(), todoAdapter, recyclerView, todoList).execute();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        buttonAdd = view.findViewById(R.id.addbtn);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NoteAdd.class);
                showBottomSheetDialog();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }
//    Swipe for Edit and Delete
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            TodoItemData swipedItem = GetTodo.todoAdapter.getItemAtPosition(position);
            switch (direction){
                case ItemTouchHelper.LEFT:
                    showBottomSheetDialogEdit(swipedItem.data, swipedItem.id);
                    break;
                case ItemTouchHelper.RIGHT:
                    new DeleteTodo(getContext(), swipedItem.id).execute();
                    GetTodo.list.remove(position);
                    break;
            }
        }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        new RecyclerViewSwipeDecorator.Builder(getContext(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(Color.parseColor("#FFA500"))
                .addSwipeLeftActionIcon(R.drawable.baseline_edit_24)
                .addSwipeRightBackgroundColor(Color.parseColor("#FF0000"))
                .addSwipeRightActionIcon(R.drawable.baseline_delete_24)
                .create()
                .decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
};





//    ===========================================
//    AsyncTask Process
    private class AddTodo extends AsyncTask<String, Void, String> {

        private String data;

        public AddTodo(String data) {
            this.data = data;
        }

        @Override
        protected String doInBackground(String... strings) {
            return performAddTodo();
        }


        private String performAddTodo() {
            try {
                SessionManager sessionManager = new SessionManager(getContext());
                String cookieSession = sessionManager.getSession();

                URL url = new URL("https://a224-180-178-94-67.ngrok-free.app/Todo");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Cookie", "User=" + cookieSession);
                urlConnection.setDoOutput(true);

                String strData = data;
                boolean strStatus = false;

                String setRequest = "req=addData&data=" + strData+"&status="+strStatus;
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
                    if ("Berhasil menambahkan todo list".equals(info)){
                        Toast.makeText(getContext(),"Tugas berhasil di ditambahkan!",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(),"Tugas gagal di ditambahkan!",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    ==================================================
//        Show Edit field on bottom with BottomSheetView
    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());

        View bottomSheetView = LayoutInflater.from(requireContext())
                .inflate(R.layout.new_todo, requireActivity().findViewById(R.id.layout));

        EditText editTextTask = bottomSheetView.findViewById(R.id.newTaskText);
        Button btnSave = bottomSheetView.findViewById(R.id.newTaskButton);

        // Atur onClickListener untuk tombol "Save"
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String task = editTextTask.getText().toString().trim();

                if (!TextUtils.isEmpty(task)) {
                    new AddTodo(task).execute();
                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Penambahan tugas tidak bisa kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Atur TextWatcher untuk EditText untuk memantau perubahan isinya
        editTextTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Tidak digunakan
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    btnSave.setBackgroundColor(Color.parseColor("#1E90FF")); // Warna background
                    btnSave.setTextColor(Color.WHITE); // Warna teks
                } else {
                    btnSave.setBackgroundColor(Color.parseColor("#939292")); // Kembalikan warna background
                    btnSave.setTextColor(Color.WHITE); // Kembalikan warna teks
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Tidak digunakan
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
//    ==================================================
//        Show Edit field on bottom with BottomSheetView
    private void showBottomSheetDialogEdit(String s,String id) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());

        View bottomSheetView = LayoutInflater.from(requireContext())
                .inflate(R.layout.new_todo, requireActivity().findViewById(R.id.layout));

        EditText editTextTask = bottomSheetView.findViewById(R.id.newTaskText);
        Button btnSave = bottomSheetView.findViewById(R.id.newTaskButton);

        editTextTask.setText(s);

        // Atur onClickListener untuk tombol "Save"
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String task = editTextTask.getText().toString().trim();

                if (!TextUtils.isEmpty(task)) {
                    EditTodo editTodo = new EditTodo(getContext());
                    editTodo.setData(task);
                    editTodo.setId(id);
                    editTodo.execute();
                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Penambahan tugas tidak bisa kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Atur TextWatcher untuk EditText untuk memantau perubahan isinya
        editTextTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Tidak digunakan
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    btnSave.setBackgroundColor(Color.parseColor("#1E90FF")); // Warna background
                    btnSave.setTextColor(Color.WHITE); // Warna teks
                } else {
                    btnSave.setBackgroundColor(Color.parseColor("#939292")); // Kembalikan warna background
                    btnSave.setTextColor(Color.WHITE); // Kembalikan warna teks
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Tidak digunakan
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

}