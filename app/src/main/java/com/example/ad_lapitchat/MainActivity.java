package com.example.ad_lapitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mauth;
    Toolbar mainPageToolBar;
    ViewPager viewPager;
    SectionsPagerAdapter pagerAdapter;
    TabLayout tabLayout;
    DatabaseReference databaseReferenceCheckOnlineOrOfflineFeature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mauth = FirebaseAuth.getInstance();
        mainPageToolBar = findViewById(R.id.MainPageAppBar);
        viewPager = findViewById(R.id.main_ViewPager);
        setSupportActionBar(mainPageToolBar);
        getSupportActionBar().setTitle(R.string.AppName);

        pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout = findViewById(R.id.main_TabLayout);
        tabLayout.setupWithViewPager(viewPager);


    }


    // check if user is logged in
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mauth.getCurrentUser();
        if(currentUser==null)
        {
            sendToStart();
        }
        else
        {
            FirebaseAuth mauth = FirebaseAuth.getInstance();

            final DatabaseReference databaseReferenceCheckOnlineOrOfflineFeature = FirebaseDatabase.getInstance().getReference().child("Users").child(mauth.getCurrentUser().getUid());
            databaseReferenceCheckOnlineOrOfflineFeature.child("online").setValue("True");
            databaseReferenceCheckOnlineOrOfflineFeature.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null)
                    {
                        databaseReferenceCheckOnlineOrOfflineFeature.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mauth.getCurrentUser();
        if(currentUser!=null)
        {
            databaseReferenceCheckOnlineOrOfflineFeature = FirebaseDatabase.getInstance().getReference().child("Users").child(mauth.getCurrentUser().getUid());
            databaseReferenceCheckOnlineOrOfflineFeature.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    // inflate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.admenu,menu);
        return true;
    }

    // when item selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.Account_logout)
        {
            final DatabaseReference databaseReferenceCheckOnlineOrOfflineFeature = FirebaseDatabase.getInstance().getReference().child("Users").child(mauth.getCurrentUser().getUid());
            databaseReferenceCheckOnlineOrOfflineFeature.child("online").setValue(ServerValue.TIMESTAMP);
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        else if(item.getItemId() == R.id.Account_settings)
        {
            Intent intent = new Intent(MainActivity.this,ADaccountSettings.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.All_Users)
        {
            Intent intent = new Intent(MainActivity.this,AD_allUsers.class);
            startActivity(intent);
        }
        return true;
    }

    // start method
    void sendToStart()
    {
        Intent intent = new Intent(MainActivity.this,AD_startActivity.class);
        startActivity(intent);
        finish();
    }


}
