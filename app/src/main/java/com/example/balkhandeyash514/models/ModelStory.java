package com.example.balkhandeyash514.models;

public class ModelStory {
    String name, image, uid;
    boolean isBlocked = false;

    public ModelStory(){

    }

    public ModelStory(String name, String image, String uid, boolean isBlocked) {
        this.name = name;
        this.image = image;
        this.uid = uid;
        this.isBlocked = isBlocked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}
