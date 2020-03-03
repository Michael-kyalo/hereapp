package com.mikonski.happa.Models;

public class User {
    private String username;
    private String about;
    private String image;
    private String uid;

    public User() {
    }

    public User(String username, String about, String image, String uid) {
        this.username = username;
        this.about = about;
        this.image = image;
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
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
}

