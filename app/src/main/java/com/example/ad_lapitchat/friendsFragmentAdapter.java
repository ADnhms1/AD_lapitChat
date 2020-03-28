package com.example.ad_lapitchat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class friendsFragmentAdapter extends RecyclerView.Adapter<friendsFragmentAdapter.friendsFragmentHolder> {

    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> date = new ArrayList<>();
    ArrayList<String> allImages = new ArrayList<>();
    Context context;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

    public friendsFragmentAdapter(Context context,ArrayList<String> name, ArrayList<String> date,ArrayList<String> allImages) {
        this.name = name;
        this.date = date;
        this.allImages = allImages;
        this.context = context;
    }

    @NonNull
    @Override
    public friendsFragmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_user_single_layout,parent,false);
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
                CharSequence options[] = new CharSequence[]{"View Profile","View Chat"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0)
                        {

                            databaseReference.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    if(dataSnapshot.child("name").getValue(String.class).equals(name.get(position)))
                                    {
                                        String Uid = dataSnapshot.getKey();
                                        Intent intent = new Intent(context, AD_profile_Activity.class);
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
                        else if(which==1)
                        {
                            Toast.makeText(context, "chat", Toast.LENGTH_SHORT).show();


                            databaseReference.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    if(dataSnapshot.child("name").getValue(String.class).equals(name.get(position)))
                                    {
                                        String Uid = dataSnapshot.getKey();
                                        String name = dataSnapshot.child("name").getValue(String.class);
                                        Intent intent = new Intent(context, ADchatActivity.class);
                                        Log.d("AD_check", "onClick: " + Uid);
                                        intent.putExtra("user_id", Uid);
                                        intent.putExtra("user_name",name);
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
                    }
                });

                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return name.size();
    }


    class friendsFragmentHolder extends RecyclerView.ViewHolder
    {
        ConstraintLayout eachLayout;
        ImageView image;
        TextView name,date;
        public friendsFragmentHolder(@NonNull View itemView) {
            super(itemView);
            eachLayout = itemView.findViewById(R.id.eachLayout);
            image = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.single_userName);
            date = itemView.findViewById(R.id.single_userStatus);
        }
    }

}
