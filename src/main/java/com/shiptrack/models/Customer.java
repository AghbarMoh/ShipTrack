package com.shiptrack.models;

// Customer extends User and adds personal information
// A customer can create shipments and track their packages
public class Customer extends User {

    private String fullName;
    private String idNumber;    // national ID or passport number
    private String contactNumber;

    // Constructor
    public Customer(int id, String username, String password, boolean isLocked, int failedAttempts,
                    String fullName, String idNumber, String contactNumber) {

        // Call the parent User constructor with role set to "customer"
        super(id, username, password, "customer", isLocked, failedAttempts);

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