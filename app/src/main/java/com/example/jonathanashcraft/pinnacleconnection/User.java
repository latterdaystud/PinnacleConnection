package com.example.jonathanashcraft.pinnacleconnection;

/**
 * Created by Joseph on 3/2/2018.
 */

public class User {
    String username;
    String password;
    int apartmentNumber;
    boolean isManager;
    String managerPassword;

    User(String username, String password, int apt) {
        this.username = username;
        this.password = password;
        apartmentNumber = apt;
        boolean isManager = false;

        // This can be loaded from the cloud, right now it will be fixed
        managerPassword = "555";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(int apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    public String getManagerPassword() {
        return managerPassword;
    }

    public void setManagerPassword(String managerPassword) {
        this.managerPassword = managerPassword;
    }

    public void validateManager(String password) {
        if (managerPassword == password) {
            isManager = true;
        } else {
            isManager = false;
        }
    }
}
