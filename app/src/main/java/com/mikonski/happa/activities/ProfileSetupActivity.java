package com.mikonski.happa.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikonski.happa.Models.User;
import com.mikonski.happa.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSetupActivity extends AppCompatActivity {
    CircleImageView circleImageView;
    Uri uri;
    private static final String TAG = "ProfileSetupActivity";
    TextInputEditText username, about;
    ProgressDialog progressDialog;

    String about_val, username_val, uid;
    Button save;
    CardView cardView;
    TextView textView;


    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore userDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        progressDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        userDb = FirebaseFirestore.getInstance();

        user = auth.getCurrentUser();
        if(user == null){
          Intent login = new Intent(ProfileSetupActivity.this, LoginAcivity.class);
          login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          startActivity(login);
        }

        uid = user.getUid();



        circleImageView = findViewById(R.id.profile_image);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCrop();
            }
        });

        cardView = findViewById(R.id.card);
        textView = findViewById(R.id.text);

        username = findViewById(R.id.username);
        about = findViewById(R.id.about);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCrop();
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCrop();
            }
        });

       save = findViewById(R.id.save_button);
       save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                upload();
            }
        });
    }

    /**
     * upload image to firebase storage
     * save user info to firestore
     */

    private void upload() {
         about_val = Objects.requireNonNull(about.getText()).toString();
         username_val = Objects.requireNonNull(username.getText()).toString();
         progressDialog.setMessage("Saving Data");
         progressDialog.setCancelable(false);
         progressDialog.show();


         if(TextUtils.isEmpty(about_val)||TextUtils.isEmpty(username_val)||uri==null){
             Toast.makeText(this,"Please Fill all Details",Toast.LENGTH_LONG).show();
         }
         else {

             final StorageReference ref = storageReference.child("profileimage").child(uid +".jpg");
             ref.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                     if(task.isSuccessful()){
                         ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                             @Override
                             public void onSuccess(Uri uri) {

                                 String download = uri.toString();

                                 User user1 = new User(username_val,about_val,download,uid);

                                 userDb.collection("User").document(uid).set(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if(task.isSuccessful()){

                                             progressDialog.dismiss();
                                             Toast.makeText(ProfileSetupActivity.this,"Succefully uploaded",Toast.LENGTH_LONG).show();
                                             finish();

                                         }
                                         else {
                                             progressDialog.dismiss();
                                             Exception error = task.getException();
                                             Toast.makeText(ProfileSetupActivity.this,"error:" + error,Toast.LENGTH_LONG).show();


                                         }
                                     }
                                 });
                             }
                         });





                     }
                     else {
                         progressDialog.dismiss();

                         Exception error = task.getException();

                         Toast.makeText(ProfileSetupActivity.this,"error:" + error,Toast.LENGTH_LONG).show();

                     }
                 }
             });



         }
    }

    private void toMain() {
        Intent main = new Intent(this,MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
    }

    /**
     * open gallery and crop the image
     */
    private void startCrop() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setCropShape(CropImageView.CropShape.OVAL).start(this);

    }
    /*
    recieve cropped image and load into the circular image view
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                uri = result.getUri();

                if(uri != null){
                    Picasso.get().load(uri).resize(80,80).centerCrop().into(circleImageView);
                }


            }
        }
        else {
            assert result != null;
            Exception error = result.getError();
            Log.e(TAG, "onActivityResult: ", error );

        }
    }
}
