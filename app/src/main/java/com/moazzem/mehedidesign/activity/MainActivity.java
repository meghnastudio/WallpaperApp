package com.moazzem.mehedidesign.activity;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.moazzem.mehedidesign.VersionChecker;
import com.moazzem.mehedidesign.tools.AdsInitMOB;
import com.moazzem.mehedidesign.BuildConfig;
import com.moazzem.mehedidesign.fragment.CategoryFragment;
import com.moazzem.mehedidesign.fragment.FavoriteFragment;
import com.moazzem.mehedidesign.tools.AdsMob;
import com.moazzem.mehedidesign.tools.CustomPref;
import com.moazzem.mehedidesign.R;
import com.moazzem.mehedidesign.tools.PushNotificationManager;
import com.onesignal.OneSignal;


public class MainActivity extends AdsInitMOB {


    DrawerLayout drawerLayout;
    ImageView openMenu;
    NavigationView navigationView;
    CustomPref customPref;
    MediaPlayer mediaPlayer;
    ProgressBar progressBar;
    private void mSound(int sound) {
        if (customPref.getSound()){
            float volume = 0.1f; // 50% volume
            mediaPlayer = MediaPlayer.create(getApplicationContext(), sound);
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mediaPlayer -> mediaPlayer.release());
        }
    }
    Boolean home = true;
    TextView title;
    boolean isFev;


    PushNotificationManager pushNotificationManager;

    public static boolean isAD = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
        new VersionChecker(this).checkForUpdate();

        pushNotificationManager = new PushNotificationManager(this);
        pushNotificationManager.checkAndRequestNotificationPermission();
        FirebaseApp.initializeApp(this);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(getResources().getString(R.string.Onesignal_app_ID));


        isFev = getIntent().getBooleanExtra("isFev", false);
        customPref = new CustomPref(MainActivity.this);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        title = findViewById(R.id.title);
        openMenu = findViewById(R.id.openMenu);
        progressBar = findViewById(R.id.progress);
        openMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSound(R.raw.click);
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });


        if (isFev){
            replaceFragment(new FavoriteFragment(), "Favorite");
            home = false;
        }else {
            replaceFragment(new CategoryFragment(), "Category");
        }


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mSound(R.raw.click);
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (item.getItemId()){

                    case R.id.nev_fav:
                        replaceFragment(new FavoriteFragment(), "Favorite");
                        home = false;
                        break;

                    case R.id.nav_facebook:
                        try {
                            Uri uri = Uri.parse(getResources().getString(R.string.FACEBOOK_LINK));
                            Intent fbi = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(fbi);

                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(getApplicationContext(), "No application can handle this request."
                                    + " Please install a webrowser", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        break;

                    case R.id.mail:

                        Intent ei = new Intent(Intent.ACTION_SENDTO);
                        ei.setData(Uri.parse("mailto:"));
                        ei.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.ENTER_YOUR_EMAIL_HARE)});
                        ei.putExtra(Intent.EXTRA_SUBJECT, "Report for: "+getResources().getString(R.string.app_name));
                        ei.putExtra(Intent.EXTRA_TEXT, "Dear "+getResources().getString(R.string.app_name)+" developer team, ");
                        startActivity(Intent.createChooser(ei, "send mail"));
                        break;

                    case R.id.save:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,getResources().getString(R.string.APP_SHARE_SMS) +
                                ": https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        break;


                    case R.id.retus:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+ BuildConfig.APPLICATION_ID)));
                        break;

                    case R.id.policy:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.PRIVACY_POLICY))));
                        break;

                }
                return true;
            }
        });
        Switch mySwitch = (Switch) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.menu_switch));
        mySwitch.setChecked(customPref.getSound());
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSound(R.raw.click);
                customPref.setSound(isChecked);
                if (customPref.getSound()){
                    mySwitch.setChecked(customPref.getSound());
                    Toast.makeText(getApplicationContext(), "Sound ON", Toast.LENGTH_SHORT).show();
                    mSound(R.raw.click);
                }else {
                    mySwitch.setChecked(customPref.getSound());
                    Toast.makeText(getApplicationContext(), "Sound OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });










    }


    public void replaceFragment(Fragment fragment, String titleTXT) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.mainFrame,fragment);
        fragmentTransaction.commit();
        title.setText(titleTXT);
    }



    @Override
    public void onBackPressed() {
        mSound(R.raw.click);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else if (!home){
            replaceFragment(new CategoryFragment(), "Category");
            home = true;
        }else {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Confirm Exit")
                    .setMessage("Are you sure want to close this app..?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mSound(R.raw.click);
                            finishAndRemoveTask();
                        }
                    })
                    .setNeutralButton("Rate Us", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mSound(R.raw.click);
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getPackageName())));
                            }catch (Exception e) {
                                Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mSound(R.raw.click);
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isAD){
            AdsMob.showAd(this, new AdsMob.OnDismiss() {
                @Override
                public void onDismiss() {
                    isAD = false;
                }
            });
        }
    }


}