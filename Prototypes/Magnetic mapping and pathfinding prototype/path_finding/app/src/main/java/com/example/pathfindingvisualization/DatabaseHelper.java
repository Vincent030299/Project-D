package com.example.pathfindingvisualization;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final static String BLOCKS_TABLE = "blocks";
    private final static String PAIR_NUMBER = "pair";
    private final static String MAGNETIC_TABLE = "magnetic_mappings";
    private final static String TESLA = "tesla";
    private final static String ROW = "grid_row";
    private final static String COLUMN = "grid_column";
    public DatabaseHelper(@Nullable Context context) {
        super(context, BLOCKS_TABLE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String blocksTableQuery = "CREATE TABLE " + BLOCKS_TABLE
                +  "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PAIR_NUMBER + " TEXT)";
        String mappingsTableQuery =  "CREATE TABLE " + MAGNETIC_TABLE
                +  "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " + ROW + " INTEGER, " + COLUMN + " INTEGER, "
                + TESLA + " TEXT)";
        db.execSQL(blocksTableQuery);
        db.execSQL(mappingsTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + BLOCKS_TABLE);
        db.execSQL("DROP IF TABLE EXISTS " + MAGNETIC_TABLE);
        onCreate(db);
    }
    public boolean addBlocks(ArrayList<Double> uniqueNum){
        SQLiteDatabase db = this.getWritableDatabase();
        long data = -1;
        db.delete(BLOCKS_TABLE, null, null);
        for (Double uNum:
             uniqueNum) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PAIR_NUMBER,String.valueOf(uNum));
            data = db.insert(BLOCKS_TABLE,null,contentValues);
        }

        if(data == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean addMagneticMapping(HashMap<Long,int[]> mappings){
        SQLiteDatabase db = this.getWritableDatabase();
        long state = -1;
        db.delete(MAGNETIC_TABLE, null, null);
        for (Map.Entry<Long,int[]> pair: mappings.entrySet()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TESLA,String.valueOf(pair.getKey()));
            contentValues.put(ROW,pair.getValue()[0]);
            contentValues.put(COLUMN,pair.getValue()[1]);
            state = db.insert(MAGNETIC_TABLE,null,contentValues);
        }

        if(state == -1){
            return false;
        }
        else{
            return true;
        }
    }
}
