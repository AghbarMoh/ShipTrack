package com.shiptrack.models;

public class Customer extends User {

    private String fullName;
    private String idNumber;    
    private String contactNumber;

    public Customer(int id, String username, String password, boolean isLocked, int failedAttempts,
                    String fullName, String idNumber, String contactNumber) {

        super(id, username, password, "customer", isLocked, failedAttempts);

        this.fullName = fullName;
        this.idNumber = idNumber;
        this.contactNumber = contactNumber;
    }

    public String getFullName() { return fullName; }
    public String getIdNumber() { return idNumber; }
    public String getContactNumber() { return contactNumber; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
}