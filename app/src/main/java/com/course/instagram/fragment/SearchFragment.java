package com.course.instagram.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.course.instagram.R;

public class SearchFragment extends Fragment { ;

    private SearchView searchViewUsers;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchViewUsers = view.findViewById(R.id.searchViewUsers);
        String searchText = "Search User";
        searchViewUsers.setQueryHint(searchText);
        searchViewUsers.clearFocus();

        return view;
    }
}