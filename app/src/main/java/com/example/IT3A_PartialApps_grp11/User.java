package com.example.IT3A_PartialApps_grp11;

public class User {
    private String FullName;
    private String UserEmail;
    private String SubsidyStatus;
    private String PhoneNumber;
    private String FCMtoken;

    public User() {}

    // Getters and Setters
    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        this.FullName = fullName;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        this.UserEmail = userEmail;
    }

    public String getSubsidyStatus() {
        return SubsidyStatus;
    }

    public void setSubsidyStatus(String subsidyStatus) {
        this.SubsidyStatus = subsidyStatus;
    }

    public String getPhoneNumber() { return PhoneNumber; }

    public void setPhoneNumber(String phoneNumber) { PhoneNumber = phoneNumber; }

    public String getFcmToken() { return FCMtoken; }

    public void setFcmToken(String fcmToken) { this.FCMtoken = fcmToken; }
}
