package com.moazzem.mehedidesign.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.moazzem.mehedidesign.BuildConfig;
import com.moazzem.mehedidesign.tools.CategoryClick;
import com.moazzem.mehedidesign.R;
import com.moazzem.mehedidesign.model.CategoryModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    ArrayList<CategoryModel> arrayList;

    Context context;
    CategoryClick categoryClick;


    public CategoryAdapter(ArrayList<CategoryModel> arrayList, Context context, CategoryClick categoryClick) {
        this.arrayList = arrayList;
        this.context = context;
        this.categoryClick = categoryClick;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        CategoryModel categoryModel = arrayList.get(position);
        holder.category_title.setText(categoryModel.getCategory_name());
        holder.category_logo.setImageResource(categoryModel.getCategory_image());
        holder.progressBar.setVisibility(View.GONE);
        holder.category_item.setOnClickListener(view -> categoryClick.onClick(categoryModel));

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView category_title;
        CardView category_item;
        ImageView category_logo;
        ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            category_title = itemView.findViewById(R.id.category_title);
            category_item = itemView.findViewById(R.id.category_item);
            category_logo = itemView.findViewById(R.id.category_logo);
            progressBar = itemView.findViewById(R.id.progress);




        }
    }

    public void filterList(ArrayList<CategoryModel> filteredList) {
        arrayList = filteredList;
        notifyDataSetChanged();
    }

}
