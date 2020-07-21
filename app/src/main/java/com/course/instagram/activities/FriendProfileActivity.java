package com.course.instagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.course.instagram.R;
import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.course.instagram.helper.UserFirebase;
import com.course.instagram.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfileActivity extends AppCompatActivity {

    private UserModel userSelected;
    private Button buttonFollow;
    private CircleImageView circleImageFriendProfile;
    private TextView textFriendFollowers, textFriendPosts, textFriendFollowing;

    private String idUserLogged;
    private DatabaseReference firebaseRef;
    private DatabaseReference followersRef;
    private DatabaseReference userRef;
    private DatabaseReference userFriendRef;
    private ValueEventListener valueEventListenerFriendProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        Toolbar toolbar = findViewById(R.id.toolbarCustom);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_clear);

        initializeComponents();
        recoverUser();
        verifyIfFollowing();

    }

    private void initializeComponents() {
        buttonFollow = findViewById(R.id.buttonActionProfile);
        buttonFollow.setText("Follow");
        circleImageFriendProfile = findViewById(R.id.circleImageProfile);
        textFriendFollowers = findViewById(R.id.textProfileFollowers);
        textFriendFollowing = findViewById(R.id.textProfileFollowing);
        textFriendPosts = findViewById(R.id.textProfilePosts);
        firebaseRef = FirebaseConfig.getFirebaseDb();
        userRef = firebaseRef.child(Constants.USERS);
        followersRef = firebaseRef.child(Constants.FOLLOWERS);
        idUserLogged = UserFirebase.getCurrentUserId();
    }

    public void recoverUser() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userSelected = (UserModel) bundle.getSerializable("userSelected");
            getSupportActionBar().setTitle(userSelected.getName());
        }
    }

    private void loadUserInformation() {
        String photo = userSelected.getPhoto();

        if (!photo.equals("")) {
            Uri url = Uri.parse(photo);
            Glide.with(getApplicationContext()).load(url).into(circleImageFriendProfile);
        } else {
            circleImageFriendProfile.setImageResource(R.drawable.profile);
        }

        userFriendRef = userRef.child(userSelected.getId());
        valueEventListenerFriendProfile = userFriendRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserModel userModel = snapshot.getValue(UserModel.class);

                assert userModel != null;
                String followers = String.valueOf(userModel.getFollowers());
                String following = String.valueOf(userModel.getFollowing());
                String posts = String.valueOf(userModel.getPosts());

                textFriendFollowers.setText(followers);
                textFriendFollowing.setText(following);
                textFriendPosts.setText(posts);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void verifyIfFollowing() {
        DatabaseReference followerRef = followersRef
                .child(idUserLogged)
                .child(userSelected.getId());
        followerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    enableButtonFollow(true);
                } else {
                    enableButtonFollow(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void enableButtonFollow(Boolean followUser) {
        if(followUser) {
            buttonFollow.setText("Following");
        } else {
            buttonFollow.setText("Follow");

            buttonFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    followUser();
                }
            });

        }
    }

    private void followUser() {
      //  DatabaseReference saveFolllower = followersRef.child(idUserLogged).child(userSelected.getId()).set
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadUserInformation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userFriendRef.removeEventListener(valueEventListenerFriendProfile);
    }
}