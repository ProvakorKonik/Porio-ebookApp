package com.konik.porio;

public class ReadChapModel {
    private String chapterNmae;
    private String chapterText;
    private String chapterPhotoUrl;
    private String chapterPriority;

    public ReadChapModel(String chapterNmae, String chapterText, String chapterPhotoUrl, String chapterPriority) {
        this.chapterNmae = chapterNmae;
        this.chapterText = chapterText;
        this.chapterPhotoUrl = chapterPhotoUrl;
        this.chapterPriority = chapterPriority;
    }

    public ReadChapModel() {
    }

    public String getChapterNmae() {
        return chapterNmae;
    }

    public String getChapterText() {
        return chapterText;
    }

    public String getChapterPhotoUrl() {
        return chapterPhotoUrl;
    }

    public String getChapterPriority() {
        return chapterPriority;
    }
}
