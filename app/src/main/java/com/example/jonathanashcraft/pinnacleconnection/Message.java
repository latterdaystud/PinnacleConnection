package com.example.jonathanashcraft.pinnacleconnection;

/**
 * This is used to hold the information of sending texts
 * Created by Joseph on 4/3/2018.
 */
public class Message {

    private String To;
    private String From;
    private String Body;
    private String time;

    public Message() {
        To = "";
        From = "";
        Body = "";
        time = "";
    }

    public Message(String To, String From, String Body, String time) {
        this.To = To;
        this.From = From;
        this.Body = Body;
        this.time = time;
    }

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

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
