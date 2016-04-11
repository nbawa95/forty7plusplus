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

    /**
     * The Users Info
     * @param id user's id
     * @param name user's name
     * @param major user's major
     * @param isAdmin user's administrative access
     */
    public User(String id, String name, String major, Boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.major = major;
        this.admin = isAdmin;
    }

    /**
     * sets ID
     * @param id the user id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * sets name
     * @param name the users's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * sets major
     * @param major the user's major
     */
    public void setMajor(String major) {
        this.major = major;
    }

    /**
     * Sets Block
     * @param isBlocked states in which a user is blocked
     */
    public void setBlocked(Boolean isBlocked) { this.blocked = isBlocked; }

    /**
     * sets Lock
     * @param isLocked state in which a user is locked
     */
    public void setLocked(Boolean isLocked) {locked = isLocked;}

    /**
     * gets user ID
     * @return ID
     */
    public String getId() {
        return id;
    }

    /**
     * gets Name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * gets major
     * @return major
     */
    public String getMajor() {
        return major;
    }

    /**
     * Whether or not a user is an Admin
     * @return admin
     */
    public Boolean isAdmin() { return admin; }

    /**
     * Is a user blocked?
     * @return blocked state
     */
    public Boolean isBlocked() { return blocked; }

    /**
     * Is a user locked?
     * @return locked state
     */
    public Boolean isLocked() {return locked;}
}
