package com.course.instagram.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.course.instagram.R;
import com.course.instagram.activities.ProfileActivity;
import com.course.instagram.adapter.GridAdapter;
import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.course.instagram.helper.UserFirebase;
import com.course.instagram.model.PostModel;
import com.course.instagram.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private UserModel user;
    private Button buttonEditProfile;
    private CircleImageView circleImageProfile;
    private TextView textPost;
    private TextView textFollowing;
    private TextView textFollowers;
    private GridAdapter gridAdapter;
    private GridView gridProfile;
    private List<PostModel> postsList;

    private DatabaseReference database;
    private DatabaseReference loggedUserRef;
    private DatabaseReference loggedUserPostsRef;;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeComponents(view);
        setListeners();

        loadProfileImage();
        loadUserData();
        loadUserPosts();

        return view;
    }

    private void loadImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getContext())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);
    }

    private void initializeComponents(View view) {
        buttonEditProfile = view.findViewById(R.id.buttonActionProfile);
        circleImageProfile = view.findViewById(R.id.circleImageProfile);
        user = UserFirebase.getLoggedUserData();
        textPost = view.findViewById(R.id.textProfilePosts);
        textFollowers = view.findViewById(R.id.textProfileFollowers);
        textFollowing = view.findViewById(R.id.textProfileFollowing);
        gridProfile = view.findViewById(R.id.gridViewProfilePage);

        database = FirebaseConfig.getFirebaseDb();
        String id = UserFirebase.getCurrentUserId();
        loggedUserRef = database.child(Constants.USERS).child(id);
        loggedUserPostsRef = database.child(Constants.POSTS).child(id);
    }

    private void loadUserData() {

       loggedUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);

                assert user != null;
                String followers = String.valueOf(user.getFollowers());
                String following = String.valueOf(user.getFollowing());
                String posts = String.valueOf(user.getPosts());

                textPost.setText(posts);
                textFollowing.setText(following);
                textFollowers.setText(followers);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //initialize Image Loader
        loadImageLoader();

    }

    private void loadUserPosts() {

        final List<String> posts = new ArrayList<>();
        postsList = new ArrayList<>();

        postsList.clear();
        posts.clear();

        loggedUserPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int gridSize = getResources().getDisplayMetrics().widthPixels;
                int imageSize = gridSize/3;
                gridProfile.setColumnWidth(imageSize);

                for(DataSnapshot datasnapshot: snapshot.getChildren()) {
                    PostModel postModel = datasnapshot.getValue(PostModel.class);
                    postsList.add(postModel);
                    posts.add(postModel.getPhotoPath());
                }

                //configure adapter
                gridAdapter = new GridAdapter(getActivity(), R.layout.grid_posts, posts);
                gridProfile.setAdapter(gridAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadProfileImage() {
        String photoUrl = user.getPhoto();

        if (!photoUrl.equals("")) {
            Uri url = Uri.parse(photoUrl);

            Glide.with(this).load(url).into(circleImageProfile);
        } else {
            circleImageProfile.setImageResource(R.drawable.profile);
        }
    }

    private void setListeners() {
        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }
}