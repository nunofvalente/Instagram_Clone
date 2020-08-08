package com.course.instagram.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.course.instagram.R;
import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.course.instagram.helper.Permission;
import com.course.instagram.helper.UserFirebase;
import com.course.instagram.model.UserModel;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editProfileUserName;
    private EditText editProfileEmail;
    private Button buttonProfileSaveChanges;
    private CircleImageView circleImageEditProfile;
    private TextView textChangeProfilePhoto;
    private DatabaseReference database;
    private UserModel user;

    private String[] permissionNeeded = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = findViewById(R.id.toolbarCustom);
        toolbar.setTitle("Edit Profile");
        setSupportActionBar(toolbar);

        configureToolbar();


        //initialize variables
        editProfileUserName = findViewById(R.id.editProfileUserName);
        editProfileEmail = findViewById(R.id.editProfileUserEmail);
        buttonProfileSaveChanges = findViewById(R.id.buttonProfileSaveChanges);
        circleImageEditProfile = findViewById(R.id.circleImageEditProfile);
        textChangeProfilePhoto = findViewById(R.id.textChangeProfilePhoto);
        database = FirebaseConfig.getFirebaseDb();
        user = UserFirebase.getLoggedUserData();

        //validate permissions
        Permission.validatePermission(permissionNeeded, this, 1);

        loadUserInformation();
        setListeners();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissionResult: grantResults) {
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                alertPermissionValidation();
            }
        }
    }

    private void alertPermissionValidation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Permissions Denied");
        builder.setMessage("Permissions are required to use the app.");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadUserInformation() {
        FirebaseUser currentUser = UserFirebase.getCurrentUser();

        editProfileUserName.setText(currentUser.getDisplayName().toUpperCase());
        editProfileEmail.setText(currentUser.getEmail());

        Uri photo = currentUser.getPhotoUrl();
        if (photo != null) {
            Glide.with(getApplicationContext()).load(photo).into(circleImageEditProfile);

        } else {
            circleImageEditProfile.setImageResource(R.drawable.profile);
        }


    }

    private void configureToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_clear);
    }

    private void setListeners() {
        textChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });
        buttonProfileSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInformation();
            }
        });
    }

    private void OpenGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (gallery.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(gallery, Constants.GALLERY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bitmap image = null;

            try {

                if (requestCode == Constants.GALLERY) {
                    Uri imagePath = null;
                    if (data != null) {
                        imagePath = data.getData();
                    }
                    image = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                }

                if (image != null) {

                    circleImageEditProfile.setImageBitmap(image);

                    //Recover image data for firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imageData = baos.toByteArray();

                    //save image to firebase
                    StorageReference storage = FirebaseConfig.getFirebaseStorage();
                    final StorageReference imageRef = storage.child(Constants.IMAGES)
                            .child(Constants.PROFILE_PIC)
                            .child(UserFirebase.getCurrentUserId())
                            .child("profile.jpeg");

                    UploadTask uploadTask = imageRef.putBytes(imageData);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Error uploading image!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                   updateUserImage(url);
                                }
                            });
                            Toast.makeText(ProfileActivity.this, "Success uploading image!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUserImage(Uri url) {
        Boolean updated = UserFirebase.updateUserPhoto(url);
        if (updated) {
            user.setPhoto(url.toString());
            user.updateUser();
            Toast.makeText(this, "Photo updated!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserInformation() {
        String name = editProfileUserName.getText().toString();
        Boolean updateDisplayName = UserFirebase.updateUserName(name);

        if (updateDisplayName) {
            user.setName(name);
            user.updateUser();

            Toast.makeText(getApplicationContext(), "Name updated with success!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}