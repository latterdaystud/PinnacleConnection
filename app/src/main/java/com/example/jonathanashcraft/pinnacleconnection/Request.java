package com.example.jonathanashcraft.pinnacleconnection;

import java.util.Calendar;

/**
 * Created by Joseph on 3/2/2018.
 */

public class Request {
    private String description;
    private String title;
    private User user;
    private String timeSent;

    public Request(String title, String description, User user) {
        this.title = title;
        this.description = description;
        this.user = user;
        setTimeSent(Calendar.getInstance().getTime().toString());
    }

    public Request() {
        this.title = "";
        this.description = "";
        this.user = null;
        setTimeSent(Calendar.getInstance().getTime().toString());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) { this.description = description; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimeSent() { return timeSent;}

    public void setTimeSent( String timeSent) { this.timeSent = timeSent; }

}
