package com.example.jonathanashcraft.pinnacleconnection;

import java.util.Date;

/**
 * Created by Joseph on 3/9/2018.
 */

public class Announcement {
    private String Announcement;
    private Date datePosted;
    private String author;

    // A string representing the file location of the image
    private String imageFileName;

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getAnnouncement() {
        return Announcement;
    }

    public void setAnnouncement(String announcement) {
        Announcement = announcement;
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
}
