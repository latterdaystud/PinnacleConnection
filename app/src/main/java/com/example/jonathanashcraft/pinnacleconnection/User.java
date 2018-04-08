package com.example.jonathanashcraft.pinnacleconnection;

/**
 * An identity for a user
 * Created by Joseph on 3/2/2018.
 */
public class User {

    private String firstName;
    private String lastName;
    private String apartmentNumber;
    private String deviceToken;
    private boolean isManager;

    /**
     * Non-default constructor
     * @param firstName First name of the user
     * @param lastName Last name of the user
     * @param apt Apartment number of the user
     * @param deviceToken The device token (for notifications)
     * @param isManager To see if the user is a manager
     */
    User(String firstName, String lastName, String apt, String deviceToken, boolean isManager) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.apartmentNumber = apt;
        this.deviceToken = deviceToken;
        this.isManager = isManager;
    }

    /**
     * Default constructor
     */
    User() {
        this.firstName = "";
        this.lastName = "";
        this.apartmentNumber = "";
        this.isManager = false;
    }

    /**
     * Copy constructor
     * @param user The user to set the user to
     */
    User(User user) {
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.apartmentNumber = user.apartmentNumber;
        this.isManager = user.isManager;
        this.deviceToken = user.deviceToken;
    }

    public void setUser(User user) {
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.apartmentNumber = user.apartmentNumber;
        this.isManager = user.isManager;
        this.deviceToken = user.deviceToken;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}