package com.sp.respond_us;

public class User {

    public String phoneNumber, email, username, uID;

    /*public String getPhoneNumber() {
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
    }*/

    public User(String phoneNumber, String email, String username, String uID) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.username = username;
        this.uID = uID;
        //this.password = password;
    }

    public User() {

    }
}
