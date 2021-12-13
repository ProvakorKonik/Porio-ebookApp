package com.konik.porio;

import com.google.firebase.firestore.Exclude;

public class S_DocsBatchModel {
    private String getId;
    private String batch_name;
    private String uploaded_by;
    private String teacher;
    private String total_data;
    private int like;

    public S_DocsBatchModel() {
    }

    public S_DocsBatchModel(String getId, String batch_name, String uploaded_by, String teacher, String total_data, int like) {
        this.getId = getId;
        this.batch_name = batch_name;
        this.uploaded_by = uploaded_by;
        this.teacher = teacher;
        this.total_data = total_data;
        this.like = like;
    }
    @Exclude
    public String getGetId() {
        return getId;
    }

    public String getBatch_name() {
        return batch_name;
    }

    public String getUploaded_by() {
        return uploaded_by;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getTotal_data() {
        return total_data;
    }

    public int getLike() {
        return like;
    }
}
