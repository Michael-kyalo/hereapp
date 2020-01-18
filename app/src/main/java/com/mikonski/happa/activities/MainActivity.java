package com.mikonski.happa.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikonski.happa.R;
import com.mikonski.happa.fragments.accountFragment;
import com.mikonski.happa.fragments.favoritesFragment;
import com.mikonski.happa.fragments.feedFragment;
import com.mikonski.happa.fragments.mapFragment;
import com.mikonski.happa.fragments.notificationFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    FirebaseUser user;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        BottomAppBar bar =  findViewById(R.id.bar);
        setSupportActionBar(bar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

        /**
         * to do check login and location
         * load feedFragment
         */
        loadFragment(new feedFragment());
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
         */
        user = firebaseAuth.getCurrentUser();
        if(user == null){
            Intent login = new Intent(MainActivity.this,LoginAcivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
        }

    }
}
