package com.example.jonathanashcraft.pinnacleconnection;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;

import java.util.Calendar;
import java.util.Date;


/**
 * Class to create and retrieve data from announcements
 *
 * This class stores the different parts of an announcement
 *
 * @author Joseph Ridgley
 * @version 2018.01
 * @since 2018.01
 */
public class Announcement {
    private String body;
    // For use in the database when an announcement is created
    private String ID;
    private String author;
    private String timeOfAnnouncement;
    private String title;
    private String date;
    private int indexInArray;
    private Bitmap image;
    private boolean isDefault;

    /**
     * Default constructor for the announcement class
     *
     * This method sets the body, author, timeOfAnnouncement, title, and date to be empty.
     * It sets the ID to be the date and time of when the method was called.
     *
     */
    public Announcement() {
        this.body = "";
        this.ID = Calendar.getInstance().getTime().toString();
        this.author = "";
        this.timeOfAnnouncement = "";
        this.title = "";
        this.date = "";
    }

    /**
     *
     * @param Title
     * @param Body
     * @param Date
     * @param time
     * @param Author
     */
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

    public void setIndexInArray(int index) {
        this.indexInArray = index;
    }

    public int getIndexInArray() {
        return indexInArray;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean getDefault() {
        return isDefault;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}