package com.konik.porio;

public class DiscussionReplyModel {
    private String r_topic;
    private String r_author;
    private String r_time;
    private String r_code;  //1A0L0C

    public DiscussionReplyModel(String r_topic, String r_author, String r_time, String r_code) {
        this.r_topic = r_topic;
        this.r_author = r_author;
        this.r_time = r_time;
        this.r_code = r_code;
    }

    public DiscussionReplyModel() {
    }

    public String getR_topic() {
        return r_topic;
    }

    public String getR_author() {
        return r_author;
    }

    public String getR_time() {
        return r_time;
    }

    public String getR_code() {
        return r_code;
    }
}
