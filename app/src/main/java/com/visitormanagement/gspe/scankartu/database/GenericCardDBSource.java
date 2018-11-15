package com.visitormanagement.gspe.scankartu.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.visitormanagement.gspe.scankartu.model.GenericCard;

import java.util.ArrayList;
import java.util.List;

public class GenericCardDBSource {

    private SQLiteDatabase database;
    private GenericDBHelper databaseHelper;
    private Context context;

    private String[] allColumns = {
            GenericDBHelper.COLUMN_ID,
            GenericDBHelper.COLUMN_CARD_TYPE,
            GenericDBHelper.COLUMN_CONTENT,
            GenericDBHelper.COLUMN_IMAGE_PATH,
    };

    public GenericCardDBSource(Context context) {
        databaseHelper = new GenericDBHelper(context);
        this.context = context;
    }


    public void open() throws SQLiteException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        databaseHelper.close();
    }

    public void addCard(GenericCard card) {
        ContentValues values = new ContentValues();
        values.put(GenericDBHelper.COLUMN_CARD_TYPE, card.getType());
        values.put(GenericDBHelper.COLUMN_CONTENT, card.getContent());
        values.put(GenericDBHelper.COLUMN_IMAGE_PATH, card.getImagePath() );
        database.insert(GenericDBHelper.TABLE_GENERIC, null, values);
    }

    public List<GenericCard> getAllGenericCard() {
        List<GenericCard> genericCardList = new ArrayList<>();
        Cursor cursor = database.query(GenericDBHelper.TABLE_GENERIC, allColumns, null,
                null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            GenericCard genericCard = getFromCursor(cursor);
            genericCardList.add(genericCard);
            cursor.moveToNext();
        }
        cursor.close();

        return genericCardList;
    }

    private GenericCard getFromCursor(Cursor cursor) {
        GenericCard genericCard = new GenericCard();
        genericCard.setId(cursor.getInt(0));
        genericCard.setType(cursor.getString(1));
        genericCard.setContent(cursor.getString(2));
        genericCard.setImagePath(cursor.getString(3));
        return genericCard;
    }
}
