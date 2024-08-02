package com.moazzem.mehedidesign;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionChecker {

    final String KEY_NAME = "bastobjibonerkoster";

    final String BASE_URL = "https://updater.loveitprofessional.com/";

    private static final String TAG = "VersionChecker";
    private Context context;

    public VersionChecker(Context context) {
        this.context = context;
    }

    public void checkForUpdate() {
        new FetchVersionTask().execute(BASE_URL+KEY_NAME+".json");
    }

    private class FetchVersionTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching version info", e);
                return null;
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(context, "Failed to check for updates", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                int latestVersionCode = jsonObject.getInt("versionCode");
                String latestVersionName = jsonObject.getString("versionName");
                boolean forceUpdate = jsonObject.getBoolean("forceUpdate");

                PackageManager pm = context.getPackageManager();
                PackageInfo pInfo = pm.getPackageInfo(context.getPackageName(), 0);
                int currentVersionCode = pInfo.versionCode;
                String currentVersionName = pInfo.versionName;

                if (currentVersionCode < latestVersionCode) {
                    showUpdateDialog(latestVersionName, forceUpdate);
                } else {
                    Toast.makeText(context, "App is up to date", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing version info", e);
                Toast.makeText(context, "Failed to check for updates", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showUpdateDialog(String latestVersionName, boolean forceUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Available");
        builder.setMessage("A new version (" + latestVersionName + ") is available. Do you want to update?");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+context.getPackageName())));
            }
        });

        if (!forceUpdate) {
            builder.setNegativeButton("Later", null);
        }

        builder.setCancelable(!forceUpdate);
        builder.show();
    }
}

