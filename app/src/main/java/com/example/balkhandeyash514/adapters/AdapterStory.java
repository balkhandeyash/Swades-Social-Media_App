package com.example.balkhandeyash514.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.balkhandeyash514.R;
import com.example.balkhandeyash514.models.ModelUser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterStory extends RecyclerView.Adapter<AdapterStory.MyHolder> {

    Context context;
    List<ModelUser> storyList;

    //for getting current user id
    FirebaseAuth firebaseAuth;
    String myUid;

    public AdapterStory(Context context, List<ModelUser> userList, FirebaseAuth firebaseAuth, String myUid) {
        this.context = context;
        this.storyList = userList;
        this.firebaseAuth = firebaseAuth;
        this.myUid = myUid;
    }

    @NonNull
    @Override
    public AdapterStory.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout(row_user.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.row_story, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterStory.MyHolder myHolder, int i) {
        //get data
        final String hisUID = storyList.get(i).getUid();
        String storyImage = storyList.get(i).getImage();
        String storyname = storyList.get(i).getName();

        //set data
        myHolder.ProfilenameTv.setText(storyname);
        try {
            Picasso.get().load(storyImage)
                    .placeholder(R.drawable.ic_add_image)
                    .into(myHolder.ProfileIv);
        }catch (Exception e){

        }

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView ProfileIv;
        TextView ProfilenameTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            ProfileIv = itemView.findViewById(R.id.storyIv);
            ProfilenameTv = itemView.findViewById(R.id.ProfilenameTv);
        }
    }
}
