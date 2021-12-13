package com.konik.porio;

import java.util.HashMap;
import java.util.Map;

public class UserAllBooksModel  {

        private String name;
        private String total_data;
        private String catUID;
        private String photoURL;
        private String last_update;
        private String create_date;
        private int views;

    public UserAllBooksModel() {
    }

    public UserAllBooksModel(String name, String total_data, String catUID, String photoURL, String last_update, String create_date, int views) {
        this.name = name;
        this.total_data = total_data;
        this.catUID = catUID;
        this.photoURL = photoURL;
        this.last_update = last_update;
        this.create_date = create_date;
        this.views = views;
    }

    public String getName() {
        return name;
    }

    public String getTotal_data() {
        return total_data;
    }

    public String getCatUID() {
        return catUID;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getLast_update() {
        return last_update;
    }

    public String getCreate_date() {
        return create_date;
    }

    public int getViews() {
        return views;
    }
}
