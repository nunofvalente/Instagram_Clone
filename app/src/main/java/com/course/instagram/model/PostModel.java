package com.course.instagram.model;

import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class PostModel implements Serializable {

    private String id;
    private String description;
    private String photoPath;
    private String userId;

    public PostModel() {
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDb();
        DatabaseReference postRef = firebaseRef.child(Constants.POSTS);
        String postId = postRef.push().getKey();
        setId(postId);

    }

    public boolean save() {
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDb();
        DatabaseReference postRef = firebaseRef.child(Constants.POSTS)
                .child(userId)
                .child(id);
        postRef.setValue(this);
        return true;
    }



    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
