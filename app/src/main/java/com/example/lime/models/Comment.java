package com.example.lime.models;

import java.util.Date;
import java.util.List;

public class Comment {

    private String userID, displayName, profilePicUrl, commentContent;
    private Date timestamp;
    private List<String> likedBy;

    public Comment() {
        //for firestore
    }

    public Comment(String userID, String displayName, String profilePicUrl, String commentContent, Date timestamp, List<String> likedBy) {
        this.userID = userID;
        this.displayName = displayName;
        this.profilePicUrl = profilePicUrl;
        this.commentContent = commentContent;
        this.timestamp = timestamp;
        this.likedBy = likedBy;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }
}
