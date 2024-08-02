package com.moazzem.mehedidesign.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import com.moazzem.mehedidesign.BuildConfig;
import com.moazzem.mehedidesign.R;
import com.moazzem.mehedidesign.tools.AdsInitMOB;

public class SplashActivity extends AdsInitMOB {private void ad(){TextView textView = findViewById(R.id.copy);if (!BuildConfig.DEBUG) textView.setText("Powered By "+BuildConfig.CPY);}



    private ConsentInformation consentInformation;
    ConsentRequestParameters params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);ad();


        if (isNetworkConnected()){

            params = new ConsentRequestParameters
                    .Builder()
                    .setTagForUnderAgeOfConsent(false)
                    .build();

            consentInformation = UserMessagingPlatform.getConsentInformation(this);


            if (consentInformation.canRequestAds()) {
                showOpenAds();
            } else {

                consentInformation.requestConsentInfoUpdate(
                        this,
                        params,
                        (ConsentInformation.OnConsentInfoUpdateSuccessListener) () -> {
                            UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                                    this,
                                    (ConsentForm.OnConsentFormDismissedListener) loadAndShowError -> {
                                        if (loadAndShowError != null) {
                                            // Consent gathering failed.
                                            Log.w(TAG, String.format("%s: %s",
                                                    loadAndShowError.getErrorCode(),
                                                    loadAndShowError.getMessage()));
                                            startApp();

                                        }

                                        // Consent has been gathered.
                                        if (consentInformation.canRequestAds()) {
                                            showOpenAds();
                                        }
                                    }
                            );
                        },
                        (ConsentInformation.OnConsentInfoUpdateFailureListener) requestConsentError -> {
                            startApp();

                            // Consent gathering failed.
                            Log.w(TAG, String.format("%s: %s",
                                    requestConsentError.getErrorCode(),
                                    requestConsentError.getMessage()));
                        });
            }



        } else {
            new AlertDialog.Builder(this)
                    .setMessage("No internet connection please try again.!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }

    }

    private void startApp() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 3000);

    }


    private void showOpenAds() {
        AppOpenAd.load(
                SplashActivity.this,
                getResources().getString(R.string.APP_OPEN_ID),
                new AdRequest.Builder().build(),
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        ad.show(SplashActivity.this);
                        ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle ad loading failure
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }



                }
        );
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}