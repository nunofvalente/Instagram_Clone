package com.course.instagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.course.instagram.R;
import com.course.instagram.adapter.FilterAdapter;
import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.course.instagram.helper.RecyclerItemClickListener;
import com.course.instagram.helper.UserFirebase;
import com.course.instagram.model.PostModel;
import com.course.instagram.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imageSelected;
    private Bitmap image;
    private Bitmap imageFilter;
    private List<ThumbnailItem> filterList;
    private TextInputEditText textDescription;
    private String userId;
    private UserModel userLogged;
    private ProgressBar progressBar;
    private Boolean isLoading;

    private DatabaseReference userRef;
    private DatabaseReference userLoggedRef;
    private StorageReference storage;
    private DatabaseReference database;
    private RecyclerView recyclerFilter;
    private FilterAdapter filterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        //inicial configurations
        filterList = new ArrayList<>();
        userId = UserFirebase.getCurrentUserId();
        storage = FirebaseConfig.getFirebaseStorage();
        database = FirebaseConfig.getFirebaseDb();

        //initialize components
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDb();
        userRef = firebaseRef.child(Constants.USERS);
        userLoggedRef = userRef.child(userId);
        textDescription = findViewById(R.id.textDescription);
        imageSelected = findViewById(R.id.imagePhotoSelected);
        recyclerFilter = findViewById(R.id.recyclerFilter);
        progressBar = findViewById(R.id.progressFilter);

        configureToolbar();
        recoverSelectedPhoto();
        recoverLoggedUserData();
        configureRecyclerView();
        setRecyclerListeners();
    }

    private void loading(Boolean state) {

        if(state) {
            isLoading = true;
            progressBar.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
        }
    }

    private void recoverLoggedUserData() {
        userLoggedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //recover logged user data
                userLogged = snapshot.getValue(UserModel.class);
                loading(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarCustom);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.filters);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear);
    }

    private void configureRecyclerView() {

        filterAdapter = new FilterAdapter(filterList, getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerFilter.setLayoutManager(layoutManager);
        recyclerFilter.setAdapter(filterAdapter);
        recyclerFilter.setHasFixedSize(true);

    }

    private void setRecyclerListeners() {
        recyclerFilter.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerFilter, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ThumbnailItem thumbnailPicked = filterList.get(position);

                imageFilter = image.copy(image.getConfig(), true);
                Filter filter = thumbnailPicked.filter;
                imageSelected.setImageBitmap(filter.processFilter(imageFilter));

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
    }

    private void recoverFilters() {
        ThumbnailsManager.clearThumbs();
        filterList.clear();

        ThumbnailItem item = new ThumbnailItem();
        item.image = image;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb(item);

        List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());
        for (Filter filter : filters) {
            ThumbnailItem itemFilter = new ThumbnailItem();
            itemFilter.image = image;
            itemFilter.filter = filter;
            itemFilter.filterName = filter.getName();

            ThumbnailsManager.addThumb(itemFilter);
        }
        filterList.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));
        filterAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_publish) {
            publishPhoto();
        }
        return super.onOptionsItemSelected(item);
    }

    private void publishPhoto() {

        if (isLoading) {
            Toast.makeText(getApplicationContext(), "Loading, please wait!", Toast.LENGTH_SHORT).show();
        } else {
            final PostModel post = new PostModel();
            post.setUserId(userId);
            post.setDescription(textDescription.getText().toString());

            //recover image data from firebase
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageFilter.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageData = baos.toByteArray();

            //save image in Storage
            final StorageReference imageRef = storage
                    .child("images")
                    .child("posts")
                    .child(post.getId() + ".jpeg");

            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Error saving image!", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            post.setPhotoPath(uri.toString());
                            if (post.save()) {
                                //update post quantity
                                int postQtt = userLogged.getPosts() + 1;
                                userLogged.setPosts(postQtt);
                                userLogged.updatePost();

                                Toast.makeText(getApplicationContext(), "Success saving image!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }
            });
        }
    }

            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.menu_filter_publish, menu);
                return super.onCreateOptionsMenu(menu);
            }

            private void recoverSelectedPhoto() {

                try {
                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null) {
                        byte[] imageData = bundle.getByteArray("selectedPhoto");
                        if (imageData != null) {

                            image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                            imageSelected.setImageBitmap(image);
                            imageFilter = image.copy(image.getConfig(), true);

                            //recoverFilters
                            recoverFilters();

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean onSupportNavigateUp() {
                finish();
                return super.onSupportNavigateUp();
            }
        }