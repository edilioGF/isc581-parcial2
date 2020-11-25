package com.example.isc581_parc2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private DatabaseHelper dbHelper;
    private Context context;
    protected SQLiteDatabase db;

    public DatabaseManager(Context context) {
        this.context = context;
        dbHelper = DatabaseHelper.getInstance(context);
        open();
    }

    public void open() throws SQLException {
        if (dbHelper == null) {
            dbHelper = DatabaseHelper.getInstance(context);
        }
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createProduct(String name, String price, String category) {
        open();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("category", category);
        cv.put("price", price);
        db.insert("PRODUCTS", null, cv);
    }

    public void createCategory(String name) {
        open();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        db.insert("CATEGORIES", null, cv);
    }

    public Cursor getProducts() {
        open();
        String[] columns = new String[] { "_id", "name", "category", "price" };
        Cursor cursor = db.query("PRODUCTS", columns, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public List<String> getCategories(){
        open();
        List<String> list = new ArrayList<String>();

        String selectQuery = "SELECT * FROM CATEGORIES";

        dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return list;
    }

    public int updateProducts(long _id, String name, String price, String category) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("category", category);
        cv.put("price", price);
        return db.update("PRODUCTS", cv, "_id = " + _id, null);
    }

    public void removeProduct(long _id) {
        db.delete("PRODUCTS",  "_id= " + _id, null);
    }
}
