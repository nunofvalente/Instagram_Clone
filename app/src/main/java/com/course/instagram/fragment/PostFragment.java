package com.course.instagram.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.course.instagram.R;
import com.course.instagram.activities.FilterActivity;
import com.course.instagram.activities.ProfileActivity;
import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.course.instagram.helper.Permission;
import com.course.instagram.helper.UserFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class PostFragment extends Fragment {

    private Button buttonOpenGallery, buttonOpenCamera;

    private String[] permissionsNeeded = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);


        buttonOpenCamera = view.findViewById(R.id.buttonOpenCamera);
        buttonOpenGallery = view.findViewById(R.id.buttonOpenGallery);

        //validate permissions
        Permission.validatePermission(permissionsNeeded, getActivity(), 2);

        setListeners();
        return view;
    }

    private void setListeners() {
        buttonOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        buttonOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, Constants.CAMERA);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, Constants.GALLERY);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bitmap image = null;

            try {

                switch (requestCode) {
                    case Constants.GALLERY:
                        Uri imagePath = null;
                        if (data != null) {
                            imagePath = data.getData();
                        }
                        image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imagePath);
                        break;
                    case Constants.CAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                }

                 if (image != null) {

                    //Recover image data for firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                    byte[] imageData = baos.toByteArray();

                    //send image to new activity
                    Intent i = new Intent(getActivity(), FilterActivity.class);
                    i.putExtra("selectedPhoto", imageData);
                    startActivity(i);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}