package com.konik.porio;

import com.google.firebase.firestore.Exclude;

public class S_DBCFilesModel {
    private String getId;
    private String name;
    private String link;
    private String size;
    private String type;
    private String date;
    private String by;

    public S_DBCFilesModel() {
    }

    public S_DBCFilesModel(String getId, String name, String link, String size, String type, String date, String by) {
        this.getId = getId;
        this.name = name;
        this.link = link;
        this.size = size;
        this.type = type;
        this.date = date;
        this.by = by;
    }
    @Exclude
    public String getGetId() {
        return getId;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public String getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getBy() {
        return by;
    }
}
