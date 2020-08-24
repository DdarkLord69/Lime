package com.example.lime.models;

import java.util.Date;
import java.util.List;

public class ImagePost {

    private String userId;
    private String displayName;
    private String profilePicUrl;
    private String postImageUrl;
    private String caption;
    private Date dateAndTimePosted;
    private List<String> likedBy;

    public ImagePost() {

    }

    public ImagePost(String userId, String displayName, String profilePicUrl, String postImageUrl, String caption, Date dateAndTimePosted, List<String> likedBy) {
        this.userId = userId;
        this.displayName = displayName;
        this.profilePicUrl = profilePicUrl;
        this.postImageUrl = postImageUrl;
        this.caption = caption;
        this.dateAndTimePosted = dateAndTimePosted;
        this.likedBy = likedBy;
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

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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
}
