package com.personal.lifttoliveapp.entities;

public class UserWorkout {
    private String name, userId;
    private long createdOn, completedOn;
    private int id;

    public UserWorkout() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserWorkout(int id, String name, long created, long completed, String userId) {
        this.id = id;
        this.name = name;
        this.createdOn = created;
        this.completedOn = completed;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public long getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(long completedOn) {
        this.completedOn = completedOn;
    }
}
