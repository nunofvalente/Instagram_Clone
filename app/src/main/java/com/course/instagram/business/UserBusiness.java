package com.course.instagram.business;

import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.course.instagram.helper.Base64Custom;
import com.course.instagram.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class UserBusiness {

    private FirebaseAuth authentication = FirebaseConfig.getFirebaseAuth();
    private DatabaseReference database = FirebaseConfig.getFirebaseDb();

    public void saveUser(UserModel userModel) {

        String userEmail = Base64Custom.encodeEmail(userModel.getEmail());

        DatabaseReference userRef = database.child(Constants.USERS);
        userRef.child(userEmail).setValue(userModel);

    }

    public String userEncodedEmail(UserModel userModel) {
        String userEmail = Base64Custom.encodeEmail(userModel.getEmail());
        return userEmail;
    }
}
