package com.moazzem.mehedidesign.model;

public class CategoryModel {
    String catId, category_name;


    int category_image;

    public CategoryModel(String catId, String category_name, int category_image) {
        this.catId = catId;
        this.category_image = category_image;
        this.category_name = category_name;
    }

    public String getCatId() {
        return catId;
    }

    public String getCategory_name() {
        return category_name;
    }


    public int getCategory_image() {
        return category_image;
    }

}

