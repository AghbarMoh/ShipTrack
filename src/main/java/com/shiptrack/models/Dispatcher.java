package com.shiptrack.models;

// Dispatcher extends User and adds personal information
// A dispatcher can assign deliveries to drivers and update delivery status
public class Dispatcher extends User {

    private String fullName;
    private String idNumber;
    private String contactNumber;

    // Constructor
    public Dispatcher(int id, String username, String password, boolean isLocked, int failedAttempts,
                      String fullName, String idNumber, String contactNumber) {

        // Call the parent User constructor with role set to "dispatcher"
        super(id, username, password, "dispatcher", isLocked, failedAttempts);

        this.fullName = fullName;
        this.idNumber = idNumber;
        this.contactNumber = contactNumber;
    }

    // Getters
    public String getFullName() { return fullName; }
    public String getIdNumber() { return idNumber; }
    public String getContactNumber() { return contactNumber; }

    // Setters
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
}