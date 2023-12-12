package com.typ.typnotes.content.adapter;

public class TodoItemData {
    public String id, data;
    public boolean status; // Menggunakan boolean langsung

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
