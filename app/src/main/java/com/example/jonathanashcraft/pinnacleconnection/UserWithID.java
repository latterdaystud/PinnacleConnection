package com.example.jonathanashcraft.pinnacleconnection;


/**
 * Created by Jonathan Ashcraft on 4/4/2018.
 */

public class UserWithID extends User {
    private String ID;
    private Boolean newMessage;
    UserWithID() {
        super();
        ID = "";
        newMessage = false;
    }
    UserWithID(String firstName, String lastName, String apt, boolean manager, String device_token, String ID) {
        super(firstName, lastName, apt, manager, device_token);
        this.ID = ID;
        newMessage = false;
    }

    UserWithID(User user, String ID) {
        super(user);
        this.ID = ID;
        newMessage = false;
    }

    UserWithID(UserWithID tempUser) {
        super(tempUser.getFirstName(), tempUser.getLastName(), tempUser.getApartmentNumber(), tempUser.isManager(), tempUser.getDeviceToken());
        this.ID = tempUser.getID();
        this.setNewMessage(tempUser.getNewMessage());
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Boolean getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(Boolean newMessage) {
        this.newMessage = newMessage;
    }
}
