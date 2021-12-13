package com.konik.porio;

public class CategoryLinkModel {

    private String name;
    private String sector;
    private String catUID;
    private int serial;

    public CategoryLinkModel(String name, String sector, String catUID, int serial) {
        this.name = name;
        this.sector = sector;
        this.catUID = catUID;
        this.serial = serial;
    }

    public CategoryLinkModel() {
    }

    public String getName() {
        return name;
    }

    public String getSector() {
        return sector;
    }

    public String getCatUID() {
        return catUID;
    }

    public int getSerial() {
        return serial;
    }
}
