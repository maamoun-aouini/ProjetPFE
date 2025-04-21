package com.example.OnlineSellingApplicationBackend.DTO;

public class GoogleLoginRequest {
    private String idToken;
    private String fcmToken;  // Optional, for push notifications

    // Default constructor
    public GoogleLoginRequest() {}

    // Constructor with parameters
    public GoogleLoginRequest(String idToken, String fcmToken) {
        this.idToken = idToken;
        this.fcmToken = fcmToken;
    }

    // Getters and setters
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}