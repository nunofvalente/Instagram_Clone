package com.course.instagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.course.instagram.R;
import com.course.instagram.adapter.GridAdapter;
import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.course.instagram.helper.UserFirebase;
import com.course.instagram.model.PostModel;
import com.course.instagram.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfileActivity extends AppCompatActivity {

    private UserModel userSelected;
    private Button buttonFollow;
    private CircleImageView circleImageFriendProfile;
    private TextView textFriendFollowers, textFriendPosts, textFriendFollowing;
    private GridView gridViewPhotos;
    private ListAdapter adapterGrid;
    private List<PostModel> postsList;

    private UserModel userLogged;
    private String idUserLogged;
    private DatabaseReference userRef;
    private DatabaseReference followersRef;
    private DatabaseReference userFriendRef;
    private DatabaseReference userLoggedRef;
    private DatabaseReference postUserRef;
    private ValueEventListener valueEventListenerFriendProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        Toolbar toolbar = findViewById(R.id.toolbarCustom);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_clear);

        initializeComponents();
        recoverUser();
        setListeners();

        userFriendRef = userRef.child(userSelected.getId());
    }

    private void setListeners() {
        gridViewPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PostModel post = postsList.get(position);

                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra("postSelected", post);
                startActivity(intent);
            }
        });
    }

    private void initializeComponents() {
        buttonFollow = findViewById(R.id.buttonActionProfile);
        buttonFollow.setText(R.string.loading);
        circleImageFriendProfile = findViewById(R.id.circleImageProfile);
        textFriendFollowers = findViewById(R.id.textProfileFollowers);
        textFriendFollowing = findViewById(R.id.textProfileFollowing);
        textFriendPosts = findViewById(R.id.textProfilePosts);
        gridViewPhotos = findViewById(R.id.gridViewProfilePage);

        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDb();
        userRef = firebaseRef.child(Constants.USERS);
        followersRef = firebaseRef.child(Constants.FOLLOWERS);
        idUserLogged = UserFirebase.getCurrentUserId();
        userLoggedRef = userRef.child(idUserLogged);
    }

    private void initializeImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);
    }

    private void loadPostPhotos() {

        final List<String> urlPhotos = new ArrayList<>();
        postsList = new ArrayList<>();

        postsList.clear();
        urlPhotos.clear();

        postUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //grid size
                int gridSize = getResources().getDisplayMetrics().widthPixels;
                int imageSize = gridSize / 3;
                gridViewPhotos.setColumnWidth(imageSize);

                for (DataSnapshot ds : snapshot.getChildren()) {
                    PostModel post = ds.getValue(PostModel.class);
                    postsList.add(post);
                    urlPhotos.add(post.getPhotoPath());
                }

                //configure adapter
                adapterGrid = new GridAdapter(getApplicationContext(), R.layout.grid_posts, urlPhotos);
                gridViewPhotos.setAdapter(adapterGrid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void recoverLoggedUserData() {
        userLoggedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //recover logged user data
                userLogged = snapshot.getValue(UserModel.class);

                /*Verify if the user is already following the friend after retrieving
                logged user data*/
                verifyIfFollowing();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void recoverUser() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userSelected = (UserModel) bundle.getSerializable("userSelected");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(userSelected.getName());

                postUserRef = FirebaseConfig.getFirebaseDb()
                        .child(Constants.POSTS)
                        .child(userSelected.getId());
            }
        }
        initializeImageLoader();
        loadPostPhotos();
    }

    private void loadUserInformation() {
        String photo = userSelected.getPhoto();

        if (!photo.equals("")) {
            Uri url = Uri.parse(photo);
            Glide.with(getApplicationContext()).load(url).into(circleImageFriendProfile);
        } else {
            circleImageFriendProfile.setImageResource(R.drawable.profile);
        }

        valueEventListenerFriendProfile = userFriendRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserModel user = snapshot.getValue(UserModel.class);

                assert user != null;
                String followers = String.valueOf(user.getFollowers());
                String following = String.valueOf(user.getFollowing());
                String posts = String.valueOf(user.getPosts());

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
        if (followUser) {
            buttonFollow.setText(R.string.following);
        } else {
            buttonFollow.setText(R.string.follow);

            buttonFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    followUser(userLogged, userSelected);
                }
            });

        }
    }

    private void followUser(UserModel userLogged, UserModel userFriend) {
        HashMap<String, Object> friendData = new HashMap<>();

        friendData.put("name", userSelected.getName());
        friendData.put("photo", userSelected.getPhoto());

        followersRef.child(userLogged.getId())
                .child(userFriend.getId())
                .setValue(friendData);

        //change button
        buttonFollow.setText(R.string.following);
        buttonFollow.setOnClickListener(null);

        //increment following value from logged user
        int following = userLogged.getFollowing() + 1;
        HashMap<String, Object> userUpdatedData = new HashMap<>();
        userUpdatedData.put("following", following);

        userLoggedRef.updateChildren(userUpdatedData);

        //increment followers from friend user
        int followers = userSelected.getFollowers() + 1;
        HashMap<String, Object> userFriendUpdatedData = new HashMap<>();
        userFriendUpdatedData.put("followers", followers);

        userRef.child(userSelected.getId()).updateChildren(userFriendUpdatedData);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //recover data from Friend user
        loadUserInformation();

        //recover data from logged user
        recoverLoggedUserData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userFriendRef.removeEventListener(valueEventListenerFriendProfile);
    }
}