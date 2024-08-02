package com.moazzem.mehedidesign.tools;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PushNotificationManager {

    public static final int PERMISSION_REQUEST_CODE = 1001;
    private final Activity activity;

    public PushNotificationManager(Activity activity) {
        this.activity = activity;
    }

    public void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For Android 8.0 (API level 26) and above, you need to create a notification channel.
            // This code demonstrates permission checks only, not channel creation.
            // See Android documentation for creating notification channels.
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, you can proceed with push notifications.
        } else {
            // Permission not granted, request it.
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_REQUEST_CODE
            );
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with push notifications.
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user).
            }
        }
    }
}

