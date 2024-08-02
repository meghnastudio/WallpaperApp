package com.moazzem.mehedidesign.fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.moazzem.mehedidesign.R;
import com.moazzem.mehedidesign.activity.WallpaperActivity;
import com.moazzem.mehedidesign.adapter.CategoryAdapter;
import com.moazzem.mehedidesign.model.CategoryModel;
import com.moazzem.mehedidesign.tools.CategoryClick;
import com.moazzem.mehedidesign.tools.CustomPref;
import com.moazzem.mehedidesign.tools.Retry;
import java.util.ArrayList;


public class CategoryFragment extends Fragment {
    View view;

    CustomPref customPref;
    MediaPlayer mediaPlayer;
    RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;
    ArrayList<CategoryModel> categoryList;
    ProgressBar progressBar;
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
        view = inflater.inflate(R.layout.fragment_category, container, false);
        customPref = new CustomPref(getContext());
        progressBar = view.findViewById(R.id.progress);
        if (CustomPref.isNetworkAvailable(getContext())){
            init();
        }else {
            CustomPref.showError(getContext(), "no internet connection..!", new Retry() {
                @Override
                public void OnRetry() {
                    init();
                }
            });
        }
        return view;
    }


    private void init(){
        recyclerView = view.findViewById(R.id.recyclerView);
        loadCategory();
        categoryAdapter = new CategoryAdapter(categoryList, getContext(), new CategoryClick() {
            @Override
            public void onClick(CategoryModel categoryModel) {
                mSound(R.raw.click);
                Intent targetedActivity = new Intent(getContext(), WallpaperActivity.class);
                targetedActivity.putExtra("title", categoryModel.getCategory_name());
                targetedActivity.putExtra("id", categoryModel.getCatId());
                startActivity(targetedActivity);


            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(categoryAdapter);
    }


    private void loadCategory() {
        progressBar.setVisibility(View.VISIBLE);
        categoryList = new ArrayList<>();
        categoryList.clear();
        categoryList.add(new CategoryModel("cat_1", "Category One", R.drawable.ic_category));
        categoryList.add(new CategoryModel("cat_2", "Category Two", R.drawable.ic_category));
        categoryList.add(new CategoryModel("cat_3", "Category Three", R.drawable.ic_category));
        categoryList.add(new CategoryModel("cat_4", "Category Four", R.drawable.ic_category));
        categoryList.add(new CategoryModel("cat_5", "Category Five", R.drawable.ic_category));
        categoryList.add(new CategoryModel("cat_6", "Category Six", R.drawable.ic_category));
        //categoryAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }


}