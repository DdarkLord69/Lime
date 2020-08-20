package com.example.lime.models;

public class UserinList {

    private String profilePicUrl;
    private String displayName;
    private String userId;

    public UserinList() {

    }

    public UserinList(String profilePicUrl, String displayName, String userId) {
        this.profilePicUrl = profilePicUrl;
        this.displayName = displayName;
        this.userId = userId;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
