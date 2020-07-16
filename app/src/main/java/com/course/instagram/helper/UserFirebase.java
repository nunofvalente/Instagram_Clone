package com.course.instagram.helper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserFirebase {

    public static String userEncodedEmail(UserModel userModel) {
        String userEmail = Base64Custom.encodeEmail(userModel.getEmail());
        return userEmail;
    }

    public static UserModel getLoggedUserData() {
        FirebaseAuth authentication = FirebaseConfig.getFirebaseAuth();
        FirebaseUser firebaseUser = authentication.getCurrentUser();
        UserModel currentUser = new UserModel();

        if (authentication.getCurrentUser() != null) {

            String photo = "";
            String name = firebaseUser.getDisplayName();
            String email = firebaseUser.getEmail();
            if (firebaseUser.getPhotoUrl() != null) {
                photo = firebaseUser.getPhotoUrl().toString();
            }

            currentUser.setName(name);
            currentUser.setEmail(email);
            currentUser.setPhoto(photo);

        }
        return currentUser;
    }

    public static Boolean updateUserName(String name) {
        FirebaseAuth authentication = FirebaseConfig.getFirebaseAuth();

        try {
            FirebaseUser user = authentication.getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(name).build();


            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("Profile", "Error updating the profile name");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}