package com.example.ad_lapitchat;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ADrequestFragment extends Fragment {

    FirebaseAuth mauth;
    public ADrequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_adrequest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mauth = FirebaseAuth.getInstance();
        final ArrayList<String> names = new ArrayList<>();
        final ArrayList<String> images = new ArrayList<>();
        final ArrayList<String> status = new ArrayList<>();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Notification").child(mauth.getCurrentUser().getUid());

        final ArrayList<String> requestUsers = new ArrayList<>();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.child("Type").getValue(String.class).equals("request"))
                {
                    String userID = dataSnapshot.child("From").getValue(String.class);
                    requestUsers.add(userID);
                    for(int i=0; i<requestUsers.size(); i++)
                    {
                        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("Users");
                        final int finalI = i;
                        users.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                if(dataSnapshot.getKey().equals(requestUsers.get(finalI)))
                                {
                                    images.add(dataSnapshot.child("image").getValue(String.class));
                                    names.add(dataSnapshot.child("name").getValue(String.class));
                                    status.add(dataSnapshot.child("status").getValue(String.class));

                                    RecyclerView recyclerView = view.findViewById(R.id.requestRecyclerView);

                                    friendsFragmentAdapter adapter = new friendsFragmentAdapter(getContext(),names,status,images);


                                    final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                    recyclerView.setLayoutManager(layoutManager);

                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setHasFixedSize(true);
                                }
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
