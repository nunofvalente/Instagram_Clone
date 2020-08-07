package com.course.instagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.course.instagram.R;
import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.course.instagram.model.PostModel;
import com.course.instagram.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    private TextView textUserNamePost;
    private CircleImageView circleImageUserPost;
    private ImageView imagePost;
    private UserModel selectedUser;
    private String selectedUserId;
    private PostModel selectedPost;

    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        initializeComponents();
        configureToolbar();
        getRequiredData();
        loadPhotoData();
    }

    private void initializeComponents() {
        textUserNamePost = findViewById(R.id.textUserNamePost);
        circleImageUserPost = findViewById(R.id.circleViewPost);
        imagePost = findViewById(R.id.imageViewPostActivity);

        database = FirebaseConfig.getFirebaseDb();
    }

    private void configureToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbarCustom);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View Post");
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_clear);
    }

    private void getRequiredData() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            selectedPost = (PostModel) bundle.getSerializable("postSelected");

            assert selectedPost != null;
            String photoPath = selectedPost.getPhotoPath();
            Uri urlPhotoPost = Uri.parse(photoPath);

            Glide.with(this).load(urlPhotoPost).into(imagePost);

            selectedUserId = selectedPost.getUserId();
        }
    }

    private void loadPhotoData() {

        DatabaseReference userRef = database.child(Constants.USERS).child(selectedUserId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);

                assert userModel != null;
                String name = userModel.getName();
                String photo = userModel.getPhoto();

                textUserNamePost.setText(name);

                if(photo != null) {
                    Uri url = Uri.parse(photo);
                    Glide.with(getApplicationContext()).load(url).into(circleImageUserPost);
                } else {
                    circleImageUserPost.setImageResource(R.drawable.profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}