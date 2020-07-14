package com.course.instagram.helper;

import com.course.instagram.model.UserModel;

public class UserFirebase {

    public static String userEncodedEmail(UserModel userModel) {
        String userEmail = Base64Custom.encodeEmail(userModel.getEmail());
        return userEmail;
    }
}
