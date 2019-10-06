package com.example.chattingapplication;

public class Users {

    private String UID;
    private String name;

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setImageComp(String imageComp) {
        this.imageComp = imageComp;
    }

    private String image;
    private String imageComp;

    public String getUID() {
        return UID;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getImageComp() {
        return imageComp;
    }

    public Users(String UID, String name, String image, String imageComp) {
        this.UID = UID;
        this.name = name;
        this.image = image;
        this.imageComp = imageComp;
    }

    public Users() {
    }
}
