package com.example.jonathanashcraft.pinnacleconnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * Holds the data that is needed to make a maintenance request.
 */
public class MaintenanceRequest extends Request {
    private boolean urgent;
    private Bitmap image;

    public MaintenanceRequest(String title, String description, User user, boolean urgent, Bitmap image) {
        super(title, description, user);
        this.urgent = urgent;
        this.image = image;
    }

    public MaintenanceRequest(String title, String description, User user, boolean urgent) {
        super(title, description,user);
        this.urgent = urgent;
        this.image = null;
    }

    public MaintenanceRequest() {
        super("no_title", "no_description", new User());
        this.urgent = false;
        this.image = null;
    }

    public boolean isUrgent() { return urgent; }

    public void setUrgent(boolean urgent) { this.urgent = urgent; }

    public Bitmap getImage() { return image; }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
