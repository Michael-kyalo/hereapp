package com.mikonski.happa.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.mikonski.happa.Models.Event;
import com.mikonski.happa.R;
import com.mikonski.happa.fragments.singeEventFragment;

import java.util.Objects;

public class singlePostActivity extends AppCompatActivity {
    private static final String TAG = "singlePost";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);
        String id = Objects.requireNonNull(getIntent().getExtras()).getString("id");
        if(id == null){
            finish();
        }
        else {
            Bundle  bundle = new Bundle();
            bundle.putString("id", id);

            singeEventFragment singeEventFragment = new singeEventFragment();
            singeEventFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,singeEventFragment).commit();
        }



    }
}
