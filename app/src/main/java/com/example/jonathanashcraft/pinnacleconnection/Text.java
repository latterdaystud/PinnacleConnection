package com.example.jonathanashcraft.pinnacleconnection;

/**
 * This is used to hold the information of sending texts
 * Created by Joseph on 4/3/2018.
 */
public class Text {

    private String To;
    private String From;
    private String Message;

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
