package com.example.M501;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    public static  final String DATABASE_NAME = "Darts.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABEL_NAME = "player";
    public static final String TABEL_NAME2 = "games";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ID2 = "_id2";
    public static final String COLUMN_NAME = "_name";
    public static final String COLUMN_SPIELE = "_spiele";
    public static final String COLUMN_SIEGE = "_siege";
    public static final String COLUMN_SPIELER1 = "_player1";
    public static final String COLUMN_SPIELER2 = "_player2";
    public static final String COLUMN_LEGS1 = "_legs1";
    public static final String COLUMN_LEGS2 = "_legs2";
    public static final String COLUMN_AVG1 = "_avg1";
    public static final String COLUMN_AVG2 = "_avg2";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null,  DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABEL_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT, " +
                        COLUMN_SPIELE + " INTEGER, " +
                        COLUMN_SIEGE + " INTEGER);";
        String query2= "CREATE TABLE " + TABEL_NAME2 +
                " (" + COLUMN_ID2 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SPIELER1 + " TEXT, " +
                COLUMN_SPIELER2 + " TEXT, " +
                COLUMN_LEGS1 + " INTEGER, " +
                COLUMN_LEGS2 + " INTEGER, " +
                COLUMN_AVG1  + " FLOAT, " +
                COLUMN_AVG2  + " FLOAT);";
        db.execSQL(query);
        db.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    db.execSQL("DROP TABLE IF EXISTS " + TABEL_NAME);
    db.execSQL("DROP TABLE IF EXISTS " + TABEL_NAME2);
    onCreate(db);
    }

    void addPlayer(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_SIEGE, 0);
        cv.put(COLUMN_SPIELE, 0);
        long result = db.insert(TABEL_NAME, null, cv);
        if(result == -1){
            Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Spieler " + name +" wurde erfolgreich hinzugefügt", Toast.LENGTH_SHORT).show();
        }
    }

    void addGame(String name1, String name2, int legs1, int legs2, float avg1, float avg2){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SPIELER1, name1);
        cv.put(COLUMN_SPIELER2, name2);
        cv.put(COLUMN_LEGS1, legs1);
        cv.put(COLUMN_LEGS2, legs2);
        cv.put(COLUMN_AVG1, avg1);
        cv.put(COLUMN_AVG2, avg2);
        long result = db.insert(TABEL_NAME2, null, cv);
        if(result == -1){
            Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
        }else{
           // Toast.makeText(context, "Spiel wurde erfolgreich hinzugefügt", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readAllData(String name){
        String query = "SELECT * FROM " + name;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
           cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
}
