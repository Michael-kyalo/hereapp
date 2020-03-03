package com.mikonski.happa.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikonski.happa.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class favoritesFragment extends Fragment {


    public favoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //hide fab to prevent confusion
        View fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

}
