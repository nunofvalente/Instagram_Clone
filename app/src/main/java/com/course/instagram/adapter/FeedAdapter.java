package com.course.instagram.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.course.instagram.R;
import com.course.instagram.helper.SquareImageView;
import com.course.instagram.model.FeedModel;
import com.like.LikeButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyFeedViewHolder> {

    List<FeedModel> listFeed;
    Context context;

    public FeedAdapter(List<FeedModel> listFeed, Context context) {
        this.listFeed = listFeed;
        this.context = context;
    }

    public class MyFeedViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImage;
        TextView textUsername, textLikes, textDescription;
        ImageView viewComments;
        SquareImageView squareImageFeed;
        LikeButton buttonLike;

        public MyFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.circleImageFeedAdapter);
            textUsername = itemView.findViewById(R.id.textNameFeedAdapter);
            textLikes = itemView.findViewById(R.id.textLikesFeed);
            textDescription = itemView.findViewById(R.id.textDescriptionFeed);
            squareImageFeed = itemView.findViewById(R.id.squareImageFeed);
            viewComments = itemView.findViewById(R.id.imageCommentsFeed);
            buttonLike = itemView.findViewById(R.id.buttonLikeFeed);
        }
    }

    @NonNull
    @Override
    public MyFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_list_adapter, parent, false);

        return new MyFeedViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyFeedViewHolder holder, int position) {
        FeedModel feedModel = listFeed.get(position);

        //Load feed data
        Uri userPhoto = Uri.parse(feedModel.getUserPhoto());
        Uri postImage = Uri.parse(feedModel.getPostPhoto());

        if (userPhoto != null) {
            Glide.with(context).load(userPhoto).into(holder.userImage);
        } else {
            holder.userImage.setImageResource(R.drawable.profile);
        }

        Glide.with(context).load(postImage).into(holder.squareImageFeed);

        holder.textUsername.setText(feedModel.getUserName());
        holder.textDescription.setText(feedModel.getDescription());

    }

    @Override
    public int getItemCount() {
        return listFeed.size();
    }
}
