package com.konik.porio;

import com.google.firebase.firestore.Exclude;

public class S_DocsBatchClassModel {
    private String getId;
    private String topic;
    private String no;
    private String date;
    private String note;
    private String by;

    public S_DocsBatchClassModel() {
    }

    public S_DocsBatchClassModel(String getId, String topic, String no, String date, String note, String by) {
        this.getId = getId;
        this.topic = topic;
        this.no = no;
        this.date = date;
        this.note = note;
        this.by = by;
    }
    @Exclude
    public String getGetId() {
        return getId;
    }

    public String getTopic() {
        return topic;
    }

    public String getNo() {
        return no;
    }

    public String getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }

    public String getBy() {
        return by;
    }
}
