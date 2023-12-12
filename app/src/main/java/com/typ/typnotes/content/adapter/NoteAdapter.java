package com.typ.typnotes.content.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.typ.typnotes.R;
import com.typ.typnotes.content.Content;
import com.typ.typnotes.content.api.DeleteNote;
import com.typ.typnotes.content.api.DeleteTodo;
import com.typ.typnotes.content.api.GetNotes;
import com.typ.typnotes.content.note.NoteEdit;

import java.nio.channels.InterruptedByTimeoutException;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    Context context;
    List<NoteItemData> list;

    public NoteAdapter(Context context, List<NoteItemData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {
        NoteItemData data = list.get(position);
        holder.title.setText(data.judul);
        holder.prevData.setText(data.prev_data);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NoteEdit.class);

                intent.putExtra("Data",data.data);
                intent.putExtra("Id",data.id);
                intent.putExtra("Judul",data.judul);

                context.startActivity(intent);
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showAlert(context,"Hapus Note","Apakah anda yakin ingin menghapus catatan ini?", data.id);
                return true;
            }
        });
    }

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
                new DeleteNote(context, id).execute();
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
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title,prevData;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView);
            prevData = itemView.findViewById(R.id.textView2);
            cardView = itemView.findViewById(R.id.cardContent);
        }
    }
}
