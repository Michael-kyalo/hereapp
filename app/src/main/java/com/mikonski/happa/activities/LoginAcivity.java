package com.mikonski.happa.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mikonski.happa.R;

public class LoginAcivity extends AppCompatActivity {

    private Button login;

    private static int DEFAULT_SIGNIN = 1;
    private static int RC_SIGN_IN =2;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    LottieAnimationView lottieAnimationView , Failed_anim, loading_anim;

    TextView textView_login;

    private static final String TAG = "LoginAcivity";

    SignInButton signInButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acivity);

        mAuth = FirebaseAuth.getInstance();



        GoogleSignInOptions google = new GoogleSignInOptions. Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.DEFAULT_CLIENT_ID))
                .requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this,google);


        /**
         * Show WalkThrough once on first Run only
         */
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
           Intent onBoard = new Intent(LoginAcivity.this,onBoardingActivity.class);
           onBoard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           startActivity(onBoard);
        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();
        /**
         *
         * GOOGLE SIGN IN
         */
        lottieAnimationView = findViewById(R.id.login_anim);
        lottieAnimationView.setAnimation(R.raw.login);
        Failed_anim = findViewById(R.id.login_failed);
        loading_anim = findViewById(R.id.login_anim_loading);
        textView_login = findViewById(R.id.textlogin);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);



        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });

    }


    private void signin() {
        signInButton.setVisibility(View.INVISIBLE);
        textView_login.setText(getString(R.string.signin));
        lottieAnimationView.setVisibility(View.GONE);
        loading_anim.setVisibility(View.VISIBLE);
        Failed_anim.setVisibility(View.GONE);

        Intent intent  = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseauth(account);

            }
             catch (ApiException e) {
                signInButton.setVisibility(View.VISIBLE);
                lottieAnimationView.setVisibility(View.GONE);
                loading_anim.setVisibility(View.GONE);
                Failed_anim.setVisibility(View.VISIBLE);
                textView_login.setText(getString(R.string.signinfailed));
                 Log.d(TAG, "onActivityResult: Signin failed" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    private void firebaseauth(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseauth: "+ account.getId());

        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    /**
                     * task is successfull send to main activity after checking if profile is set
                     *
                     *
                     */

                    FirebaseUser user = mAuth.getCurrentUser();

                    Intent intent = new Intent(LoginAcivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
                else {
                    signInButton.setVisibility(View.VISIBLE);
                    lottieAnimationView.setVisibility(View.GONE);
                    loading_anim.setVisibility(View.GONE);
                    Failed_anim.setVisibility(View.VISIBLE);
                    textView_login.setText(getString(R.string.signinfailed));

                    Log.d(TAG, "onComplete: " + task.getException());
                }

            }
        });
    }
}
