package com.moazzem.mehedidesign.fragment;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.moazzem.mehedidesign.BuildConfig;
import com.moazzem.mehedidesign.tools.CustomPref;
import com.moazzem.mehedidesign.R;
import com.moazzem.mehedidesign.tools.WallpaperDB;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ImageFragment extends Fragment {
    private static final String ARG_IMAGE_RES_ID = "image_res_id";

    public static ImageFragment newInstance(String imageLink, String id) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_RES_ID, imageLink);
        args.putString("id", id);
        fragment.setArguments(args);
        return fragment;
    }
    private int selectedWallpaperFlags = 0;
    ProgressBar progressBar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        ImageView imageView = view.findViewById(R.id.photo_view);
        WallpaperDB wallpaperDB = new WallpaperDB(getContext());
        ContentValues contentValues = new ContentValues();
        progressBar = view.findViewById(R.id.progress);
        Picasso.get().load(BuildConfig.SERVER_URL+getArguments().getString(ARG_IMAGE_RES_ID)).into(imageView,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
        ImageView fev = view.findViewById(R.id.fav);
        ImageView btnShare = view.findViewById(R.id.btnShare);
        ImageView save = view.findViewById(R.id.save);
        if (wallpaperDB.isWallpaperExists(getArguments().getString("id"))){
            fev.setImageResource(R.drawable.ic_liked);
        }else {
            fev.setImageResource(R.drawable.ic_like);
        }

        fev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wallpaperDB.isWallpaperExists(getArguments().getString("id"))){
                    wallpaperDB.removeWallpaperByID(getArguments().getString("id"));
                    fev.setImageResource(R.drawable.ic_like);
                    Toast.makeText(getContext(), "Remove from Favorite", Toast.LENGTH_SHORT).show();
                }else {
                    contentValues.put(WallpaperDB.KEY_ID, getArguments().getString("id"));
                    contentValues.put(WallpaperDB.KEY_IMAGE, getArguments().getString(ARG_IMAGE_RES_ID));
                    wallpaperDB.addWallpaper(contentValues);
                    fev.setImageResource(R.drawable.ic_liked);
                    Toast.makeText(getContext(), "Added To Favorite", Toast.LENGTH_SHORT).show();
                }
                mSound(R.raw.click);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSound(R.raw.click);
                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                imageView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                Bitmap bitmap = imageView.getDrawingCache();

                String fileName = getArguments().getString(ARG_IMAGE_RES_ID)+".jpg";
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                try {
                    OutputStream outputStream = getActivity().getContentResolver().openOutputStream(uri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                    Toast.makeText(getContext(), "Image saved to gallery", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Error saving image", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }



            }
        });


        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadImageTask().execute(getArguments().getString(ARG_IMAGE_RES_ID));

            }
        });





        return view;
    }


    MediaPlayer mediaPlayer;
    CustomPref customPref;

    private void mSound(int sound) {
        customPref = new CustomPref(getContext());
        if (customPref.getSound()){
            float volume = 0.1f;
            mediaPlayer = MediaPlayer.create(getContext(), sound);
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mediaPlayer -> mediaPlayer.release());
        }
    }






    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = BuildConfig.SERVER_URL+urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(imageUrl).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                shareImage(result);
            } else {
                Toast.makeText(getContext(), "Failed to download image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void shareImage(Bitmap bitmap) {
        try {
            File cachePath = new File(getContext().getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/"+getStamp()+".jpg");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            File imagePath = new File(getContext().getCacheDir(), "images");
            File newFile = new File(imagePath, getStamp()+".jpg");
            Uri contentUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName()+".fileprovider", newFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getStamp(){
        // Get current timestamp
        long currentTimeMillis = System.currentTimeMillis();

        // Convert timestamp to Date object
        Date currentDate = new Date(currentTimeMillis);

        // Define format for time, date, and seconds
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss_yyyy-MM-dd_ss", Locale.getDefault());

        // Format the date according to the defined format
        String formattedTimestamp = sdf.format(currentDate);

        return formattedTimestamp;
    }




}
