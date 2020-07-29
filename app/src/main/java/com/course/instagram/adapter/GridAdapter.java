package com.course.instagram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.course.instagram.R;

import java.util.List;

public class GridAdapter extends ArrayAdapter<String> {

    Context context;
    private int layoutResource;
    private List<String> urlPhotos;

    public GridAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }

    public class ViewHolder {
        ImageView image;
        ProgressBar progressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
        if(convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(layoutResource, parent, false);
                viewHolder.progressBar = convertView.findViewById(R.id.progressGridProfile);
                viewHolder.image = convertView.findViewById(R.id.imageGridProfile);
                convertView.setTag(viewHolder);
            } else {
            viewHolder = (ViewHolder) convertView.getTag();
            }

        return convertView;
    }
}
