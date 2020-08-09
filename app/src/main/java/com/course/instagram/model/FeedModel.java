package com.course.instagram.model;

public class FeedModel {

    private String postId;
    private String postPhoto;
    private String description;
    private String userName;
    private String userPhoto;

    public FeedModel() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String id) {
        this.postId = id;
    }

    public String getPostPhoto() {
        return postPhoto;
    }

    public void setPostPhoto(String postImage) {
        this.postPhoto = postImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }
}
