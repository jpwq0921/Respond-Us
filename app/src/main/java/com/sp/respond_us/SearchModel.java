package com.sp.respond_us;

import com.google.firebase.database.PropertyName;

public class SearchModel {
    private String phoneNumber, email, userName;

    public SearchModel() {
    }

    public SearchModel(String phoneNumber, String email, String userName) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.userName = userName;
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
}
