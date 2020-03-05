package com.mikonski.happa.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Source;
import com.mikonski.happa.Models.Event;
import com.mikonski.happa.Models.EventCLick;
import com.mikonski.happa.R;
import com.mikonski.happa.utility.LocationFinder;
import com.mikonski.happa.utility.RecyclerAdapter;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.listeners.GeoQueryDataEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.firebase.inappmessaging.internal.Logging.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class feedFragment extends Fragment {
    private CollectionReference collectionReference, geofire;
    private RecyclerAdapter recyclerAdapter;
    private ProgressBar progressBar;
    private List<EventCLick> eventList = new ArrayList<>();
    private TextView loading;
    private Boolean empty = true;







    public feedFragment() {
        // Required empty public constructor
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =inflater.inflate(R.layout.fragment_feed, container, false);
        View fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        loading = view.findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("events");
        geofire= firebaseFirestore.collection("events_locations");

        FragmentManager fragmentManager = getFragmentManager();



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);

        recyclerAdapter = new RecyclerAdapter(eventList, getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        setUpRecycleView(recyclerView);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setUpRecycleView(RecyclerView recyclerView) {
        GeoFirestore geoFirestore = new GeoFirestore(geofire);

        LocationFinder finder = new LocationFinder(getContext());
        if (finder.canGetLocation()) {
            double latitude = finder.getLatitude();
            double longitude = finder.getLongitude();
            if(latitude == 0.0 && longitude ==0.0 ){
                Toast.makeText(getContext(),"Turn On location and Internet",Toast.LENGTH_LONG).show();
            }
            GeoPoint point = new GeoPoint(latitude, longitude);



            GeoQuery geoQuery= geoFirestore.queryAtLocation(point,10);
            geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                @Override
                public void onDocumentEntered(@NonNull DocumentSnapshot documentSnapshot, @NonNull GeoPoint geoPoint) {
                    empty = false;
                   //allows offline mode
                    Source source = Source.CACHE;
                    final String id = documentSnapshot.getId();
                    Log.d(TAG, "onDocumentEntered: "+ id);

                    DocumentReference documentReference = collectionReference.document(id);
                    documentReference.get(source).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Event event = documentSnapshot.toObject(Event.class);
                            EventCLick eventCLick = new EventCLick();
                            assert event != null;
                            eventCLick.setDate(event.getDate());
                            eventCLick.setDescription(event.getDescription());
                            eventCLick.setTime(event.getTime());
                            eventCLick.setUid(event.getUid());
                            eventCLick.setId(id);
                            eventCLick.setImage(event.getImage());
                            eventCLick.setTitle(event.getTitle());


                            eventList.add(eventCLick);
                            progressBar.setVisibility(View.GONE);
                            loading.setVisibility(View.GONE);

                        }
                    });

                    recyclerAdapter.notifyDataSetChanged();


                }

                @Override
                public void onDocumentExited(@NonNull DocumentSnapshot documentSnapshot) {
                    String id = documentSnapshot.getId();
                    Log.d(TAG, "onDocumentEntered: "+ id);

                    DocumentReference documentReference = collectionReference.document(id);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Event event = documentSnapshot.toObject(Event.class);
                            eventList.remove(event);

                        }
                    });

                    recyclerAdapter.notifyDataSetChanged();


                }

                @Override
                public void onDocumentMoved(@NonNull DocumentSnapshot documentSnapshot, @NonNull GeoPoint geoPoint) {



                }

                @Override
                public void onDocumentChanged(@NonNull DocumentSnapshot documentSnapshot, @NonNull GeoPoint geoPoint) {

                }

                @Override
                public void onGeoQueryReady() {
                    if(empty){
                        progressBar.setVisibility(View.GONE);
                        loading.setVisibility(View.GONE);

                       AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                       builder.setMessage("No events around start posting")
                               .setTitle("oops")
                               .setIcon(R.drawable.sad)
                               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {



                                       dialog.dismiss();

                                   }
                               });
                       AlertDialog alertDialog = builder.create();
                       alertDialog.show();


                    }

                }

                @Override
                public void onGeoQueryError(@NonNull Exception e) {

                }
            });
        } else {
            Toast.makeText(getContext(),"Turn On location and Internet",Toast.LENGTH_LONG).show();
        }





    }

}
