package com.sp.respond_us;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class SearchModel implements Serializable {
    private String phoneNumber, email, userName, uID;

    public SearchModel() {
    }

    public SearchModel(String phoneNumber, String email, String userName, String uID) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.userName = userName;
        this.uID = uID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }
}
