package com.course.instagram.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.course.instagram.activities.CommentActivity;
import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.course.instagram.helper.SquareImageView;
import com.course.instagram.helper.UserFirebase;
import com.course.instagram.model.FeedModel;
import com.course.instagram.model.PostLikes;
import com.course.instagram.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

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
    public void onBindViewHolder(@NonNull final MyFeedViewHolder holder, int position) {
        final FeedModel feedModel = listFeed.get(position);
        final UserModel userLogged = UserFirebase.getLoggedUserData();

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

        holder.viewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", feedModel.getPostId());
                context.startActivity(intent);
            }
        });

        //recover liked post data
        DatabaseReference likeRef = FirebaseConfig.getFirebaseDb()
                .child(Constants.POSTS_LIKES)
                .child(feedModel.getPostId());

        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int likeQuantity = 0;
                if(snapshot.hasChild("likeQuantity")) {
                    PostLikes postLikes = snapshot.getValue(PostLikes.class);
                    likeQuantity = postLikes.getLikeQuantity();
                }

                //Verify if liked button has been pressed
                if(snapshot.hasChild(userLogged.getId())) {
                    holder.buttonLike.setLiked(true);
                } else {
                    holder.buttonLike.setLiked(false);
                }

                //mount object postLiked

                final PostLikes like = new PostLikes();
                like.setFeed(feedModel);
                like.setUserModel(userLogged);
                like.setLikeQuantity(likeQuantity);

                //add event
                holder.buttonLike.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        like.save();
                        holder.textLikes.setText(like.getLikeQuantity() + " Likes");
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        like.delete();
                        holder.textLikes.setText(like.getLikeQuantity() + " Likes");
                    }
                });

                holder.textLikes.setText(like.getLikeQuantity() + " Likes");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return listFeed.size();
    }
}
