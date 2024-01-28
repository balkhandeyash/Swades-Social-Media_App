package com.example.balkhandeyash514;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        //create firebase offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //after enabling it, the data that is loaded will able to see offline

    }
}
