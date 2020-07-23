package com.course.instagram.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.course.instagram.R;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        filterList = new ArrayList<>();

        imageSelected = findViewById(R.id.imagePhotoSelected);

        Toolbar toolbar = findViewById(R.id.toolbarCustom);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.filters);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear);


        recoverSelectedPhoto();
    }

    private void recoverFilters() {
        filterList.clear();

        ThumbnailItem item = new ThumbnailItem();
        item.image = image;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb(item);

        List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());
        for(Filter filter: filters) {
            ThumbnailItem itemFilter = new ThumbnailItem();
            itemFilter.image = image;
            itemFilter.filter = filter;
            itemFilter.filterName = filter.getName();

            ThumbnailsManager.addThumb(itemFilter);
        }
        filterList.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_publish) {
            publishPhoto();
        }
        return super.onOptionsItemSelected(item);
    }

    private void publishPhoto() {
        Toast.makeText(getApplicationContext(), "TODO", Toast.LENGTH_SHORT).show();
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

                    //recoverFilters
                    recoverFilters();

                   /* imageFitler = image.copy(image.getConfig(), true);
                    Filter filter = FilterPack.getAmazonFilter(getApplicationContext());
                    imageSelected.setImageBitmap(filter.processFilter(imageFitler));*/
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