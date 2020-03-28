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
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class ADchatFragment extends Fragment {

    public ADchatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_adchat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("AD_id", "onViewCreated: in onViewCreated");

        FirebaseAuth mauth = FirebaseAuth.getInstance();


        final ArrayList<String> status = new ArrayList<>();
        final ArrayList<String> Uid = new ArrayList<>();
        final ArrayList<String> names = new ArrayList<>();
        final ArrayList<String> images = new ArrayList<>();

        String currentUid = mauth.getCurrentUser().getUid();

        DatabaseReference uidReference = FirebaseDatabase.getInstance().getReference().child("messages").child(currentUid);
        uidReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String id = dataSnapshot.getKey();
                Uid.add(id);


                for(int i=0; i<Uid.size(); i++)
                {
                    DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("Users");
                    final int finalI = i;
                    users.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if(dataSnapshot.getKey().equals(Uid.get(finalI)))
                            {
                                images.add(dataSnapshot.child("image").getValue(String.class));
                                names.add(dataSnapshot.child("name").getValue(String.class));
                                status.add(dataSnapshot.child("status").getValue(String.class));


                                RecyclerView recyclerView = view.findViewById(R.id.chatRecyclerView);
                                friendsFragmentAdapter adapter = new friendsFragmentAdapter(getContext(),names,status,images);

                                final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                recyclerView.setLayoutManager(layoutManager);

                                recyclerView.setAdapter(adapter);
                                recyclerView.setHasFixedSize(true);
                                Log.d("AD_size", "onChildAdded: " + names.size());
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
                Log.d("AD_size", "onViewCreated: " + Uid.size());
            }
        });



    }


}
