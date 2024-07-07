package com.example.IT3A_PartialApps_grp11;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class UserSubsidy {
    private String FullName;
    private String SubsidyStatus;
    private String userId;
    private Timestamp timestamp;

    private String UserEmail;

    private String PhoneNumber;

    public UserSubsidy() {
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getSubsidyStatus() {
        return SubsidyStatus;
    }

    public void setSubsidyStatus(String subsidyStatus) {
        this.SubsidyStatus = subsidyStatus;
    }

    public String getEmail() {
        return UserEmail;
    }

    public void setEmail(String email) {
        this.UserEmail = email;
    }
    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

}

