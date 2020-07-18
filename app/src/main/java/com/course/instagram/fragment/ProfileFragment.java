package com.course.instagram.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.course.instagram.R;
import com.course.instagram.activities.ProfileActivity;
import com.course.instagram.helper.UserFirebase;
import com.course.instagram.model.UserModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private UserModel user;
    private Button buttonEditProfile;
    private CircleImageView circleImageProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_profile, container, false);

       //initialize variables
        buttonEditProfile = view.findViewById(R.id.buttonEditProfile);
        circleImageProfile = view.findViewById(R.id.circleImageProfile);
        user = UserFirebase.getLoggedUserData();

       setListeners();
        loadImage();

       return view;
    }

    private void loadImage() {
        String photoUrl = user.getPhoto();

        if(!photoUrl.equals("")) {
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
}