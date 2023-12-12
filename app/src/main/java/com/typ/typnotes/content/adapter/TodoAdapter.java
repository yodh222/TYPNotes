package com.typ.typnotes.content.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.typ.typnotes.R;
import com.typ.typnotes.content.api.DeleteNote;
import com.typ.typnotes.content.api.DeleteTodo;
import com.typ.typnotes.content.api.EditTodo;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
    private Context context;
    private List<TodoItemData> todoList;

    public TodoAdapter(Context context, List<TodoItemData> todoList) {
        this.context = context;
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TodoItemData data = todoList.get(position);
        holder.checkBox.setChecked(data.getStatus());
        holder.checkBox.setText(data.getData());
        holder.id.setText(data.getId());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditTodo editTodo = new EditTodo(context);
                if (isChecked){
                    editTodo.setStatus(true);
                    editTodo.setData(data.getData());
                    editTodo.setId(data.getId());
                    editTodo.execute();
                }else{
                    editTodo.setStatus(false);
                    editTodo.setData(data.getData());
                    editTodo.setId(data.getId());
                    editTodo.execute();
                }
            }
        });
    }

//    Get Id from position
    public TodoItemData getItemAtPosition(int position) {
        return todoList.get(position);
    }

    //    Show Alert for Delete
    private void showAlert(Context context, String title, String message, String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Inflate custom view
        View customView = LayoutInflater.from(context).inflate(R.layout.layout_alert, null);
        builder.setView(customView);

        // Set title and message
        TextView alertTitle = customView.findViewById(R.id.alertTitle);
        TextView alertMessage = customView.findViewById(R.id.alertMessage);
        alertTitle.setText(title);
        alertMessage.setText(message);

        // Set positive button
        builder.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new DeleteTodo(context, id).execute();
            }
        });

        // Set negative button
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();

        // Set background color
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.white);

        // Show the dialog
        alertDialog.show();

        // Set text color after the dialog is shown
        alertTitle.setTextColor(Color.BLACK);
        alertMessage.setTextColor(Color.BLACK);
    }


    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CheckBox checkBox;
        CardView cardView;
        TextView id;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.todoCheck);
            cardView = itemView.findViewById(R.id.cardContent);
            id = itemView.findViewById(R.id.idTodo);
        }
    }
}
