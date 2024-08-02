package com.moazzem.mehedidesign.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import android.media.MediaPlayer;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;
import com.moazzem.mehedidesign.tools.AdsInitMOB;
import com.moazzem.mehedidesign.tools.AdsMob;
import com.moazzem.mehedidesign.tools.CustomPref;
import com.moazzem.mehedidesign.fragment.ImageFragment;
import com.moazzem.mehedidesign.R;
import com.moazzem.mehedidesign.model.WallpaperModel;


import java.util.ArrayList;

public class WallpaperView extends AdsInitMOB {

    public static ArrayList<WallpaperModel> designListView = new ArrayList<>();
    private ViewPager viewPager;
    private ImagePagerAdapter adapter;
    int position1;
    String key;



    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallpaper_view);
        position1 = getIntent().getIntExtra("position", 0);
        key = getIntent().getStringExtra("key");
        AdsMob.showAd(this, new AdsMob.OnDismiss() {
            @Override
            public void onDismiss() {

            }
        });
        // Set up the ViewPager and adapter
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        adapter = new ImagePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set up the indicator view (TabLayout)
        TabLayout tabLayout = findViewById(R.id.tabLayout);


        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager, true);
        }
        tabLayout.getTabAt(position1).select();

        // Set up a listener to detect when the user has swiped left or right
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // Update the indicator view if necessary
                if (tabLayout != null) {
                    mSound(R.raw.click);
                    tabLayout.getTabAt(position).select();
                }
            }
        });


    }

    public class ImagePagerAdapter extends FragmentPagerAdapter {

        public ImagePagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            WallpaperModel design = designListView.get(position);
            return ImageFragment.newInstance(key+"/"+design.getImage(), design.getId());
        }

        @Override
        public int getCount() {
            return designListView.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Optional: Set the title of each page in the indicator view (TabLayout)
            return "Design No " + (position + 1);
        }
    }

    MediaPlayer mediaPlayer;
    CustomPref customPref;

    private void mSound(int sound) {
        customPref = new CustomPref(getApplicationContext());
        if (customPref.getSound()){
            float volume = 0.1f;
            mediaPlayer = MediaPlayer.create(getApplicationContext(), sound);
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mediaPlayer -> mediaPlayer.release());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSound(R.raw.click);
    }






}