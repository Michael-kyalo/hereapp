package com.mikonski.happa.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.mikonski.happa.Models.User;
import com.mikonski.happa.R;
import com.mikonski.happa.activities.LoginAcivity;
import com.mikonski.happa.activities.ProfileSetupActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class accountFragment extends Fragment {
    DocumentReference  reference;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    FirebaseAuth auth;
    FirebaseUser user;

    TextView username_tv, about_tv;
    CircleImageView profile_civ;

    public accountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if(user == null){
            Intent login = new Intent(getActivity(), LoginAcivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
        }
        String uid = user.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        reference = firebaseFirestore.collection("User").document(uid);
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                User user1 = documentSnapshot.toObject(User.class);

                setprofile(user1);



            }
        });
    // Inflate the layout for this fragment
        //hide add button to prevent confusion
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        username_tv = view.findViewById(R.id.username);
        about_tv = view.findViewById(R.id.about);
        profile_civ = view.findViewById(R.id.profile_image);

        View fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);





        TextView edit = view.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileSetupActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    /**
     * set the username and about
     * get uid to perfom querries
     * @param user1
     */
    private void setprofile(User user1) {
        String username = user1.getUsername();
        String about = user1.getAbout();
        String image = user1.getImage();
        String uid = user1.getUid();

        Uri uri = Uri.parse(image);

        username_tv.setText(username);
        about_tv.setText(about);
        Picasso.get().load(uri).resize(65,65).centerCrop().into(profile_civ);

        Log.d(TAG, "setprofile: " + image);



    }

}
