package com.example.isc581_parc2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "ISC581-PARC2.DB";
    public static final int DB_VERSION = 1;

    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE PRODUCTS (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL, " +
            "category TEXT NOT NULL, " +
            "price TEXT NOT NULL)";

    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE CATEGORIES (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL)";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context c) {
        if (instance == null) {
            instance = new DatabaseHelper(c);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS PRODUCTS");
        db.execSQL("DROP TABLE IF EXISTS CATEGORIES");
        onCreate(db);
    }
}
