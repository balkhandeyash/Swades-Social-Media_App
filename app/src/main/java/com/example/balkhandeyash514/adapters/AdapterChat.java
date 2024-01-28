package com.example.balkhandeyash514.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.balkhandeyash514.models.ModelChat;
import com.example.balkhandeyash514.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;


public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{


    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelChat> chatList;
    String imgUrl;

    FirebaseUser fUser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imgUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imgUrl = imgUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layouts: row_chat_left.xml for receiver, row_chat_right.xml for sender
        if (viewType ==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.new_chat_right, parent, false);
            return new MyHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.new_chat_left, parent, false);
            return new MyHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        //get data
        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimestamp();
        String type = chatList.get(position).getType();

//        convert time stamp to dd/mm/yyyy hh:mm am/pm
//        8

        try {
            if (type.equals("text")) {
                //text message
                holder.messageTv.setVisibility(View.VISIBLE);
                holder.messageIv.setVisibility(View.GONE);

                holder.messageTv.setText(message);
            }
            else {
                //image message
                holder.messageTv.setVisibility(View.GONE);
                holder.messageIv.setVisibility(View.VISIBLE);

                Picasso.get().load(message).placeholder(R.drawable.ic_image_black).into(holder.messageIv);
            }

            //set data
            holder.messageTv.setText(message);
            holder.timeTv.setText(timeStamp);


            //set data
//            holder.timeTv.setText(dateTime);
            Picasso.get().load(imgUrl).into(holder.profileTv);
        }
        catch (Exception e){
            Log.d("time", "onBindViewHolder: "+e.getMessage());
        }

        //click to show delete dialog
        holder.messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show delete message confirm dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this messaage ?");
                //delete button
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteMessage(position);
                    }
                });
                //cancel delete button
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss dialog
                        dialog.dismiss();
                    }
                });
                //create and show dialog
                builder.create().show();
            }
        });

//        //set seen/delivered status of message
//        if (position == chatList.size()-1){
//            if (chatList.get(position).isSeen()){
//                holder.isSeenTv.setText("Seen");
//            }
//            else {
//                holder.isSeenTv.setText("Delivered");
//            }
//        }
//        else {
////            holder.isSeenTv.setVisibility(View.GONE);
//        }
    }

    private void deleteMessage(int position) {
        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();



        /*Logic:
        * Get timestamp of clicked message
        * Compare the timestamp of the clicked with all message in Chats
        * Where both values matches delete that message*/
        String msgtime = chatList.get(position).getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dbRef.orderByChild("timeStamp").equalTo(msgtime);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {

                    try {
                            /*If you want to allow sender to delete only  his message then
                    compare sender value with current user's uid
                    * if they match means its the message of sender that is trying to deleted*/
                        if ((ds.child("sender").getValue()).equals(myUID)) {
                            /*We can do one of two things here
                             * 1) Remove the message from Chats
                             * 2) Set the value of message "This message was deleted ...
                             * So do whatever you want*/

                            //1) Remove the message from chats
//                        ds.getRef().removeValue();

                            //2) Set the value of message "This message was deleted"
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("message", "This message was deleted...");
                            ds.getRef().updateChildren(hashMap);

                            Toast.makeText(context, "message deleted..." , Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, "You can delete only your messages...", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e){
                        Log.d("z", "onDataChange: "+e.getMessage());
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get currently signed in user
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getReceiver().equals(fUser.getUid())){
            return MSG_TYPE_LEFT;
        }
        else {
            return MSG_TYPE_RIGHT;
        }
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{


        //views
        ImageView profileTv, messageIv;
        TextView messageTv, timeTv, isSeenTv;
        LinearLayout messageLayout; //for click listner to show delete

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            profileTv = itemView.findViewById(R.id.profileTv);
            messageIv = itemView.findViewById(R.id.messageIv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);
            messageLayout = itemView.findViewById(R.id.messageLayout);

        }
    }
}
