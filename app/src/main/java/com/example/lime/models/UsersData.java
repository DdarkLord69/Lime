package com.example.lime.models;

import java.util.List;

public class UsersData {

    private String userId;
    private String displayName;
    private String profilePicUrl;
    private List<String> followers;
    private List<String> following;
    private int postCount;

    public UsersData() {
        //empty constructor for firestore idk
    }

    public UsersData(String userId, String displayName, String profilePicUrl, List<String> followers, List<String> following, int postCount) {
        this.userId = userId;
        this.displayName = displayName;
        this.profilePicUrl = profilePicUrl;
        this.followers = followers;
        this.following = following;
        this.postCount = postCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }
}
