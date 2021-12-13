package com.konik.porio;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;

public class CategoryModel {
    private String name ;
    private String type;
    private String priority;
    private String photo_url;
    private String total_data;
    private String getId;
    public CategoryModel() {
    }

    public CategoryModel(String getId,String name, String type, String priority, String photo_url, String total_data) {
        this.getId = getId;
        this.name = name;
        this.type = type;
        this.priority = priority;
        this.photo_url = photo_url;
        this.total_data = total_data;
    }
    @Exclude
    public String getGetId() {
        return getId;
    }




    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getPriority() {
        return priority;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public String getTotal_data() {
        return total_data;
    }
}
