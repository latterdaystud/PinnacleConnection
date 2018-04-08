package com.example.jonathanashcraft.pinnacleconnection;

/**
 * User class containing ID and if new message is found for purpose of conversations.
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

    /**
     * Non-default constructor receiving all desired values.
     * @param firstName First Name
     * @param lastName Last Name of user
     * @param apt Appartment number
     * @param manager If is manager.
     * @param device_token Device_token
     * @param ID ID of user.
     */
    UserWithID(String firstName, String lastName, String apt, boolean manager, String device_token, String ID) {
        super(firstName, lastName, apt, device_token, manager);
        this.ID = ID;
        newMessage = false;
    }

    UserWithID(User user, String ID) {
        super(user);
        this.ID = ID;
        newMessage = false;
    }

    /**
     * Copy constructor
     * @param tempUser User to be copied.
     */
    UserWithID(UserWithID tempUser) {
        super(tempUser.getFirstName(), tempUser.getLastName(), tempUser.getApartmentNumber(), tempUser.getDeviceToken(), tempUser.isManager());
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
