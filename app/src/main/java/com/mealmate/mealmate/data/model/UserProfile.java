package com.mealmate.mealmate.data.model;

public class UserProfile {
    private String uid; // Unique identifier
    private String fullName;
    private String email;
    private String phone;
    private String imageUrl;
    private String dietaryPreference;

    // Default Constructor
    public UserProfile() {
    }

    // Parameterized Constructor
    public UserProfile(String uid, String fullName, String email, String phone, String imageUrl, String dietaryPreference) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.dietaryPreference = dietaryPreference;
    }

    // Getters
    public String getUid() {
        return uid;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDietaryPreference() {
        return dietaryPreference;
    }

    // Setters
    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDietaryPreference(String dietaryPreference) {
        this.dietaryPreference = dietaryPreference;
    }

    // Optional: Override toString() for easy printing
    @Override
    public String toString() {
        return "UserProfile {" +
                "uid='" + uid + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", dietaryPreference='" + dietaryPreference + '\'' +
                '}';
    }
}
