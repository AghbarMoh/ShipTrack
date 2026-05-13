package com.shiptrack.models;

// DeliveryPersonnel extends User and adds personal information
// A delivery person can view assigned deliveries and update their status
public class DeliveryPersonnel extends User {

    private String fullName;
    private String idNumber;
    private String contactNumber;

    // Constructor
    public DeliveryPersonnel(int id, String username, String password, boolean isLocked, int failedAttempts,
                             String fullName, String idNumber, String contactNumber) {

        // Call the parent User constructor with role set to "delivery"
        super(id, username, password, "delivery", isLocked, failedAttempts);

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