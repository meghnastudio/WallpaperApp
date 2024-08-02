package com.moazzem.mehedidesign.tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.moazzem.mehedidesign.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

    private Context context;
    private String imageName;
    boolean isShare;

    MediaPlayer mediaPlayer;

    private void click(int sound) {
            /*float volume = 0.1f; // 50% volume
            mediaPlayer = MediaPlayer.create(context, sound);
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mediaPlayer -> mediaPlayer.release());*/

    }

    public ImageDownloader(Context context, String imageName, boolean isShare) {
        this.context = context;
        this.imageName = imageName;
        this.isShare = isShare;
        click(R.raw.click);

    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String imageUrl = params[0];
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            if (isShare){
                shareImage(result, imageName);
            } else {
                saveImageToGallery(result, imageName);
            }
        } else {
            Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToGallery(Bitmap bitmap, String imageName) {
        try {
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File myDir = new File(root, context.getResources().getString(R.string.app_name));

            if (!myDir.exists()) {
                myDir.mkdirs();
            }

            File file = new File(myDir, imageName + ".jpg");

            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
            }

            // Tell the media scanner about the new file so that it is immediately available to the user.
            MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null, null);

            Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save image to gallery", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareImage(Bitmap bitmap, String imageName) {
        try {
            // Save the image to a file
            File file = new File(context.getExternalCacheDir(), imageName + ".png");
            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
            }

            // Create an intent to share the image
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Check if there are apps that can handle the intent
            if (shareIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
            } else {
                Toast.makeText(context, "No app can handle this action", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to share image", Toast.LENGTH_SHORT).show();
        }
    }
}

