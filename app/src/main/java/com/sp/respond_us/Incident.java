package com.sp.respond_us;

public class Incident {
    private String dateOfIncident;
    private String timeOfIncident;
    private String offenderName;
    private String address;

    public Incident(String dateOfIncident, String timeOfIncident, String offenderName,String address) {
        this.dateOfIncident = dateOfIncident;
        this.timeOfIncident = timeOfIncident;
        this.offenderName = offenderName;
        this.address=address;
    }

    public Incident() {
    }

    public String getDateOfIncident() {
        return dateOfIncident;
    }

    public void setDateOfIncident(String dateOfIncident) {
        this.dateOfIncident = dateOfIncident;
    }

    public String getTimeOfIncident() {
        return timeOfIncident;
    }

    public void setTimeOfIncident(String timeOfIncident) {
        this.timeOfIncident = timeOfIncident;
    }

    public String getOffenderName() {
        return offenderName;
    }

    public void setOffenderName(String offenderName) {
        this.offenderName = offenderName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
