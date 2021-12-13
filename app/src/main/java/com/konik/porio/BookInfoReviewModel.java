package com.konik.porio;

public class BookInfoReviewModel {
    private String user_uid;
    private String user_review;
    private Double user_rating;
    private String user_time;
    private String admin_reply;
    private String admin_id;

    public BookInfoReviewModel() {
    }

    public BookInfoReviewModel(String user_uid, String user_review, Double user_rating, String user_time, String admin_reply, String admin_id) {
        this.user_uid = user_uid;
        this.user_review = user_review;
        this.user_rating = user_rating;
        this.user_time = user_time;
        this.admin_reply = admin_reply;
        this.admin_id = admin_id;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public String getUser_review() {
        return user_review;
    }

    public Double getUser_rating() {
        return user_rating;
    }

    public String getUser_time() {
        return user_time;
    }

    public String getAdmin_reply() {
        return admin_reply;
    }

    public String getAdmin_id() {
        return admin_id;
    }
}
