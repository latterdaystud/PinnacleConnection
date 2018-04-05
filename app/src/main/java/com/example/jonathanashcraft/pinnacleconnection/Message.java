package com.example.jonathanashcraft.pinnacleconnection;

/**
 * This is used to hold the information of sending texts
 * Created by Joseph on 4/3/2018.
 */
public class Message {

    public Message(String to, String from, String body) {
        To = to;
        From = from;
        Body = body;
    }

    private String To;
    private String From;
    private String Body;
    private String Time;

    public String getTime() { return Time; }

    public void setTime(String time) { Time = time; }

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
}
