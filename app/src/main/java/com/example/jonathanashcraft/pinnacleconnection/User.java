package com.example.jonathanashcraft.pinnacleconnection;

/**
 * Created by Joseph on 3/2/2018.
 */

public class User {

    private String firstName;
    private String lastName;
    private String apartmentNumber;
    private String deviceToken;
    private boolean isManager;

    User(String firstName, String lastName, String apt, String deviceToken, boolean isManager) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.apartmentNumber = apt;
        this.deviceToken = deviceToken;
        this.isManager = isManager;
    }

    User() {
        this.firstName = "";
        this.lastName = "";
        this.apartmentNumber = "";
        this.isManager = false;
    }

    User(User user) {
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.apartmentNumber = user.apartmentNumber;
        this.isManager = user.isManager;
    }

    public void setUser(User user) {
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.apartmentNumber = user.apartmentNumber;
        this.isManager = user.isManager;
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
}