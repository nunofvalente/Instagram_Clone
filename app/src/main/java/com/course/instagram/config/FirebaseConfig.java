package com.course.instagram.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseConfig {

    private static DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private static FirebaseAuth authentication = FirebaseAuth.getInstance();
    private static StorageReference storage = FirebaseStorage.getInstance().getReference();

    public static DatabaseReference getFirebaseDb() { return database; }

    public static FirebaseAuth getFirebaseAuth() {
        return authentication;
    }

    public static StorageReference getFirebaseStorage() {
        return storage;
    }
}
