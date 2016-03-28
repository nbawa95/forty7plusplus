package com.example.ciyengar.myapplication;

/**
 * Created by Dhruv Sagar on 08-Mar-16.
 */
public class User {
    private String id;
    private String name;
    private String major;
    private Boolean admin;
    private Boolean blocked, locked;

    public User(String id, String name, String major, Boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.major = major;
        this.admin = isAdmin;
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

    public void setBlocked(Boolean isBlocked) { this.blocked = isBlocked; }

    public void setLocked(Boolean isLocked) {locked = isLocked;}

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMajor() {
        return major;
    }

    public Boolean isAdmin() { return admin; }

    public Boolean isBlocked() { return blocked; }

    public Boolean isLocked() {return locked;}
}
