package com.mikonski.happa.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.common.api.internal.GoogleApiManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikonski.happa.Models.Event;
import com.mikonski.happa.R;
import com.mikonski.happa.fragments.accountFragment;
import com.mikonski.happa.fragments.favoritesFragment;
import com.mikonski.happa.fragments.feedFragment;
import com.mikonski.happa.fragments.mapFragment;
import com.mikonski.happa.fragments.notificationFragment;
import com.mikonski.happa.utility.LocationFinder;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import org.imperiumlabs.geofirestore.GeoFirestore;

import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    FloatingActionButton fab;
    TextInputEditText title_text, desc_text;
    ImageView add_image;
    CircleImageView upload;
    Button btnDatePicker, btnTimePicker;
    private static final int MY_PERMISSIONS_REQUEST_FINE = 1;
    private static final int MY_PERMISSIONS_REQUEST_COARSE = 2 ;
    LocationFinder finder;
    double longitude, latitude;
    private int mYear, mMonth, mDay, mHour, mMinute;
    String date, time,uid;
    ProgressDialog progressDialog;

    Uri imageUri;

    Dialog popup;

    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore eventsDb;
    CollectionReference collectionReference;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        progressDialog = new ProgressDialog(this);


        eventsDb = FirebaseFirestore.getInstance();

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_COARSE);

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE);
        }

        else {

            finder = new LocationFinder(this);
            if (finder.canGetLocation()) {
                latitude = finder.getLatitude();
                longitude = finder.getLongitude();
                if(latitude== 0.0 && longitude==0.0 ){
                    Toast.makeText(this,"Please Turnon location and internet",Toast.LENGTH_LONG).show();
                }
            } else {
                finder.showSettingsAlert();
            }
        }

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if(user == null){
            Intent login = new Intent(MainActivity.this,LoginAcivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
        }
        uid= user.getUid();


        BottomAppBar bar =  findViewById(R.id.bar);
        setSupportActionBar(bar);

        initpopup();


        fab  = findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {



                popup.show();

            }
        });

        loadFragment(new feedFragment());
    }

    /**
     * show popup for upload activity
     * i didnt implement View.Onclick bit me :D
     */
    public void initpopup() {

        popup = new Dialog(this);
        popup.setContentView(R.layout.popup_add_post);
        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popup.getWindow().getAttributes().gravity = Gravity.TOP;

        upload = popup.findViewById(R.id.upload);
        title_text = popup.findViewById(R.id.title_et);
        desc_text = popup.findViewById(R.id.desc_et);
        add_image = popup.findViewById(R.id.add_image);


        btnDatePicker=popup.findViewById(R.id.btn_date);
        btnTimePicker=popup.findViewById(R.id.btn_time);


        /*
        get date
         */
        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                    time = hourOfDay + ":"+ minute;
                                    Toast.makeText(MainActivity.this,"time set :" +time,Toast.LENGTH_LONG).show();


                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        /*
        get time
         */
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {


                                date = dayOfMonth+"-"+monthOfYear+"-"+year;
                                Toast.makeText(MainActivity.this,"date set :" +date,Toast.LENGTH_LONG).show();




                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        /*
        open gallery to get image
         */
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opengallery();
            }
        });
        /*
        add event based on location to firestore
         */
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addevent();
            }
        });

    }

    private void addevent() {
        final String title = Objects.requireNonNull(title_text.getText()).toString();
        final String desc = Objects.requireNonNull(desc_text.getText()).toString();
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading event");


        if(TextUtils.isEmpty(title)||TextUtils.isEmpty(desc)||TextUtils.isEmpty(date)||TextUtils.isEmpty(time)||imageUri==null){
            Toast.makeText(MainActivity.this,"Fill all event requirements",Toast.LENGTH_LONG).show();
        }
        else {
            progressDialog.show();
            final StorageReference reference = storageReference.child("events").child(uid + ".jpg");
            reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String download = uri.toString();

                                Event event = new Event(uid,title,download,time,date,desc);

                                final String ref = eventsDb.collection("events").document().getId();

                                eventsDb.collection("events").document(ref).set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            GeoFirestore geoFirestore = new GeoFirestore(eventsDb.collection("events_locations"));
                                            geoFirestore.setLocation(ref, new GeoPoint(latitude, longitude), new GeoFirestore.CompletionCallback() {
                                                @Override
                                                public void onComplete(Exception e) {
                                                    if(e!=null){
                                                    Toast.makeText(MainActivity.this,"" + e.getMessage(),Toast.LENGTH_LONG).show();
                                                        popup.dismiss();
                                                    }

                                                    else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(MainActivity.this,"event successfully added",Toast.LENGTH_LONG).show();
                                                        popup.dismiss();

                                                    }
                                                }
                                            });
                                        }
                                        else {
                                            progressDialog.dismiss();
                                            Exception error = task.getException();
                                            assert error != null;
                                            Toast.makeText(MainActivity.this,"error :" +error.getMessage(),Toast.LENGTH_LONG).show();
                                            popup.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                    }else {
                        progressDialog.dismiss();
                        Exception error = task.getException();
                        assert error != null;
                        Toast.makeText(MainActivity.this,"error :" +error.getMessage(),Toast.LENGTH_LONG).show();
                        popup.dismiss();

                    }

                }
            });
        }

    }

    private void opengallery() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d(TAG, "onCreateOptionsMenu: inflating menu");
        getMenuInflater().inflate(R.menu.bottom_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        // too lazy to use select statement


        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.app_bar_settings) {
            loadFragment(new accountFragment());
        }
        if (id == R.id.app_bar_fav) {
            loadFragment(new favoritesFragment());
        }
        if (id == R.id.app_bar_map) {
            loadFragment(new mapFragment());
        }
        if (id == R.id.app_bar_notification) {
            loadFragment(new notificationFragment());
        }
        if (id == R.id.app_bar_home) {
            loadFragment(new feedFragment());
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * load fragments into the frame layout
     * @param fragment
     */
    private void loadFragment(Fragment fragment) {
        Log.d(TAG, "loadFragment: loading fragments");

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //add animation
        fragmentTransaction.setCustomAnimations(R.animator.fragment_fade_in,R.animator.fragment_fade_out);

        //replace and commit
        fragmentTransaction.replace(R.id.framelayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        /**
         * check if user is logged in
         * if user is null send to login Activity
         * handling login this way to use one time login like whatsapp
         * with options of log out available in profile page
         */

        user = firebaseAuth.getCurrentUser();
        if(user == null){
            Intent login = new Intent(MainActivity.this,LoginAcivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImage.ActivityResult result = CropImage.getActivityResult(data);

        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode== RESULT_OK){
                assert result != null;
                imageUri = result.getUri();
                if(imageUri != null){
                    Picasso.get().load(imageUri).into(add_image);
                }

            }

        }



    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_COARSE);
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_COARSE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_FINE);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        finder = new LocationFinder(this);
        if (finder.canGetLocation()) {
            latitude = finder.getLatitude();
            longitude = finder.getLongitude();
            if(latitude== 0.0 && longitude==0.0 ){
                Toast.makeText(this,"Please Turnon location and internet",Toast.LENGTH_LONG).show();
            }
        } else {
            finder.showSettingsAlert();
        }
    }
}
