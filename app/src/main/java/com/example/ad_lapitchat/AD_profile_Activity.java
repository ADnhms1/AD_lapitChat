package com.example.ad_lapitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class AD_profile_Activity extends AppCompatActivity {

    TextView profileName,profileStatus,profileNoOfFriends;

    Button profileSendRequest,profileRejectRequest;

    DatabaseReference mainDatabaseReference;
    DatabaseReference acceptRequestDatabaseReference;
    DatabaseReference notificationDatabaseReference;

    ImageView profileImage;

    ProgressDialog progressDialog;

    FirebaseAuth mauth;

    String current_State;

    String Uid;

    String UidOfOtherUser;

    int c;

    ProgressDialog acceptRequestProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_profile_);
        progressDialog = new ProgressDialog(this);
        acceptRequestProgressDialog = new ProgressDialog(AD_profile_Activity.this);
        UidOfOtherUser = getIntent().getStringExtra("user_id");
        current_State = "not_Sent";
        mauth = FirebaseAuth.getInstance();
        Uid = mauth.getCurrentUser().getUid();
        c=0;
        // Check if the user is friend

        checkFriends(Uid,UidOfOtherUser);

        profileImage = (ImageView) findViewById(R.id.profileImage);
        profileName = (TextView) findViewById(R.id.profileName);
        profileStatus = (TextView) findViewById(R.id.profileStatus);
        profileNoOfFriends = (TextView) findViewById(R.id.profileNoOfFriends);
        profileSendRequest = (Button) findViewById(R.id.profileSendReqBtn);
        profileRejectRequest = (Button) findViewById(R.id.profileRejectRequest);

        mainDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(UidOfOtherUser);
        acceptRequestDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        notificationDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Notification");


        checkCurrentState();

        profileRejectRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRequest(Uid,UidOfOtherUser);
                profileRejectRequest.setVisibility(View.INVISIBLE);
            }
        });

        mainDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.setTitle("Loading Data");
                progressDialog.setMessage("Please Wait Loading Users Data");
                progressDialog.setCancelable(false);
                progressDialog.show();
                String displayName = dataSnapshot.child("name").getValue().toString();
                String displayStatus = dataSnapshot.child("status").getValue().toString();
                String displayImage = dataSnapshot.child("image").getValue().toString();

                profileName.setText(displayName);
                profileStatus.setText(displayStatus);
                Picasso.get().load(displayImage).placeholder(R.drawable.ic_account_circle_black_24dp).into(profileImage);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        buttonClickListener();

    }

    void sendRequest(final String Uid, final String UidOfOtherUser)
    {
        showProgressDialog();
        DatabaseReference sendingDatabaseReference = mainDatabaseReference.child(Uid).child(UidOfOtherUser).child("Request_Type");
        sendingDatabaseReference.setValue("Sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {

                    DatabaseReference receivingDatabaseReference = mainDatabaseReference.child(UidOfOtherUser).child(Uid).child("Request_Type");
                    receivingDatabaseReference.setValue("Received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                HashMap<String,String> notification = new HashMap<>();
                                notification.put("From",Uid);
                                notification.put("Type","request");
                                notificationDatabaseReference.child(UidOfOtherUser).push().setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(AD_profile_Activity.this,"Request Sent!",Toast.LENGTH_SHORT).show();
                                            profileSendRequest.setText("Cancel Sent Request");
                                            cancelProgressDialog();
                                            current_State="Request Sent";
                                        }
                                        else
                                        {
                                            Toast.makeText(AD_profile_Activity.this,"Sending Request Failed!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(AD_profile_Activity.this,"Sending Request Failed!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(AD_profile_Activity.this,"Sending Request Failed!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void cancelRequest(final String Uid, final String UidOfOtherUser) {
        showProgressDialog();
        final DatabaseReference sendingDatabaseReference = mainDatabaseReference.child(Uid).child(UidOfOtherUser);
        sendingDatabaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    DatabaseReference currentDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friend_Request").child(Uid).child(UidOfOtherUser);
                    currentDatabaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                DatabaseReference otherDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friend_Request").child(UidOfOtherUser).child(Uid);
                                otherDatabaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {

                                            final DatabaseReference notification = FirebaseDatabase.getInstance().getReference().child("Notification").child(UidOfOtherUser);
                                            notification.addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                    if(dataSnapshot.child("From").getValue().toString().equals(Uid))
                                                    {
                                                        DatabaseReference delete = notification.child(dataSnapshot.getKey());
                                                        delete.removeValue();
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
                                            Log.d("ADdata", "onComplete: Success");
                                            cancelProgressDialog();
                                            profileSendRequest.setText("Send Friend Request");
                                            current_State = "not_Sent";
                                            profileRejectRequest.setEnabled(false);
                                            profileRejectRequest.setVisibility(View.INVISIBLE);
                                        }
                                        else
                                        {
                                            Log.d("ADdata", "onComplete: Failure");
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Log.d("ADdata", "onComplete: Failure");
                            }
                        }
                    });
                }
                else
                {
                    Log.d("ADdata", "onComplete: Failure");
                }
            }
        });
    }

    void rejectRequest(final String UidN, String UidOfOtherUserN)
    {
        Log.d("AD_reject", "rejectRequest: called");
        final DatabaseReference removeNotification = FirebaseDatabase.getInstance().getReference().child("Notification").child(UidOfOtherUserN);
        removeNotification.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.child("From").getValue().toString().equals(UidN))
                {
                    DatabaseReference remove = removeNotification.child(dataSnapshot.getKey());
                    remove.removeValue();
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
        current_State = "not_Sent";
        profileSendRequest.setText("Send Friend Request");
    }

    void acceptRequest(final String uid, final String uidOfOtherUser)
    {
        showProgressDialog();
        final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
        DatabaseReference extracting_Friends_Data_DatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(UidOfOtherUser);
        extracting_Friends_Data_DatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String status = dataSnapshot.child("status").getValue(String.class);
                HashMap <String,String> userdata = new HashMap<>();
                userdata.put("name",name);
                userdata.put("status",status);
                userdata.put("date",currentDate);

                acceptRequestDatabaseReference.child(uid).child(UidOfOtherUser).setValue(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            DatabaseReference extracting_Current_loggedinUser_DatabaseReference  = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            extracting_Current_loggedinUser_DatabaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String currentName = dataSnapshot.child("name").getValue(String.class);
                                    String currentStatus = dataSnapshot.child("status").getValue(String.class);
                                    HashMap <String,String> currentUserdata = new HashMap<>();
                                    currentUserdata.put("name",currentName);
                                    currentUserdata.put("status",currentStatus);
                                    currentUserdata.put("date",currentDate);

                                    acceptRequestDatabaseReference.child(UidOfOtherUser).child(uid).setValue(currentUserdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                DatabaseReference currentDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friend_Request").child(uid).child(uidOfOtherUser);
                                                currentDatabaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            DatabaseReference otherDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friend_Request").child(uidOfOtherUser).child(uid);
                                                            otherDatabaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful())
                                                                    {
                                                                        final DatabaseReference notification = notificationDatabaseReference.child(uid);
                                                                        notification.addChildEventListener(new ChildEventListener() {
                                                                            @Override
                                                                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                                                Log.d("AD_get", "onChildAdded: " + dataSnapshot.child("From").getValue(String.class) + "   " + uidOfOtherUser );
                                                                                Log.d("AD_get", "onChildAdded: " + dataSnapshot.getKey() + " " + mauth.getCurrentUser().getUid());
                                                                                Log.d("AD_get", "onChildAdded: " + dataSnapshot.child("From").getValue(String.class) + "   " + uidOfOtherUser );
                                                                                if(dataSnapshot.child("From").getValue(String.class).equals(UidOfOtherUser))
                                                                                {
                                                                                    DatabaseReference deleteNotification = notification.child(dataSnapshot.getKey());
                                                                                    deleteNotification.removeValue();
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

                                                                        Log.d("ADdata", "onComplete: Success");
                                                                        cancelProgressDialog();
                                                                        current_State = "Friends";
                                                                        profileRejectRequest.setEnabled(false);
                                                                        profileRejectRequest.setVisibility(View.INVISIBLE);
                                                                    }
                                                                    else
                                                                    {
                                                                        Log.d("ADdata", "onComplete: Failure");
                                                                    }
                                                                }
                                                            });
                                                        }
                                                        else
                                                        {
                                                            Log.d("ADdata", "onComplete: Failure");
                                                        }
                                                    }
                                                });
                                            }
                                            else
                                            {
                                                Log.d("ADdata", "onComplete: Failure");
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else
                        {
                            Log.d("ADdata", "onComplete: Failure");
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    void checkFriends(final String uid,final String uidOfOtherUser)
    {
        DatabaseReference checkFriendsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(uid);
        checkFriendsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(uidOfOtherUser))
                {
                    current_State = "Friends";
                    profileSendRequest.setText("Unfriend");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void unfriendUser(final String uid,final String uidOfOtherUser)
    {
        showProgressDialog();
        final DatabaseReference mainUnfriendDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        final DatabaseReference currentUser = mainUnfriendDatabaseReference.child(uid).child(uidOfOtherUser);
        currentUser.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    DatabaseReference friendUser = mainUnfriendDatabaseReference.child(uidOfOtherUser).child(uid);
                    friendUser.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                current_State = "not_Sent";
                                profileSendRequest.setText("Send Friend Request");
                                cancelProgressDialog();
                            }
                            else
                            {
                                Toast.makeText(AD_profile_Activity.this,"Unfriend Error",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(AD_profile_Activity.this,"Unfriend Error",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void showProgressDialog()
    {
        Log.d("ADprogress", "showProgressDialog: inside show start");
        acceptRequestProgressDialog.setTitle("Processing");
        acceptRequestProgressDialog.setMessage("Please Wait!");
        acceptRequestProgressDialog.setCancelable(false);
        acceptRequestProgressDialog.show();
        Log.d("ADprogress", "showProgressDialog: inside show end");
    }

    void cancelProgressDialog()
    {
        Log.d("ADprogress", "showProgressDialog: inside cancel start");
        acceptRequestProgressDialog.dismiss();
        Log.d("ADprogress", "showProgressDialog: inside cancel end");
    }

    void checkCurrentState()
    {
        DatabaseReference check = FirebaseDatabase.getInstance().getReference().child("Friend_Request").child(Uid).child(UidOfOtherUser);
        check.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String data = dataSnapshot.child("Request_Type").getValue().toString();
                    if(data.equals("Sent"))
                    {
                        current_State = "sent";
                        profileSendRequest.setText("Cancel Request");
                        Log.d("ADfound", "onDataChange:  " + current_State);
                    }
                    else if(data.equals("Received"))
                    {
                        current_State = "req_Received";
                        profileSendRequest.setText("Accept Request");
                        profileRejectRequest.setVisibility(View.VISIBLE);
                        profileRejectRequest.setEnabled(true);
                        profileRejectRequest.setClickable(true);
                        Log.d("ADfound", "onDataChange:  " + current_State);
                    }
                }
                catch(Exception e)
                {
                    Log.d("ADfound", "onDataChange: exception " + current_State);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void buttonClickListener()
    {
        profileSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friend_Request");

                //----------Send Request State------------//

                if(current_State=="not_Sent")
                {
                    sendRequest(Uid,UidOfOtherUser);
                    Log.d("ADfound", "onDataChange: in if " + current_State);
                }

                //----------Accept Request State------------//

                else if(current_State.equals("req_Received"))
                {
                    acceptRequest(Uid,UidOfOtherUser);
                }

                //----------Friends State------------//
                else if(current_State.equals("Friends"))
                {
                    unfriendUser(Uid,UidOfOtherUser);
                }
//                ----------Cancel Friend Request----//
                else{
                    cancelRequest(Uid, UidOfOtherUser);
                    current_State = "not_sent";
                    Log.d("ADfound", "onDataChange: in if " + current_State);
                }

                Log.d("AD_check", "onClick: " + current_State);
            }
        });
    }
}
