package com.moazzem.mehedidesign.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.moazzem.mehedidesign.BuildConfig;
import com.moazzem.mehedidesign.tools.AdsInitMOB;
import com.moazzem.mehedidesign.tools.AdsMob;
import com.moazzem.mehedidesign.tools.CustomPref;
import com.moazzem.mehedidesign.tools.WallpaperClick;
import com.moazzem.mehedidesign.R;
import com.moazzem.mehedidesign.adapter.WallpaperAdapter;
import com.moazzem.mehedidesign.model.WallpaperModel;
import com.moazzem.mehedidesign.tools.Retry;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

public class WallpaperActivity extends AdsInitMOB {

    Toolbar toolbar;
    RecyclerView recyclerview;
    WallpaperAdapter wallpaperAdapter;

    Intent targetedActivity;
    MediaPlayer mediaPlayer;
    CustomPref customPref;
    ArrayList<WallpaperModel> designArrayList;
    ProgressBar progressBar;
    String id;
    private void mSound(int sound) {
        if (customPref.getSound()){
            float volume = 0.1f;
            mediaPlayer = MediaPlayer.create(getApplicationContext(), sound);
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mediaPlayer -> mediaPlayer.release());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        MainActivity.isAD = true;
        progressBar = findViewById(R.id.progress);
        toolbar = findViewById(R.id.toolbar);
        customPref = new CustomPref(WallpaperActivity.this);
        setSupportActionBar(toolbar);
        setTitle(getIntent().getStringExtra("title"));
        id = getIntent().getStringExtra("id");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (CustomPref.isNetworkAvailable(this)){
            init();
        }else {
            progressBar.setVisibility(View.GONE);
            CustomPref.showError(this, "no internet connection..!", new Retry() {
                @Override
                public void OnRetry() {
                    init();
                }
            });
        }






    }

    private void init(){
        loadDesign();
        wallpaperAdapter = new WallpaperAdapter(id,WallpaperActivity.this, designArrayList, new WallpaperClick() {
            @Override
            public void onClick(WallpaperModel wallpaperModel, int position) {
                mSound(R.raw.click);
                WallpaperView.designListView = designArrayList;
                targetedActivity =new Intent(WallpaperActivity.this, WallpaperView.class);
                targetedActivity.putExtra("position", position);
                targetedActivity.putExtra("key", id);
                startActivity(targetedActivity);

            }
        }, false, null);
        recyclerview = findViewById(R.id.recyclerview);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerview.setLayoutManager(new GridLayoutManager(WallpaperActivity.this, 2));
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerview.setLayoutManager(new GridLayoutManager(WallpaperActivity.this, 4));
        }
        recyclerview.setHasFixedSize(true);
        recyclerview.setAdapter(wallpaperAdapter);
    }

    private void loadDesign() {
        progressBar.setVisibility(View.VISIBLE);
        designArrayList = new ArrayList<>();
        designArrayList.clear();





        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, BuildConfig.SERVER_URL+"?key="+id,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                WallpaperModel wallpaperModel = new WallpaperModel();
                                wallpaperModel.setId(response.getString(i));
                                wallpaperModel.setImage(response.getString(i));
                                designArrayList.add(wallpaperModel);
                            }

                            Collections.reverse(designArrayList);
                            wallpaperAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                            CustomPref.showError(WallpaperActivity.this, ""+e.getMessage(), new Retry() {
                                @Override
                                public void OnRetry() {
                                    init();
                                }
                            });
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CustomPref.showError(WallpaperActivity.this, ""+error.getMessage(), new Retry() {
                            @Override
                            public void OnRetry() {
                                init();
                            }
                        });                    }
                });

        Volley.newRequestQueue(this).add(jsonArrayRequest);


    }





    @Override
    public void onBackPressed() {
        mSound(R.raw.click);
        super.onBackPressed();
    }






}