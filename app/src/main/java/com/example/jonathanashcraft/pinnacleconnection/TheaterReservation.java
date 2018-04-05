package com.example.jonathanashcraft.pinnacleconnection;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jonathan Ashcraft on 3/24/2018.
 */

public class TheaterReservation {
    private Date startTime;
    private Date endTime;
    private User user;
    private String ID;

    public  TheaterReservation() {
        this.startTime = new Date();
        this.endTime = new Date();
        this.user = new User();
        this.ID = Calendar.getInstance().getTime().toString();
    }

    public TheaterReservation(Date startTime, Date endTime, User user) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.user = user;
        this.ID = Calendar.getInstance().getTime().toString();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
