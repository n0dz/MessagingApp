package com.nodz.messagingapp.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.nodz.messagingapp.Models.MessageModel;
import com.nodz.messagingapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter{
    ArrayList<MessageModel> messagesModels;
    Context context;
    String recId;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<MessageModel> messagesModels, Context context) {
        this.messagesModels = messagesModels;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessageModel> messagesModels, Context context, String recId) {
        this.messagesModels = messagesModels;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == SENDER_VIEW_TYPE){
            view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        }
        else{
            view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if(messagesModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }
        else{
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messagesModel = messagesModels.get(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String senderRoom = FirebaseAuth.getInstance().getUid() +recId;
                                database.getReference().child("chats").child(senderRoom)
                                        .child(messagesModel.getMessageId())
                                        .setValue(null);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                return false;
            }
        });

        if(holder.getClass() == SenderViewHolder.class){
            ((SenderViewHolder)holder).senderMsg.setText(messagesModel.getMessage());
            ((SenderViewHolder)holder).senderTime.setText(millisToDateChat(messagesModel.getTimestamp()));

        }
        else
        {
            ((ReceiverViewHolder)holder).receiverMsg.setText(messagesModel.getMessage());
            ((ReceiverViewHolder)holder).receiverTime.setText(millisToDateChat(messagesModel.getTimestamp()));
        }

    }

    @Override
    public int getItemCount() {
        return messagesModels.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{

        TextView receiverMsg, receiverTime;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiverText);
            receiverTime = itemView.findViewById(R.id.time);

        }
    }
    public class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView senderMsg, senderTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.timesender);
        }
    }
    public static String millisToDateChat(long time) {

        long currentTime = System.currentTimeMillis();
        long defe = currentTime - time;
        long  time_in;

        if(time!=0){
            time_in = time;
        }else{
            time_in = currentTime;
            defe = 0;
        }

        int s = (int)defe/1000;
        int m = (int)defe/(1000*60);
        int h = (int)defe/(1000*60*60);
        int d = (int)defe/(1000*60*60*24);
        int w = (int)defe/(1000*60*60*24*7);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time_in);
        Date date = calendar.getTime();
        @SuppressLint("SimpleDateFormat") String formattedDate=(new SimpleDateFormat("HH:mm")).format(date);
        @SuppressLint("SimpleDateFormat") String formattedYear=(new SimpleDateFormat("MMM d, ''yy")).format(date);
        @SuppressLint("SimpleDateFormat") String formattedm=(new SimpleDateFormat("MMM d")).format(date);

        if(d>365) {
            return formattedYear;
        }else if(s>172000){
            return formattedm;
        }else if(s>86400) {
            return "Yest.";
        }else{
            return formattedDate;
        }
    }
}
