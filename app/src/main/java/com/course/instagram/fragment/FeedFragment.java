package com.course.instagram.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.course.instagram.R;
import com.course.instagram.adapter.FeedAdapter;
import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.course.instagram.helper.UserFirebase;
import com.course.instagram.model.FeedModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedFragment extends Fragment {

    private RecyclerView recyclerFeed;
    private FeedAdapter feedAdapter;
    private List<FeedModel> feedList = new ArrayList<>();
    private ValueEventListener valueEventListener;
    private DatabaseReference feedRef;
    private String idLoggedUser;


    public FeedFragment() {
        // Required empty public constructor
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        initializeComponents(view);

        return view;
    }

    private void configureRecyclerView() {
        recyclerFeed.setHasFixedSize(true);
        recyclerFeed.setLayoutManager(new LinearLayoutManager(getActivity()));

        feedAdapter = new FeedAdapter(feedList, getActivity());
        recyclerFeed.setAdapter(feedAdapter);

    }

    private void initializeComponents(View view) {
        recyclerFeed = view.findViewById(R.id.recyclerFeed);

        idLoggedUser = UserFirebase.getCurrentUserId();
        feedRef = FirebaseConfig.getFirebaseDb()
                .child(Constants.FEED)
                .child(idLoggedUser);
    }

    private void listingFeed() {
        valueEventListener = feedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren() ) {
                    feedList.add(ds.getValue(FeedModel.class));
                }
                Collections.reverse(feedList);
                feedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        listingFeed();
        configureRecyclerView();
    }

    @Override
    public void onStop() {
        super.onStop();
        feedRef.removeEventListener(valueEventListener);
    }
}