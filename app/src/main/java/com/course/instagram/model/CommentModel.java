package com.course.instagram.model;

import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.course.instagram.helper.UserFirebase;
import com.google.firebase.database.DatabaseReference;

public class CommentModel {

    private String commentId;
    private String postId;
    private String userId;
    private String photoPath;
    private String userName;
    private String comment;

    public CommentModel() {
    }

    public boolean save() {
        DatabaseReference database = FirebaseConfig.getFirebaseDb();
        userId = UserFirebase.getCurrentUserId();

        DatabaseReference commentRef = database
                .child(Constants.COMMENTS)
                .child(getPostId());

        String commentKey = commentRef.push().getKey();
        setCommentId(commentKey);

        commentRef.child(getCommentId()).setValue(this);
        return true;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
