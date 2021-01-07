package com.mathma.mathtrainew;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Splash extends AppCompatActivity {

    private FirebaseRemoteConfig firebaseRemoteConfig;
    private RelativeLayout relativeLayout;

    //testpart
    private ImageView imageView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_test);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);

        //TestPart
        imageView.findViewById(R.id.image);
        button.findViewById(R.id.btn_load);
        relativeLayout.findViewById(R.id.relLay);

        //firebase
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("btn_text", "Version 1.0");
        defaultData.put("btn_enable", false);
        defaultData.put("image_link", "https://image.freepik.com/free-vector/christmas-background_1048-6750.jpg");
        firebaseRemoteConfig.setDefaultsAsync(defaultData);

        //Load image
        Picasso.get().load("https://image.freepik.com/free-vector/christmas-background_1048-6750.jpg").into(imageView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
    }
}