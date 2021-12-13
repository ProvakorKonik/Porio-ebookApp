package com.konik.porio;

public class DiscussionCommentModel {
    private String c_topic;
    private String c_author;
    private String c_time;
    private String c_code;  //1A0L0C

    public DiscussionCommentModel(String c_topic, String c_author, String c_time, String c_code) {
        this.c_topic = c_topic;
        this.c_author = c_author;
        this.c_time = c_time;
        this.c_code = c_code;
    }

    public DiscussionCommentModel() {
    }

    public String getC_topic() {
        return c_topic;
    }

    public String getC_author() {
        return c_author;
    }

    public String getC_time() {
        return c_time;
    }

    public String getC_code() {
        return c_code;
    }
}
