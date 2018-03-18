package com.example.jonathanashcraft.pinnacleconnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Jonathan Ashcraft on 3/16/2018.
 */

public class MaintenanceRequestSent extends Request {
    private boolean urgent;
    private Bitmap image;
    public MaintenanceRequestSent(String title, String description, User user, boolean urgent, Bitmap image) {
        super(title, description, user);
        this.urgent = urgent;
        this.image = image;
    }

    public boolean isUrgent() { return urgent; }

    public void setUrgent(boolean urgent) { this.urgent = urgent; }

    public Bitmap getImage() { return image; }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
