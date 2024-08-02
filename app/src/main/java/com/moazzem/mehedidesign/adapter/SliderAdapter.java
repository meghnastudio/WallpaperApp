package com.moazzem.mehedidesign.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moazzem.mehedidesign.BuildConfig;
import com.moazzem.mehedidesign.tools.WallpaperClick;
import com.moazzem.mehedidesign.R;
import com.moazzem.mehedidesign.model.WallpaperModel;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


public class SliderAdapter extends
        SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;
    private List<WallpaperModel> mBooks;

    WallpaperClick bookClick;

    public SliderAdapter(Context context, List<WallpaperModel> mBooks, WallpaperClick bookClick) {
        this.context = context;
        this.mBooks = mBooks;
        this.bookClick = bookClick;
        notifyDataSetChanged();

    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imageslider, parent,false);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH holder, final int position) {
        WallpaperModel sliderItem = mBooks.get(position);
        Picasso.get().load(BuildConfig.SERVER_URL+"images/"+sliderItem.getImage()).into(holder.imageSlider, new Callback() {
            @Override
            public void onSuccess() {
                holder.progress.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                holder.progress.setVisibility(View.GONE);
            }
        });
        holder.itemView.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookClick.onClick(sliderItem, position);
            }
        });
    }

    @Override
    public int getCount() {
        return mBooks.size();
    }

    static class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        ImageView imageSlider;
        TextView book_title;
        TextView author_name;
        ProgressBar progress;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageSlider = itemView.findViewById(R.id.imageSlider);
            book_title = itemView.findViewById(R.id.book_title);
            author_name = itemView.findViewById(R.id.author_name);
            author_name = itemView.findViewById(R.id.author_name);
            progress = itemView.findViewById(R.id.progress);
        }
    }

}

