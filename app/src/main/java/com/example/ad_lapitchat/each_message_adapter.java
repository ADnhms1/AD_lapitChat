package com.example.ad_lapitchat;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class each_message_adapter extends RecyclerView.Adapter<each_message_adapter.each_message_holder> {

    ArrayList<String> mesages = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();
    ArrayList<String> from = new ArrayList<>();
    ArrayList<String> time = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();
    FirebaseAuth mauth = FirebaseAuth.getInstance();
    Context context;

    public each_message_adapter(Context context,ArrayList<String> mesages,ArrayList<String> from,ArrayList<String> time,ArrayList<String> name) {
        this.mesages = mesages;
//        this.images = images;
        this.from = from;
        this.time = time;
        this.name = name;
        this.context = context;
    }

    @NonNull
    @Override
    public each_message_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_message_layout,parent,false);
        each_message_holder holder = new each_message_holder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull each_message_holder holder, final int position) {

        holder.message.setText(mesages.get(position));
//        holder.name.setText(name.get(position));
//        holder.time.setText(time.get(position));
//        Picasso.get().load(images.get(0)).placeholder(R.drawable.ic_account_circle_black_24dp).into(holder.image);

//        message sent

        if(from.get(position).equals(mauth.getCurrentUser().getUid()))
        {
            holder.message.setBackgroundResource(R.drawable.current_message_background);
            holder.message_single_layout.setGravity(Gravity.RIGHT);
        }

//        message got

        if(from.get(position).equals(ADchatActivity.mchatUser))
        {
            holder.message.setBackgroundResource(R.drawable.message_text_background);
            holder.message_single_layout.setGravity(Gravity.LEFT);
        }

        holder.message_single_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,""+mesages.get(position),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mesages.size();
    }

    class each_message_holder extends RecyclerView.ViewHolder
    {
        TextView message,time,name;
        LinearLayout message_single_layout;
        public each_message_holder(@NonNull View itemView) {
            super(itemView);
            message = (TextView)itemView.findViewById(R.id.message_User_Text);
//            time = itemView.findViewById(R.id.message_User_time);
//            name  = itemView.findViewById(R.id.message_User_name);
            message_single_layout = itemView.findViewById(R.id.message_single_layout);
        }
    }
}
