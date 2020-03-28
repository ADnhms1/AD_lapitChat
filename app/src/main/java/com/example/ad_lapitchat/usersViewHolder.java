package com.example.ad_lapitchat;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.widget.Toast.LENGTH_SHORT;

public class usersViewHolder extends RecyclerView.ViewHolder {
    View mview;

    public usersViewHolder(@NonNull View itemView) {
        super(itemView);
        mview = itemView;
    }

    public void setName(String name) {
        TextView username = mview.findViewById(R.id.single_userName);
        username.setText(name);
    }

    public void setStatus(String status)
    {
        TextView userstatus = mview.findViewById(R.id.single_userStatus);
        userstatus.setText(status);
    }
}