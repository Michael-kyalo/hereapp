package com.mikonski.happa.Models;

public class EventCLick {
    String uid;
    String title;
    String image;
    String time;
    String Date;
    String description;
    String id;

    public EventCLick() {
    }

    public EventCLick(String uid, String title, String image, String time, String date, String description, String id) {
        this.uid = uid;
        this.title = title;
        this.image = image;
        this.time = time;
        Date = date;
        this.description = description;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

