package com.example.ad_lapitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AD_allUsers extends AppCompatActivity {

    Toolbar AllUsersToolbar;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    FirebaseRecyclerOptions<AD_users> options;
    ArrayList<String> names, allStatus, images;
    FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_all_users);
        progressDialog = new ProgressDialog(this);

        names = new ArrayList<>();
        images = new ArrayList<>();
        allStatus = new ArrayList<>();
        mauth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

//        options = new FirebaseRecyclerOptions.Builder<AD_users>().setQuery(databaseReference,AD_users.class).build();

        AllUsersToolbar = findViewById(R.id.allUsersToolBar);
        setSupportActionBar(AllUsersToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        recyclerView = findViewById(R.id.AllUserRecyclerView);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initData();
    }

    private void initData() {

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                if (!key.equals(mauth.getCurrentUser().getUid())) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String status = dataSnapshot.child("status").getValue(String.class);
                    String image = dataSnapshot.child("image").getValue(String.class);
                    names.add(name);
                    allStatus.add(status);
                    images.add(image);

                    RecyclerView recyclerView = findViewById(R.id.AllUserRecyclerView);

                    friendsFragmentAdapter adapter = new friendsFragmentAdapter(AD_allUsers.this, names, allStatus,images);


                    final LinearLayoutManager layoutManager = new LinearLayoutManager(AD_allUsers.this);
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


//    @Override
//    protected void onStart() {
//        super.onStart();
//        showProgressDialog();
//        FirebaseRecyclerAdapter<AD_users,usersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AD_users, usersViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull usersViewHolder holder, int position, @NonNull AD_users model) {
//
//                final String Uid = getRef(position).getKey();
//
//                if(!Uid.equals(mauth.getCurrentUser().getUid()))
//                {
//                    holder.setName(model.getName());
//                    holder.setStatus(model.getStatus());
//
//                    // this is getting the key/Uid which is pressed
//
//                    progressDialog.cancel();
//                    holder.mview.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(AD_allUsers.this,AD_profile_Activity.class);
//                            intent.putExtra("user_id",Uid);
//                            startActivity(intent);
//                        }
//                    });
//                }
//                else
//                {
//                    holder.setName("");
//                    holder.setStatus("");
//                }
//            }
//
//            @NonNull
//            @Override
//            public usersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_user_single_layout,parent,false);
//                return new usersViewHolder(v);
//            }
//        };
//        firebaseRecyclerAdapter.startListening();
//        recyclerView.setAdapter(firebaseRecyclerAdapter);
//    }

    private void showProgressDialog() {
        progressDialog.setTitle("Loading Users");
        progressDialog.setMessage("Please Wait While We Load Users Data");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    public class friendsFragmentAdapter extends RecyclerView.Adapter<friendsFragmentAdapter.friendsFragmentHolder> {

        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> date = new ArrayList<>();
        ArrayList<String> allImages = new ArrayList<>();
        Context context;

        public friendsFragmentAdapter(Context context, ArrayList<String> name, ArrayList<String> date,ArrayList<String> allImages) {
            this.name = name;
            this.date = date;
            this.context = context;
            this.allImages = allImages;
        }

        @NonNull
        @Override
        public friendsFragmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_user_single_layout, parent, false);
            friendsFragmentHolder holder = new friendsFragmentHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull friendsFragmentHolder holder, final int position) {

            holder.name.setText(name.get(position));
            holder.date.setText(date.get(position));

            holder.eachLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "" + name.get(position), Toast.LENGTH_SHORT).show();

                    databaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if(dataSnapshot.child("name").getValue(String.class).equals(name.get(position)) && dataSnapshot.child("image").getValue(String.class).equals(allImages.get(position)))
                            {
                                String Uid = dataSnapshot.getKey();
                                Intent intent = new Intent(AD_allUsers.this, AD_profile_Activity.class);
                                Log.d("AD_check", "onClick: " + Uid);
                                intent.putExtra("user_id", Uid);
                                context.startActivity(intent);
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
            });

        }

        @Override
        public int getItemCount() {
            return name.size();
        }


        class friendsFragmentHolder extends RecyclerView.ViewHolder {
            ConstraintLayout eachLayout;
            ImageView image;
            TextView name, date;

            public friendsFragmentHolder(@NonNull View itemView) {
                super(itemView);
                eachLayout = itemView.findViewById(R.id.eachLayout);
                image = itemView.findViewById(R.id.imageView);
                name = itemView.findViewById(R.id.single_userName);
                date = itemView.findViewById(R.id.single_userStatus);
            }
        }

    }


    // for offline features use firebaseDatabase.keepSynced("true");
}

