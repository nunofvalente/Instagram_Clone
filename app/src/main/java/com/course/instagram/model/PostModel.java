package com.course.instagram.model;

import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.course.instagram.helper.UserFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

    public boolean save(DataSnapshot followersSnapshot) {
        Map<String, Object> object = new HashMap();
        UserModel loggedUser = UserFirebase.getLoggedUserData();
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDb();

        //Ref for posting
        String idComb = "/" + getUserId() + "/" + getId();
        object.put("/posts" + idComb, this);

        //Ref for posting for feed
        for(DataSnapshot followers: followersSnapshot.getChildren()) {

            String followerId = followers.getKey();

            HashMap<String, Object> followerData = new HashMap<>();
            followerData.put("postPhoto", getPhotoPath());
            followerData.put("description", getDescription());
            followerData.put("id", getId());
            followerData.put("userName", loggedUser.getName());
            followerData.put("userPhoto", loggedUser.getPhoto());

            String updatedIds = "/" + followerId + "/" + getId();
            object.put("/feed" + updatedIds, followerData);
        }

        firebaseRef.updateChildren(object);
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
