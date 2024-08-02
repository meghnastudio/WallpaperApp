package com.moazzem.mehedidesign.adapter;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moazzem.mehedidesign.BuildConfig;
import com.moazzem.mehedidesign.activity.MainActivity;
import com.moazzem.mehedidesign.tools.WallpaperClick;
import com.moazzem.mehedidesign.R;
import com.moazzem.mehedidesign.model.WallpaperModel;
import com.moazzem.mehedidesign.tools.WallpaperDB;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.ViewHolder>{
    Context context;
    ArrayList<WallpaperModel> designArrayList;
    WallpaperClick wallpaperClick;
    Boolean liked;
    OnRefreshListener onRefreshListener;

    String key;

    public WallpaperAdapter(String key,Context context, ArrayList<WallpaperModel> designArrayList, WallpaperClick wallpaperClick, Boolean liked, OnRefreshListener onRefreshListener) {
        this.key = key;
        this.context = context;
        this.designArrayList = designArrayList;
        this.wallpaperClick = wallpaperClick;
        this.liked = liked;
        this.onRefreshListener = onRefreshListener;
    }

    @NonNull
    @Override
    public WallpaperAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wallpaper, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WallpaperModel design = designArrayList.get(position);
        WallpaperDB wallpaperDB = new WallpaperDB(context);
        ContentValues wallpaper = new ContentValues();
        if (wallpaperDB.isWallpaperExists(design.getId())){
            holder.like.setImageResource(R.drawable.ic_liked);
        }else {
            holder.like.setImageResource(R.drawable.ic_like);
        }

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wallpaperDB.isWallpaperExists(design.getId())){
                    if (liked){
                        designArrayList.remove(position);
                        notifyItemRemoved(position);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra("isFev", true);
                                context.startActivity(intent);
                            }
                        },500);
                    }
                    wallpaperDB.removeWallpaperByID(design.getId());
                    holder.like.setImageResource(R.drawable.ic_like);
                    Toast.makeText(context, "Removed from Favorite", Toast.LENGTH_SHORT).show();
                }else {
                    wallpaper.put(WallpaperDB.KEY_ID, design.getId());
                    wallpaper.put(WallpaperDB.KEY_IMAGE, key+"/"+design.getImage());
                    wallpaperDB.addWallpaper(wallpaper);
                    holder.like.setImageResource(R.drawable.ic_liked);
                    Toast.makeText(context, "Added To Favorite", Toast.LENGTH_SHORT).show();
                }

            }
        });
        Picasso.get().load(BuildConfig.SERVER_URL+key+"/"+design.getImage()).into(holder.design_image,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                });
        holder.design_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wallpaperClick.onClick(design,position);
            }
        });








    }

    @Override
    public int getItemCount() {
        return designArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView like;
        ImageView design_image;
        ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            like = itemView.findViewById(R.id.btn_like);
            design_image = itemView.findViewById(R.id.design_image);
            progressBar = itemView.findViewById(R.id.progress);
        }
    }

    public interface OnRefreshListener {
        void onRefresh();
    }
}

