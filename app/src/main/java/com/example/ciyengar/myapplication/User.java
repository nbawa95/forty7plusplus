package com.example.ciyengar.myapplication;

/**
 * Created by Dhruv Sagar on 08-Mar-16.
 */
public class User {
    private String id;
    private String name;
    private String major;

    public User(String id, String name, String major) {
        this.id = id;
        this.name = name;
        this.major = major;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMajor() {
        return major;
    }
}
