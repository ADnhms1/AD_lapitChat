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

import org.w3c.dom.Text;

public class AD_login extends AppCompatActivity {

    Toolbar toolbar;
    Button loginButton;
    FirebaseAuth mauth;
    TextInputEditText email,password;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_login);
        // pregress Dialog
        progressDialog = new ProgressDialog(this);

        // assigning Toolbar
        toolbar = findViewById(R.id.LoginAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Firebase Authentication
        mauth = FirebaseAuth.getInstance();
        email = findViewById(R.id.LoginEmail);
        password = findViewById(R.id.LoginPassword);
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    // show progress method
    void showProgess()
    {
        progressDialog.setTitle("Login User!");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    // login user method
    void loginUser()
    {
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        if(!TextUtils.isEmpty(userEmail) || !TextUtils.isEmpty(userPassword))
        {
            showProgess();
            mauth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        Intent intent = new Intent(AD_login.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        progressDialog.hide();
                        Toast.makeText(AD_login.this,"Invalid Username or Password!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(AD_login.this,"Invalid Username or Password!",Toast.LENGTH_SHORT).show();
        }

    }
}
