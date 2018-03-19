package com.example.jonathanashcraft.pinnacleconnection;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joseph on 3/9/2018.
 */

public class Announcement {
    private String body;
    // For use in the database when an announcement is created
    private String ID;
    private String author;
    private String timeOfAnnouncement;
    private String title;
    private String date;

    public Announcement() {
        this.body = "";
        this.ID = Calendar.getInstance().getTime().toString();
        this.author = "";
        this.timeOfAnnouncement = "";
        this.title = "";
        this.date = "";
    }

    public Announcement(String Title, String Body, String Date, String time, String Author) {
        this.body = Body;
        this.author = Author;
        this.title = Title;
        this.timeOfAnnouncement = time;
        this.date = Date;

        // The ID will be the date and time of the announcement being created
        this.ID = Calendar.getInstance().getTime().toString();

        int year = Calendar.getInstance().get(Calendar.YEAR);

    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTimeOfAnnouncement() {
        return timeOfAnnouncement;
    }

    public void setTimeOfAnnouncement(String timeOfAnnouncement) {
        this.timeOfAnnouncement = timeOfAnnouncement;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}