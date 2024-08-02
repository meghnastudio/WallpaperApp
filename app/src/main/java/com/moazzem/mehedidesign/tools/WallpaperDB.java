package com.moazzem.mehedidesign.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.moazzem.mehedidesign.model.WallpaperModel;

import java.util.ArrayList;

public class WallpaperDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "wallpaper.db";
    private static final String TABLE_WALLPAPER_NAME = "wallpaper";

    public static final String KEY_ID = "id";
    public static final String KEY_IMAGE = "url";

    public WallpaperDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TYPO_TABLE = "CREATE TABLE " + TABLE_WALLPAPER_NAME + "("
                + KEY_ID + " TEXT PRIMARY KEY,"
                + KEY_IMAGE + " TEXT"
                + ")";
        db.execSQL(CREATE_TYPO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLPAPER_NAME);
        onCreate(db);
    }

    public boolean isWallpaperExists(String storyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_WALLPAPER_NAME, new String[]{KEY_ID},
                    KEY_ID + "=?", new String[]{storyId}, null, null, null);

            return cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public void removeWallpaperByID(String _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_WALLPAPER_NAME, KEY_ID + " = ?", new String[]{_id});
        } finally {
            db.close();
        }
    }

    public long addWallpaper(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            return db.insert(TABLE_WALLPAPER_NAME, null, values);
        } finally {
            db.close();
        }
    }




    public ArrayList<WallpaperModel> getWallpaperList() {
        ArrayList<WallpaperModel> fevList = new ArrayList<>();
        String selectQuery = "SELECT *  FROM "
                + TABLE_WALLPAPER_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                WallpaperModel contact= new WallpaperModel();
                contact.setId(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)));
                contact.setImage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)));
                fevList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return fevList;
    }

}
