package com.mikonski.happa.fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikonski.happa.R;


public class mapFragment extends Fragment {



    public mapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

}
