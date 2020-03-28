package com.example.ad_lapitchat;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ADfriendsFragment extends Fragment {

    ArrayList<String> names,dates;
    ArrayList<String> images;
    FirebaseAuth mauth;

    public ADfriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        names = new ArrayList<>();
        dates = new ArrayList<>();
        images = new ArrayList<>();


        return inflater.inflate(R.layout.fragment_adfriends, container, false);
    }

    private void initData(final View view) {

        mauth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(mauth.getCurrentUser().getUid());

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String status = dataSnapshot.child("status").getValue(String.class);
                String image = dataSnapshot.child("image").getValue(String.class);

                Log.d("AD_check", "onChildAdded: " + name);     // here in log it show the value but its not showing me the data until i physically rotate my phone
                Log.d("AD_check", "onChildAdded: " + status);
                names.add(name);
                dates.add(status);
                images.add(image);


//                Calling the recyclerView here to add the data directly one after the other once fetched

                RecyclerView recyclerView = view.findViewById(R.id.friendsRecyclerView);

                friendsFragmentAdapter adapter = new friendsFragmentAdapter(getContext(),names,dates,images);


                final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);

                recyclerView.setAdapter(adapter);
                recyclerView.setHasFixedSize(true);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("AD_check", "onChildAdded: changed");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("AD_check", "onChildAdded: removed");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("AD_check", "onChildAdded: moved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
                Log.d("AD_check", "onChildAdded: cancel" + databaseError);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData(view);

    }
}
