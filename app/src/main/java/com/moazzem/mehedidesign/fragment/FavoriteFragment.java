package com.moazzem.mehedidesign.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moazzem.mehedidesign.R;
import com.moazzem.mehedidesign.activity.WallpaperView;
import com.moazzem.mehedidesign.adapter.WallpaperAdapter;
import com.moazzem.mehedidesign.model.WallpaperModel;
import com.moazzem.mehedidesign.tools.CustomPref;
import com.moazzem.mehedidesign.tools.WallpaperClick;
import com.moazzem.mehedidesign.tools.WallpaperDB;

import java.util.ArrayList;


public class FavoriteFragment extends Fragment {
    View view;


    ArrayList<WallpaperModel> likedStatusList;
    RecyclerView recyclerview;
    WallpaperAdapter wallpaperAdapter;
    CustomPref customPref;
    MediaPlayer mediaPlayer;
    WallpaperDB wallpaperDB;
    Intent targetedActivity;

    private void mSound(int sound) {
        if (customPref.getSound()){
            float volume = 0.1f; // 50% volume
            mediaPlayer = MediaPlayer.create(getContext(), sound);
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mediaPlayer -> mediaPlayer.release());
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite, container, false);
        customPref = new CustomPref(getContext());
        wallpaperDB = new WallpaperDB(getContext());
        likedStatusList = wallpaperDB.getWallpaperList();
        TextView nothing = view.findViewById(R.id.nothing);
        if (likedStatusList.size()<1){
            nothing.setVisibility(View.VISIBLE);
        }else nothing.setVisibility(View.GONE);

        wallpaperAdapter = new WallpaperAdapter("",getContext(), wallpaperDB.getWallpaperList(), new WallpaperClick() {
            @Override
            public void onClick(WallpaperModel wallpaperModel, int position) {
                mSound(R.raw.click);
                WallpaperView.designListView = likedStatusList;
                targetedActivity = new Intent(getContext(), WallpaperView.class);
                targetedActivity.putExtra("position", position);
                startActivity(targetedActivity);

            }
        }, true, new WallpaperAdapter.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        recyclerview = view.findViewById(R.id.recyclerview);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerview.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerview.setLayoutManager(new GridLayoutManager(getContext(), 4));
        }
        recyclerview.setHasFixedSize(true);
        recyclerview.setAdapter(wallpaperAdapter);


        return view;
    }

}