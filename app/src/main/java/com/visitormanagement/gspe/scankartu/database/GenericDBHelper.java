package com.visitormanagement.gspe.scankartu.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GenericDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_GENERIC = "genericCard";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CARD_TYPE = "cardtype";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_IMAGE_PATH = "imagePath";

    private static final String DATABASE_NAME = TABLE_GENERIC + ".db";
    private static final int DATABASE_VERSION = 1;

    public GenericDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "create table "
                + TABLE_GENERIC + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_CARD_TYPE + " text not null, "
                + COLUMN_CONTENT + " text not null, "
                + COLUMN_IMAGE_PATH + " text);";

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_GENERIC);
        onCreate(sqLiteDatabase);
    }
}
