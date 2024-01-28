package com.example.balkhandeyash514.models;

public class ModelChatList {

    String id; //we need this id to get chat List, sender/receiver uid

    public ModelChatList(String id) {
        this.id = id;
    }

    public ModelChatList() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
