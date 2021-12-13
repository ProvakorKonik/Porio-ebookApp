package com.konik.porio;

public class DiscussionModel  {

    private String topic;
    private String author;
    private String time;
    private String condition;
    private int like;
    private int comment;

    public DiscussionModel() {
    }
    public DiscussionModel(String topic, String author,  String time, String condition, int like, int comment) {
        this.topic = topic;
        this.author = author;

        this.time = time;
        this.condition = condition;
        this.like = like;
        this.comment = comment;
    }

    public String getTopic() {
        return topic;
    }

    public String getAuthor() {
        return author;
    }



    public String getTime() {
        return time;
    }

    public String getCondition() {
        return condition;
    }

    public int getLike() {
        return like;
    }

    public int getComment() {
        return comment;
    }
}
