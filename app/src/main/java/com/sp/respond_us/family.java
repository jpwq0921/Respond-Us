package com.sp.respond_us;

public class family {
    private String userName;
    private String description;
    private String uID;

    public family(String userName, String description) {
        this.userName = userName;
        this.description = description;
    }

    public family() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }
}
