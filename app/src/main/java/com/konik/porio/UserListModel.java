package com.konik.porio;

public class UserListModel {
    private String name;
    private String email;
    private String birth_reg;
    private String uid;
    private String bio;
    private String photoURL;
    private String lastActivity;
    private String phone_no;
    private String total;

    public UserListModel() {
    }

    public UserListModel(String name, String email, String birth_reg, String uid, String bio, String photoURL, String lastActivity, String phone_no, String total) {
        this.name = name;
        this.email = email;
        this.birth_reg = birth_reg;
        this.uid = uid;
        this.bio = bio;
        this.photoURL = photoURL;
        this.lastActivity = lastActivity;
        this.phone_no = phone_no;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBirth_reg() {
        return birth_reg;
    }

    public String getUid() {
        return uid;
    }

    public String getBio() {
        return bio;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getLastActivity() {
        return lastActivity;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public String getTotal() {
        return total;
    }
}
