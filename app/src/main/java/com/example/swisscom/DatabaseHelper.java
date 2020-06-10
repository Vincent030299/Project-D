package com.example.swisscom;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
public class DatabaseHelper extends SQLiteAssetHelper  {
    private static final String DATABASE_NAME = "blocks2.db";
    private static final int dbVersion = 1;
    private final static String BLOCKS_TABLE = "blocks";
    private final static String PAIR_NUMBER = "pair";
    private final static String MAGNETIC_TABLE = "magnetic_mappings";
    private final static String TESLA = "tesla";
    private final static String ROW = "grid_row";
    private final static String COLUMN = "grid_column";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, dbVersion);
    }
    public Cursor getBlocks(){
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {PAIR_NUMBER};

        qb.setTables(BLOCKS_TABLE);
        Cursor data = qb.query(db, sqlSelect, null, null,
                null, null, null);

        data.moveToFirst();
        return data;
    }

    public Cursor getMappings(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {TESLA,ROW,COLUMN};

        qb.setTables(MAGNETIC_TABLE);
        Cursor data = qb.query(db, sqlSelect, null, null,
                null, null, null);

        data.moveToFirst();
        return data;
    }
}
