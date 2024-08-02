package com.moazzem.mehedidesign.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomPref {
    Context context;
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void showError(Context context,String msg, Retry retry){
       new AlertDialog.Builder(context)
               .setTitle("Something went wrong..")
               .setMessage(msg)
               .setCancelable(false)
               .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       dialogInterface.dismiss();
                   }
               })
               .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       if (isNetworkAvailable(context)){
                           retry.OnRetry();
                           dialogInterface.dismiss();
                       }else {
                           Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                           context.startActivity(intent);
                           retry.OnRetry();
                       }
                   }
               }).show();
    }

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public CustomPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    public Boolean getSound() {
        return sharedPreferences.getBoolean("sound", true);
    }

    public void setSound(Boolean setSound) {
        editor.putBoolean("sound", setSound);
        editor.apply();
    }




}
