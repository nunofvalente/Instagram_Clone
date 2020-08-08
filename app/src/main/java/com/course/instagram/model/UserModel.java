package com.course.instagram.model;

import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.course.instagram.helper.UserFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;


public class UserModel implements Serializable {

    private String id;
    private String name;
    private String email;
    private String password;
    private String photo = "";
    private Integer followers = 0;
    private Integer posts = 0;
    private Integer following = 0;

    public UserModel() {
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public Integer getPosts() {
        return posts;
    }

    public void setPosts(Integer posts) {
        this.posts = posts;
    }

    public Integer getFollowing() {
        return following;
    }

    public void setFollowing(Integer following) {
        this.following = following;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name.toUpperCase();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void updateUser() {

        DatabaseReference database = FirebaseConfig.getFirebaseDb();
        UserModel user = UserFirebase.getLoggedUserData();
        String userId = UserFirebase.userEncodedEmail(user);

        DatabaseReference path = database.child(Constants.USERS).child(userId);

        HashMap<String, Object> userValues = convertToMap();

        path.updateChildren(userValues);

    }

    public void updatePost() {
        DatabaseReference database = FirebaseConfig.getFirebaseDb();
        UserModel user = UserFirebase.getLoggedUserData();
        String userId = UserFirebase.userEncodedEmail(user);

        DatabaseReference path = database.child(Constants.USERS).child(userId);

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("posts", this.posts);

        path.updateChildren(userMap);
    }

    private HashMap<String, Object> convertToMap() {
       HashMap<String, Object> userMap = new HashMap<>();

       userMap.put("name", this.name);
       userMap.put("email", this.email);
       userMap.put("photo", this.photo);
       userMap.put("followers", this.followers);
       userMap.put("following", this.following);
       userMap.put("posts", this.posts);

        return userMap;
    }
}

