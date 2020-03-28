package com.example.ad_lapitchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AD_startActivity extends AppCompatActivity {

    Button registration;
    Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_start);
        registration = findViewById(R.id.startRegistration);
        login = findViewById(R.id.startLogin);
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AD_startActivity.this,AD_registration.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AD_startActivity.this,AD_login.class);
                startActivity(intent);
            }
        });
    }
}
