package com.example.balkhandeyash514;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity {

    //init views
    SwitchCompat postSwitch;

    //Interstitial ads (Full Screen)
    InterstitialAd mInterstitialAd;

    ScheduledExecutorService schedular;

    //use shared Preference to save the state of switch
    SharedPreferences sp;
    SharedPreferences.Editor editor; // to edit value of shared pref

    //constant for topic
    private static final String TOPIC_POST_NOTIFICATION = "POST"; //assign any value but use same for this kind

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        postSwitch = findViewById(R.id.postSwitch);

        //init sp
        sp = getSharedPreferences("Notification_SP", MODE_PRIVATE);
        boolean isPostEnabled = sp.getBoolean(""+TOPIC_POST_NOTIFICATION, false);
        //if enabled check switch, otherwise uncheck switch by default unchecked/false
        if (isPostEnabled) {
            postSwitch.setChecked(true);
        }
        else {
            postSwitch.setChecked(false);
        }


        postSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //edit switch state
                editor = sp.edit();
                editor.putBoolean(""+TOPIC_POST_NOTIFICATION, isChecked);
                editor.apply();

                if (isChecked) {
                   subscribePostNotification(); //call to subscribe
                }
                else {
                    unsubscribePostNotification(); //call to unsubscribe
                }
            }
        });

        prepareAd();
        schedular = Executors.newSingleThreadScheduledExecutor();
        schedular.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mInterstitialAd.isLoaded()) {
                            //ad is loaded
                            mInterstitialAd.show();
                        } else {
                            //ad is not loaded
                            Log.d("TAG", "The interstitial wasn't loaded yet.");
                        }
                        prepareAd();
                    }
                });
            }
        }, 5, 5, TimeUnit.SECONDS); //dislay after every 5 seconds

        /*Displaying Ads even after closing the Application/App/Activity may violate AdMob policy
         * so when activity is destroyed/paused ads should'nt be displayed
         * so lets handle this situation*/

        MobileAds.initialize(this, "ca-app-pub-2276510984916133/7112140085");
    }
    private void prepareAd() {
        //prepare the ad
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2276510984916133/7112140085");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onDestroy() {
        //when activity destroyed stop displaying ads
        schedular.shutdown();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        schedular.shutdown();
        super.onBackPressed();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void unsubscribePostNotification() {
        //unsubscribe to a topic (Post) to disable it's notification
        FirebaseMessaging.getInstance().unsubscribeFromTopic(""+TOPIC_POST_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will not receive post notifications";
                        if (!task.isSuccessful()) {
                            msg = "UnSubscription failed";
                        }
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void subscribePostNotification() {
        //subscribe to a topic (Post) to enable it's notification
        FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_POST_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will receive post notifications";
                        if (!task.isSuccessful()) {
                            msg = "Subscription failed";
                        }
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}