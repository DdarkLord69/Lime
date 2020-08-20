package com.example.lime.models;

import java.util.Date;
import java.util.List;

public class Post {

    private String userId;
    private String displayName;
    private String profilePicUrl;
    private String postContent;
    private Date dateAndTimePosted;
    private List<String> likedBy;
    private int comments;

    public Post() {
        //empty constructor for firestore
    }

    public Post(String userId, String displayName, String profilePicUrl, String postContent, Date dateAndTimePosted, List<String> likedBy, int comments) {
        this.userId = userId;
        this.displayName = displayName;
        this.profilePicUrl = profilePicUrl;
        this.postContent = postContent;
        this.dateAndTimePosted = dateAndTimePosted;
        this.likedBy = likedBy;
        this.comments = comments;
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

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public Date getDateAndTimePosted() {
        return dateAndTimePosted;
    }

    public void setDateAndTimePosted(Date dateAndTimePosted) {
        this.dateAndTimePosted = dateAndTimePosted;
    }

    public List<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}
