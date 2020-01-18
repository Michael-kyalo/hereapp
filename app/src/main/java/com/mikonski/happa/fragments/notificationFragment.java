package com.mikonski.happa.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikonski.happa.R;

/**
 * will have recyclerview displaying notifications (someone favorited your post)
 *
 */

public class notificationFragment extends Fragment {

    public notificationFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //hide the Floating button
        View fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        return inflater.inflate(R.layout.fragment_notification, container, false);



    }

}
