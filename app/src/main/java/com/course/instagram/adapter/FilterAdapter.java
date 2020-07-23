package com.course.instagram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.course.instagram.R;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.MyFilterViewHolder> {

    List<ThumbnailItem> filterList;
    private Context context;

    public FilterAdapter(List<ThumbnailItem> filterList, Context context) {
        this.filterList = filterList;
        this.context = context;
    }

    public class MyFilterViewHolder extends RecyclerView.ViewHolder {

        ImageView photo;
        TextView filter;

        public MyFilterViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.imageViewFilter);
            filter = itemView.findViewById(R.id.textFilter);

        }
    }

    @NonNull
    @Override
    public MyFilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_adapter, parent, false);
        return new MyFilterViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyFilterViewHolder holder, int position) {
            ThumbnailItem item = filterList.get(position);
            holder.photo.setImageBitmap(item.image);
            holder.filter.setText(item.filterName);
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }
}
