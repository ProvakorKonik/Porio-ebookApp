package com.konik.porio;

import com.google.firebase.firestore.Exclude;

public class BookInfoModel {
    
    private String name;
    private String code;
    private String summary;
    private String author;
    private String total_data;
    private String photoURL;
    private String getId;
    private String pdf;
    public BookInfoModel() {
    }



    public BookInfoModel(String getId, String name, String code, String summary, String author, String total_data, String photoURL, String pdf) {
        this.getId = getId;
        this.name = name;
        this.code = code;
        this.summary = summary;
        this.author = author;
        this.total_data = total_data;
        this.photoURL = photoURL;
        this.pdf = pdf;
    }
    @Exclude
    public String getGetId() {
        return getId;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getSummary() {
        return summary;
    }

    public String getAuthor() {
        return author;
    }

    public String getTotal_data() {
        return total_data;
    }

    public String getPhotoURL() {
        return photoURL;
    }
    public String getPdf() {
        return pdf;
    }
}
