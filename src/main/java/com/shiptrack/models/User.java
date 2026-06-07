package com.shiptrack.models;


public class User {

    private int id;
    private String username;
    private String password;   
    private String role;       
    private boolean isLocked;  
    private int failedAttempts; 

    public User(int id, String username, String password, String role, boolean isLocked, int failedAttempts) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isLocked = isLocked;
        this.failedAttempts = failedAttempts;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public boolean isLocked() { return isLocked; }
    public int getFailedAttempts() { return failedAttempts; }

    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setLocked(boolean locked) { this.isLocked = locked; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }
}