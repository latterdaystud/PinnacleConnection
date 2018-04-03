package com.example.jonathanashcraft.pinnacleconnection;

/**
 * Created by Joseph on 3/2/2018.
 */

public class User {

    private String firstName;
    private String lastName;
    private String apartmentNumber;
    private boolean manager;
    private String device_token;

    User(String firstName, String lastName, String apt, boolean manager, String device_token) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.apartmentNumber = apt;
        this.manager = manager;
        this.device_token = device_token;
    }

    User() {
        this.firstName = "";
        this.lastName = "";
        this.apartmentNumber = "";
        this.manager = false;
        this.device_token = "";
    }

    User(User user) {
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.apartmentNumber = user.apartmentNumber;
        this.manager = user.manager;
        this.device_token = user.device_token;
    }

    public void setUser(User user) {
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.apartmentNumber = user.apartmentNumber;
        this.manager = user.manager;
        this.device_token = user.device_token;
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
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public String getDeviceToken() {
        return device_token;
    }

    public void setDeviceToken(String device_token) {
        this.device_token = device_token;
    }
}