package com.course.instagram.model;

import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class PostLikes {

    private int likeQuantity = 0;
    private UserModel userModel;

    public UserModel getUserModel() {
        return userModel;
    }

    public void save() {
        DatabaseReference databaseRef = FirebaseConfig.getFirebaseDb();

        HashMap<String, Object> userData = new HashMap<>();
        userData.put("name", userModel.getName());
        userData.put("photoPath", userModel.getPhoto());

        DatabaseReference postLikedRef = databaseRef
                .child(Constants.POSTS_LIKES)
                .child(feed.getPostId())
                .child(userModel.getId());
        postLikedRef.setValue(userData);

        updateQuantity(1);
    }

    private void updateQuantity(int quantity) {
        DatabaseReference databaseRef = FirebaseConfig.getFirebaseDb();

        DatabaseReference postLikedRef = databaseRef
                .child(Constants.POSTS_LIKES)
                .child(feed.getPostId())
                .child("likeQuantity");
        setLikeQuantity(getLikeQuantity() + quantity);

        postLikedRef.setValue(getLikeQuantity());

    }

    public void delete() {
        DatabaseReference databaseRef = FirebaseConfig.getFirebaseDb();

        DatabaseReference postLikedRef = databaseRef
                .child(Constants.POSTS_LIKES)
                .child(feed.getPostId())
                .child(userModel.getId());;
        postLikedRef.removeValue();

        updateQuantity(-1);
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    private FeedModel feed;

    public FeedModel getFeed() {
        return feed;
    }

    public void setFeed(FeedModel feed) {
        this.feed = feed;
    }

    public PostLikes() {
    }

    public int getLikeQuantity() {
        return likeQuantity;
    }

    public void setLikeQuantity(int likeQuantity) {
        this.likeQuantity = likeQuantity;
    }
}
