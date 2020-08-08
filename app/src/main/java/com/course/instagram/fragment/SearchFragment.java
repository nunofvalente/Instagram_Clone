package com.course.instagram.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.course.instagram.R;
import com.course.instagram.activities.FriendProfileActivity;
import com.course.instagram.adapter.SearchAdapter;
import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.course.instagram.helper.RecyclerItemClickListener;
import com.course.instagram.helper.UserFirebase;
import com.course.instagram.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    ;

    private SearchView searchViewUsers;
    private RecyclerView recyclerSearchFragment;

    private List<UserModel> userList;
    private DatabaseReference userRef;
    private RecyclerView.Adapter searchAdapter;
    private String idUserLogged;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //initialize variables
        searchViewUsers = view.findViewById(R.id.searchViewUsers);
        recyclerSearchFragment = view.findViewById(R.id.recyclerSearchFragment);
        userList = new ArrayList<>();
        userRef = FirebaseConfig.getFirebaseDb().child(Constants.USERS);
        idUserLogged = UserFirebase.getCurrentUserId();

        configureSearchView();
        configureRecyclerView();

        setListeners();
        return view;
    }

    private void configureRecyclerView() {

        //define adapter
        searchAdapter = new SearchAdapter(userList, getContext());
        recyclerSearchFragment.setAdapter(searchAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerSearchFragment.setLayoutManager(layoutManager);
        recyclerSearchFragment.setHasFixedSize(true);
    }

    private void configureSearchView() {
        String searchText = "Search User";
        searchViewUsers.setQueryHint(searchText);
        searchViewUsers.clearFocus();
        searchViewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchViewUsers.setIconified(false);
            }
        });

        searchViewUsers.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String writtenText = newText.toUpperCase();
                searchUser(writtenText);
                return true;
            }
        });
    }

    private void searchUser(String text) {
        userList.clear();

        if (text.length() >= 2) {
            Query query = userRef.orderByChild("name")
                    .startAt(text).endAt(text + "\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userList.clear();
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    String id = userModel.getId();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if(idUserLogged.equals(id)) continue;

                        userList.add(ds.getValue(UserModel.class));
                    }
                    searchAdapter.notifyDataSetChanged();
                 /*int total = userList.size();
                 Log.i("totalUsers", "total:" + total);*/
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void setListeners() {
        recyclerSearchFragment.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerSearchFragment, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                UserModel user = userList.get(position);

                Intent intent = new Intent(getActivity(), FriendProfileActivity.class);
                intent.putExtra("userSelected", user);
                startActivity(intent);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
    }
}