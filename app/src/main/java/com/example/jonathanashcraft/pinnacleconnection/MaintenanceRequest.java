package com.example.jonathanashcraft.pinnacleconnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * Holds the data that is needed to make a maintenance request.
 */
public class MaintenanceRequest extends Request {
    private boolean urgent;
    private Bitmap image;
    private String author;
    private String path;
    private byte[] bytes;

    public MaintenanceRequest(String title, String description, String author, boolean urgent, Bitmap image) {
        super(title, description, new User());
        this.urgent = urgent;
        this.image = image;
        this.author = author;

    }

    public MaintenanceRequest(String title, String description, String author, boolean urgent,
                              String path) {
        super(title, description, new User());
        this.urgent = urgent;
        this.image = null;
        this.author = author;
        this.path = path;
    }

    public MaintenanceRequest() {
        super("no_title", "no_description", new User());
        this.urgent = false;
        this.image = null;
        this.path = "no_path";
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isUrgent() { return urgent; }

    public void setUrgent(boolean urgent) { this.urgent = urgent; }

    public Bitmap getImage() { return image; }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
