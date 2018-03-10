package com.example.jonathanashcraft.pinnacleconnection;

import android.text.Editable;

import java.util.Date;

/**
 * Created by Joseph on 3/9/2018.
 */

public class Announcement {
    private String body;
    private Date datePosted;
    private String author;
    private String timePeriod;
    private String title;
    private String date;


    public Announcement() {
        this.body = "This is the first post and just a test. Do not show up! You will be disappointed";

        // Currently null, will add method to give it the current date when the annoucement is created
        datePosted = null;
        this.author = "Joseph Swag";

        this.timePeriod = "10/3/1887";
        this.title = "First Announcement";
        this.date = "today";
    }

    public Announcement(String title, String timePeriod, String date, String body) {

        this.body = body;

        // Currently null, will add method to give it the current date when the annoucement is created
        datePosted = null;
        this.author = null;

        this.timePeriod = timePeriod;
        this.title = title;
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // A string representing the file location of the image
    private String imageFileName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }
}
