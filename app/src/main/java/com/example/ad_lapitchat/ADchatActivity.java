package com.example.ad_lapitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ADchatActivity extends AppCompatActivity {

    static String mchatUser;
    Toolbar toolbar;
    TextView title,lastSeen;
    FirebaseAuth mauth;
    EditText messageView;
    ImageButton addView,sendView;
    String Uid;
    DatabaseReference mrootRef;
    ArrayList<String> messages;
    ArrayList<String> image,name,time;
    ArrayList<String> from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adchat);

        // init the views
        messageView = findViewById(R.id.chat_Message_View);
        addView = findViewById(R.id.chat_Add);
        sendView = findViewById(R.id.chat_Send);
        name = new ArrayList<>();

        mrootRef = FirebaseDatabase.getInstance().getReference();


        mauth = FirebaseAuth.getInstance();

        mchatUser = getIntent().getExtras().getString("user_id");

        toolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);

        actionBar.setDisplayHomeAsUpEnabled(true);

        String name = getIntent().getStringExtra("user_name");      // getting name from friendsFragmentAdapter using getIntent().getStringExtra("keyName");


        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_Bar_View = inflater.inflate(R.layout.chat_custom_header,null);

        actionBar.setCustomView(action_Bar_View);



        title = (TextView) findViewById(R.id.custom_Bar_Name);
        lastSeen = (TextView) findViewById(R.id.custom_Bar_LastSeen);
        Log.d("AD_check", "onCreate: " + title + " : " + lastSeen);

        title.setText(name);

        mrootRef.child("Users").child(mchatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                if(online.equals("True"))
                {
                    lastSeen.setText("Online");
                }
                else
                {
                    long time = Long.parseLong(online);
                    String lastTime = getTimeAgo(time,getApplicationContext());
                    lastSeen.setText(lastTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        Uid = mauth.getCurrentUser().getUid();
        mrootRef.child("chat").child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(mchatUser))
                {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("timeStamp", ServerValue.TIMESTAMP);
                    chatAddMap.put("seen",false);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("chat/" + Uid + "/" + mchatUser,chatAddMap);
                    chatUserMap.put("chat/" + mchatUser + "/" + Uid,chatAddMap);

                    mrootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError != null)
                            {
                                Log.d("AD_error", "onComplete: " + databaseError.getDetails());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                messageView.setText("");
            }
        });

        // loading data

        initData();
    }


    private void initData() {
        messages = new ArrayList<>();
        from = new ArrayList<>();
//        name = new ArrayList<>();
        time = new ArrayList<>();

        final DatabaseReference databaseReference = mrootRef.child("messages").child(Uid).child(mchatUser);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               if(dataSnapshot.hasChild("message") && dataSnapshot.hasChild("time"))
               {
                   String message = dataSnapshot.child("message").getValue().toString();
                   messages.add(message);
                   String messageTime = dataSnapshot.child("time").getValue().toString();
                   time.add(messageTime);
               }
               if(dataSnapshot.hasChild("from"))
               {
                   String senderID = dataSnapshot.child("from").getValue().toString();
                   from.add(senderID);
//                   DatabaseReference timeDatabaseReference = mrootRef.child("Users").child(senderID);
//                   timeDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                       @Override
//                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                           String messageName = dataSnapshot.child("name").getValue().toString();
//                           name.add(messageName);
//                           Log.d("AD_name", "onDataChange: " + messageName);
//                       }
//
//                       @Override
//                       public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                       }
//                   });
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

//        DatabaseReference chatUserImage = mrootRef.child("Users").child(Uid);
//        chatUserImage.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String i = dataSnapshot.child("image").getValue().toString();
//                image.add(i);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        initRecyclerView();
    }

    void initRecyclerView()
    {
        RecyclerView recyclerView = findViewById(R.id.messages_recycler_view);
        Log.d("AD_size", "initRecyclerView: " + name.size());
        each_message_adapter adapter = new each_message_adapter(this,messages,from,time,name);

        for(int i=0; i<name.size(); i++)
        {
            Log.d("AD_check_name", "initRecyclerView: " + name.get(i));
        }


        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        layoutManager.setReverseLayout(true);   // for showing data from bottom of recyclerView
    }

    void sendMessage()
    {
        String message = messageView.getText().toString();

        if(!TextUtils.isEmpty(message))
        {
            String currentUser = "messages/" + Uid + "/" + mchatUser;
            String chatUser = "messages/" + mchatUser + "/" + Uid;

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("messages").child(Uid).child(mchatUser).push();
            String pushID = databaseReference.getKey();

            final Map messageMap = new HashMap();

            messageMap.put("lastSeen",false);
            messageMap.put("type","text");
            messageMap.put("message",message);
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",Uid);


            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUser + "/" + pushID,messageMap);
            messageUserMap.put(chatUser + "/" + pushID, messageMap);

            mrootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError != null)
                    {
                        Log.d("AD_error", "onComplete: " + databaseError.getDetails());
                    }
                }
            });
        }

    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }


    public String getTimeAgoForString(long time, Context ctx) {
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
        String date = sfd.format(new Date(time));
        return date;
    }
}





