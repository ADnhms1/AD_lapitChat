    package com.example.ad_lapitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

    public class ADaccountSettings extends AppCompatActivity {

    CircleImageView ADprofileImageView;
    Button changeImage,changeStatus;
    int CHANGE_IMAGE;
    FirebaseAuth mauth;
    String Uid;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    TextView UserName,Status;
    ImageView settings_Image;
    String currentUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adaccount_settings);
        UserName = findViewById(R.id.Settings_userName);
        Status = findViewById(R.id.Settings_status);
        settings_Image = findViewById(R.id.settings_Image);
        mauth = FirebaseAuth.getInstance();
        CHANGE_IMAGE = 1;
        changeImage = findViewById(R.id.settings_ChangeImage);
        ADprofileImageView = findViewById(R.id.settings_Image);
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,CHANGE_IMAGE);
            }
        });
        changeStatus = findViewById(R.id.settings_Change_Status);
        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ADaccountSettings.this,changeStatusClass.class);
                startActivity(intent);
                finish();
            }
        });

        // retrieving user data
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mauth.getCurrentUser().getUid());
        // setting to offline mode
        databaseReference.keepSynced(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String status = dataSnapshot.child("status").getValue(String.class);
                final String image = dataSnapshot.child("image").getValue(String.class);
                String thumb_image = dataSnapshot.child("thumb_image").getValue(String.class);
                UserName.setText(name);
                Status.setText(status);
                if(!image.equals("default"))
                {
//                    Picasso.get().load(image).placeholder(R.drawable.ic_account_circle_black_24dp).into(settings_Image);

                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.ic_account_circle_black_24dp).into(settings_Image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).placeholder(R.drawable.ic_account_circle_black_24dp).into(settings_Image);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CHANGE_IMAGE && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            final CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                final Uri resultUri = result.getUri();
                FirebaseUser current = mauth.getCurrentUser();
                Uid = current.getUid();
                storageReference = FirebaseStorage.getInstance().getReference().child("AD_profile_Photo").child(Uid);
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
                storageReference.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            databaseReference.child("image").setValue(resultUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        ADprofileImageView.setImageURI(resultUri);
                                        currentUri = resultUri.toString();
                                        Toast.makeText(ADaccountSettings.this,"Image Uploaded!",Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(ADaccountSettings.this,"Upload Fail!",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(ADaccountSettings.this,"Upload Fail!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }
}
