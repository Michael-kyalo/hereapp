package com.mikonski.happa.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.mikonski.happa.Models.Event;
import com.mikonski.happa.Models.User;
import com.mikonski.happa.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class singeEventFragment extends Fragment {
    private static final String TAG = "singeEventFragment";
    private TextView username,date,title,description;
    private Button chat,gallery;
    private CircleImageView circleImageView;
    private ImageView imageView;


    public singeEventFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: created");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: created view");
        View view = inflater.inflate(R.layout.fragment_singe_event, container, false);
        username = view.findViewById(R.id.username);
        date =  view.findViewById(R.id.date);
        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);
        chat = view.findViewById(R.id.chat);
        gallery = view.findViewById(R.id.gallery);
        circleImageView = view.findViewById(R.id.profile);
        imageView = view.findViewById(R.id.image);

        assert this.getArguments() != null;
        final String id = this.getArguments().getString("id");
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("events");
        final Source source = Source.CACHE;
        assert id != null;
        final DocumentReference documentReference = collectionReference.document(id);
        documentReference.get(source).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Event event = documentSnapshot.toObject(Event.class);
                assert event != null;
                date.setText(event.getDate());
                title.setText(event.getTitle());
                description.setText(event.getDescription());
                Picasso.get().load(event.getImage()).into(imageView);
                CollectionReference collectionReference1 = firebaseFirestore.collection("User");
                DocumentReference documentReference1 = collectionReference1.document(event.getUid());
                documentReference1.get(source).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        assert user != null;
                        username.setText(user.getUsername());
                        Picasso.get().load(user.getImage()).into(circleImageView);

                    }
                });
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle  bundle = new Bundle();
                bundle.putString("id", id);
                Fragment fragment = new galleryFragment();
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout,fragment);
                fragmentTransaction.commit();


            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle  bundle = new Bundle();
                bundle.putString("id", id);
                Fragment fragment = new chatFragment();
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout,fragment);
                fragmentTransaction.commit();

            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}
