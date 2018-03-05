package com.example.jonathanashcraft.pinnacleconnection;

/**
 * Created by Joseph on 3/2/2018.
 */

public class Request {
    String description;
    String title;
    User user;

    public void Request(String title, String description, User user) {
        this.title = title;
        this.description = description;
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
