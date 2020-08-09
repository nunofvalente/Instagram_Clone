package com.course.instagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.course.instagram.R;
import com.course.instagram.adapter.CommentAdapter;
import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.course.instagram.helper.UserFirebase;
import com.course.instagram.model.CommentModel;
import com.course.instagram.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private EditText editComment;
    private Button buttonSendComment;
    private String postId;
    private UserModel user;
    private RecyclerView recyclerComments;
    private List<CommentModel> commentList = new ArrayList<>();
    private CommentAdapter adapter;

    private DatabaseReference database;
    private DatabaseReference commentsRef;
    private ValueEventListener valueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        configureToolbar();
        initializeComponents();
        setListeners();
        recoverData();
        recoverComments();
        configureRecyclerView();

    }

    private void recoverComments() {

        commentsRef = database.child(Constants.COMMENTS).child(postId);

        commentList.clear();

        valueEventListener = commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()) {
                    CommentModel commentModel = ds.getValue(CommentModel.class);
                    commentList.add(commentModel);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configureRecyclerView() {
        recyclerComments.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerComments.setHasFixedSize(true);

        adapter = new CommentAdapter(getApplicationContext(), commentList);
        recyclerComments.setAdapter(adapter);
    }

    private void recoverData() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            postId = bundle.getString("postId");
        }

        user = UserFirebase.getLoggedUserData();
    }

    private void setListeners() {
        buttonSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveComment();
                commentList.clear();
            }
        });
    }

    private void initializeComponents() {
        editComment = findViewById(R.id.editComment);
        buttonSendComment = findViewById(R.id.buttonSendComment);
        recyclerComments = findViewById(R.id.recyclerComments);
        database = FirebaseConfig.getFirebaseDb();
    }

    private void saveComment() {

        String comment = editComment.getText().toString();
        if(!comment.equals("") && comment != null) {
            CommentModel commentModel = new CommentModel();
            commentModel.setUserId(user.getId());
            commentModel.setUserName(user.getName());
            commentModel.setPhotoPath(user.getPhoto());
            commentModel.setPostId(postId);
            commentModel.setComment(comment);
            if(commentModel.save()) {
                Toast.makeText(getApplicationContext(), "Comment saved!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please write a comment to send!", Toast.LENGTH_SHORT).show();
        }

        editComment.setText("");
    }

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarCustom);
        toolbar.setTitle("Comments");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_clear);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        commentsRef.removeEventListener(valueEventListener);
    }
}