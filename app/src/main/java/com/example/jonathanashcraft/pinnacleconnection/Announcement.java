package com.example.jonathanashcraft.pinnacleconnection;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;

import java.text.SimpleDateFormat;
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
    private boolean isDefault;
    private String pathToImage;
    private byte[] imageAsBytes;

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
        this.pathToImage = "no_path";


        SimpleDateFormat df = new SimpleDateFormat("h:mma, EEE, MMM d");
        String date = df.format(Calendar.getInstance().getTime());

        this.timeOfAnnouncement = date;

    }

    /**
     * Non-default constructor for announcement class
     * @param Title Title of the announcement
     * @param Body The body of the Announcement
     * @param Date The date of the activity
     * @param time The time of the activity
     * @param Author The author of the activity
     * @param pathToImage The path to the image that will be posted with the announcement
     */
    public Announcement(String Title, String Body, String Date, String time, String Author, String pathToImage) {
        this.body = Body;
        this.ID = Calendar.getInstance().getTime().toString();
        this.author = Author;
        this.title = Title;
        this.date = Date;
        this.pathToImage = pathToImage;


        SimpleDateFormat df = new SimpleDateFormat("h:mma, EEE, MMM d");
        String date = df.format(Calendar.getInstance().getTime());

        this.timeOfAnnouncement = date;


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

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public byte[] getImageAsBytes() {
        return imageAsBytes;
    }

    public void setImageAsBytes(byte[] imageAsBytes) {
        this.imageAsBytes = imageAsBytes;
    }

    public int getIndexInArray() {
        return indexInArray;
    }

    public void setIndexInArray(int indexInArray) {
        this.indexInArray = indexInArray;
    }
}