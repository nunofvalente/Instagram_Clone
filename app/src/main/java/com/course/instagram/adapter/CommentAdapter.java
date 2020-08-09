package com.course.instagram.adapter;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.course.instagram.R;
import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.course.instagram.model.CommentModel;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyCommentViewHolder> {

    Context context;
    List<CommentModel> commentList;

    public CommentAdapter(Context context, List<CommentModel> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    public class MyCommentViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageComments;
        TextView textUsernameComments, textDescriptionComments;

        public MyCommentViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageComments = itemView.findViewById(R.id.circleImageComment);
            textUsernameComments = itemView.findViewById(R.id.textNameComment);
            textDescriptionComments = itemView.findViewById(R.id.textDescriptionComment);
        }
    }

    @NonNull
    @Override
    public MyCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_adapter, parent, false);

        return new MyCommentViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCommentViewHolder holder, int position) {
        CommentModel commentModel = commentList.get(position);

        String photoPath = commentModel.getPhotoPath();

        if(photoPath != null) {
            Uri url = Uri.parse(photoPath);
            Glide.with(context).load(url).into(holder.circleImageComments);
        } else {
            holder.circleImageComments.setImageResource(R.drawable.profile);
        }

        holder.textDescriptionComments.setText(commentModel.getComment());
        holder.textUsernameComments.setText(commentModel.getUserName());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

}
