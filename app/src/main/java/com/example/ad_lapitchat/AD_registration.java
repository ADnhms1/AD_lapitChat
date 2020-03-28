package com.example.ad_lapitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AD_registration extends AppCompatActivity {

    Button registerbtn;
    TextInputEditText name;
    TextInputEditText email;
    TextInputEditText password;
    FirebaseAuth mauth;
    Toolbar registerPageToolBar;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_registration);

        // Firebase Authentication

        mauth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        // toolbar

        registerPageToolBar = findViewById(R.id.RegiterPageAppBar);
        setSupportActionBar(registerPageToolBar);
        getSupportActionBar().setTitle("Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerbtn = findViewById(R.id.RegisterButton);
        name = findViewById(R.id.RegistrationName);
        email = findViewById(R.id.RegistrationEmail);
        password = findViewById(R.id.RegistrationPassword);

        // register user

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupUser();
            }
        });
    }

    // progress method

    void showProgress()
    {
        progressDialog.setMessage("Registering User!");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    // signup method

    void SignupUser()
    {
        final String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        if(!TextUtils.isEmpty(userEmail) || !TextUtils.isEmpty(userPassword))
        {
            showProgress();
            mauth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        FirebaseUser current_user = mauth.getCurrentUser();
                        String uid = current_user.getUid();
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                        HashMap <String,String> userdata = new HashMap<>();
                        userdata.put("name",userName);
                        userdata.put("status","Hey There I'm Using ADLapit chat App");
                        userdata.put("image","default");
                        userdata.put("thumb_image","default");

                        databaseReference.setValue(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(AD_registration.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    Log.d("ADcheck", "onComplete: not complete!");
                                }
                            }
                        });
                    }
                    else
                    {
                        progressDialog.hide();
                        Toast.makeText(AD_registration.this,"Invalid Username or Password",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(AD_registration.this,"Invalid Username or Password!",Toast.LENGTH_SHORT).show();
        }
    }
}
