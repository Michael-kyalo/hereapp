package com.mikonski.happa.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.mikonski.happa.R;
import com.mikonski.happa.utility.LocationFinder;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.listeners.GeoQueryDataEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.mikonski.happa.R.*;


public class mapFragment extends Fragment implements OnMapReadyCallback {


    private static final String TAG = "mapFragment";
    private MapView Map;
    private GoogleMap moogleMap;



    public mapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: mapview created");
        // Inflate the layout for this fragment
        View view = inflater.inflate(layout.fragment_map, container, false);



        View fab = Objects.requireNonNull(getActivity()).findViewById(id.fab);
        fab.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Map = view.findViewById(id.mapView);
        if(Map != null){
            Map.onCreate(null);
            Map.onResume();
            Map.getMapAsync(this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map ready");
        MapsInitializer.initialize(Objects.requireNonNull(getContext()));
        moogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        LocationFinder finder = new LocationFinder(getContext());
        double lat = finder.getLatitude();
        double lon = finder.getLongitude();
        /*
        array of markers to be updated by geo qu
         */
        final ArrayList<Marker> markers = new ArrayList<>();

        // drawable to bitmap


        LatLng focus = new LatLng(lat,lon);
        CameraPosition current = CameraPosition.builder().target(focus).zoom(20).tilt(45).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(current));
//        googleMap.addMarker(new MarkerOptions().position(focus));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference location = db.collection("events_locations");

        GeoFirestore geoFirestore = new GeoFirestore(location);


        GeoPoint geoPoint = new GeoPoint(lat, lon);
        GeoQuery geoQuery = geoFirestore.queryAtLocation(geoPoint,10);
        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDocumentEntered(@NonNull DocumentSnapshot documentSnapshot, @NonNull GeoPoint geoPoint) {
                Log.d(TAG, "onDocumentEntered: "+documentSnapshot.getId());
                Log.d(TAG, "onDocumentEntered: "+ geoPoint.getLatitude());
                LatLng position = new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude());

               MarkerOptions markerOptions = new MarkerOptions().position(position).title("event").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
               markers.add(googleMap.addMarker(markerOptions));

            }

            @Override
            public void onDocumentExited(@NonNull DocumentSnapshot documentSnapshot) {

            }

            @Override
            public void onDocumentMoved(@NonNull DocumentSnapshot documentSnapshot, @NonNull GeoPoint geoPoint) {

            }

            @Override
            public void onDocumentChanged(@NonNull DocumentSnapshot documentSnapshot, @NonNull GeoPoint geoPoint) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(@NonNull Exception e) {

            }
        });
    }
}
