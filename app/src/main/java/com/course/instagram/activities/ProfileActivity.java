package com.course.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.course.instagram.R;
import com.course.instagram.fragment.FeedFragment;
import com.course.instagram.fragment.ProfileFragment;
import com.course.instagram.helper.UserFirebase;
import com.course.instagram.model.UserModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editProfileUserName;
    private EditText editProfileEmail;
    private Button buttonProfileSaveChanges;
    private CircleImageView circleImageEditProfile;
    private TextView textChangeProfilePhoto;

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

        loadUserInformation();
        setListeners();

    }

    private void loadUserInformation() {
        UserModel currentUser = UserFirebase.getLoggedUserData();

        editProfileUserName.setText(currentUser.getName());
        editProfileEmail.setText(currentUser.getEmail());

        String photo = currentUser.getPhoto();
        if(!photo.equals("")) {
            Uri url = Uri.parse(photo);

            Glide.with(getApplicationContext()).load(url).into(circleImageEditProfile);

        } else {
            circleImageEditProfile.setImageResource(R.drawable.profile);
        }


    }

    private void configureToolbar() {
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_clear);
    }

    private void setListeners() {
        textChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //OpenCamera();
                //OpenGallery();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}