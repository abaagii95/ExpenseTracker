package com.example.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBAdapter {

    private static final String TABLE_CATEGORIES = "Categories";
    private static final String TABLE_EXPENSES = "Expenses";

    public static final String CATEGORIES_KEY_ROWID = "_id";
    public static final String CATEGORIES_KEY_NAME = "category_name";
    /*
    public static final String TAGS_KEY_ROWID = "_id";
    public static final String TAGS_KEY_NAME = "tag_name";
    public static final String TAGS_KEY_CATEGORYID = "category_id";
    */
    public static final String EXPENSES_KEY_ROWID = "_id";
    public static final String EXPENSES_KEY_NAME = "expense_name";
    public static final String EXPENSES_KEY_TAGID = "tag_id";
    public static final String EXPENSES_KEY_AMOUNT = "amount";
    public static final String EXPENSES_KEY_DESCRIPTION = "description";
    public static final String EXPENSES_KEY_DATE = "date";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        context = ctx;
        DBHelper = new DatabaseHelper(context);
    }



    public DBAdapter open() throws SQLException{
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    public long insertCategory(String category) {
        ContentValues values = new ContentValues();
        values.put(CATEGORIES_KEY_NAME, category);
        return db.insert(TABLE_CATEGORIES, null, values);
    }

    public Cursor getCategories() {
        Cursor cursor = db.query(true, TABLE_CATEGORIES, new String[] {CATEGORIES_KEY_ROWID, CATEGORIES_KEY_NAME},
                null, null, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public long updateCategory(int catId, String newCategory) {
        ContentValues values = new ContentValues();
        values.put(CATEGORIES_KEY_NAME, newCategory);
        return db.update(TABLE_CATEGORIES, values, CATEGORIES_KEY_ROWID + "=" + catId + "", null);
    }

    public long insertExpense(ContentValues cv) {
        return db.insert(TABLE_EXPENSES, null, cv);
    }

    public Cursor getExpenses() {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EXPENSES + " ORDER BY " + EXPENSES_KEY_DATE + " DESC", null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getExpense(int id) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EXPENSES + " WHERE " + EXPENSES_KEY_ROWID + "=" + id, null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int deleteExpense(int id) {
        return db.delete(TABLE_EXPENSES, EXPENSES_KEY_ROWID + "=" + id, null);
    }

    public long updateExpense(int id, ContentValues cv) {
        return db.update(TABLE_EXPENSES, cv, CATEGORIES_KEY_ROWID + "=" + id + "", null);
    }

    public Cursor getExpenses(int id) {
        return db.rawQuery("SELECT * FROM " + TABLE_EXPENSES + " WHERE " + EXPENSES_KEY_TAGID + "=" + id, null);
    }

    public Cursor getTopCatExpenses() {
        return db.rawQuery("SELECT c.category_name, SUM(e.amount) AS sumexp, c._id FROM Expenses e, Categories c WHERE e.tag_id=c._id GROUP BY e.tag_id " +
                "ORDER BY sumexp desc LIMIT 5", null);
    }

    public Cursor getSumExpenses() {
        return db.rawQuery("SELECT sum(" + EXPENSES_KEY_AMOUNT + ") AS sumexp FROM " + TABLE_EXPENSES, null);
    }

}