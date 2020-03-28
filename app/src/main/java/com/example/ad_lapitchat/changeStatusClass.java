package com.example.ad_lapitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class changeStatusClass extends AppCompatActivity {
    Toolbar Change_Status_Main_Toolbar;
    Button statusChangeButton;
    DatabaseReference databaseReference;
    FirebaseAuth mauth;
    TextView changeStatusTextView;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_status_class);
        progressDialog = new ProgressDialog(this);
        mauth = FirebaseAuth.getInstance();
        changeStatusTextView = findViewById(R.id.statusChangeTextView);
        statusChangeButton = findViewById(R.id.statusChangeButton);
        Change_Status_Main_Toolbar = findViewById(R.id.Change_Status_Main_Toolbar);
        setSupportActionBar(Change_Status_Main_Toolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        statusChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String Uid = mauth.getCurrentUser().getUid();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid).child("status");
                final String changeStatusValue = changeStatusTextView.getText().toString();
                progressDialog.setMessage("Uploading Status!");
                progressDialog.setCancelable(false);
                databaseReference.setValue(changeStatusValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            Intent intent = new Intent(changeStatusClass.this,ADaccountSettings.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(changeStatusClass.this,"Status Change Fail!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mauth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.child("status").getValue(String.class);
                changeStatusTextView.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
