package com.example.expensetracker;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by bat-erdene on 12/26/15.
 */
class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseTrackerDB";
    private static final String TABLE_CATEGORIES = "Categories";
    private static final String TABLE_TAGS = "Tags";
    private static final String TABLE_EXPENSES = "Expenses";
    private static final int DATABASE_VERSION = 2;
    private static final String TAG = "ET DBAdapter";

    public static final String CATEGORIES_KEY_ROWID = "_id";
    public static final String CATEGORIES_KEY_NAME = "category_name";

    public static final String TAGS_KEY_ROWID = "_id";
    public static final String TAGS_KEY_NAME = "tag_name";
    public static final String TAGS_KEY_CATEGORYID = "category_id";

    public static final String EXPENSES_KEY_ROWID = "_id";
    public static final String EXPENSES_KEY_NAME = "expense_name";
    public static final String EXPENSES_KEY_TAGID = "tag_id";
    public static final String EXPENSES_KEY_AMOUNT = "amount";
    public static final String EXPENSES_KEY_DESCRIPTION = "description";
    public static final String EXPENSES_KEY_DATE = "date";

    private static final String CREATE_TABLE_EXPENSES =
            "create table if not exists " + TABLE_EXPENSES + " ("
                    + EXPENSES_KEY_ROWID +" integer primary key autoincrement, "
                    + EXPENSES_KEY_NAME + " varchar(100) null, "
                    + EXPENSES_KEY_AMOUNT + " integer, "
                    + EXPENSES_KEY_DATE + " date, "
                    + EXPENSES_KEY_DESCRIPTION + " varchar(100) null,"
                    + EXPENSES_KEY_TAGID + " integer"
                    + ");";

    private static final String CREATE_TABLE_TAGS =
            "create table if not exists " + TABLE_TAGS + " ("
                    + TAGS_KEY_ROWID +" integer primary key autoincrement, "
                    + TAGS_KEY_CATEGORYID + " integer, "
                    + TAGS_KEY_NAME + " varchar(100) null "
                    + ");";

    private static final String CREATE_TABLE_CATEGORIES =
            "create table if not exists " + TABLE_CATEGORIES + " ("
                    + CATEGORIES_KEY_ROWID +" integer primary key autoincrement, "
                    + CATEGORIES_KEY_NAME + " varchar(100) null "
                    + ");";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_CATEGORIES);
            db.execSQL(CREATE_TABLE_TAGS);
            db.execSQL(CREATE_TABLE_EXPENSES);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS);
        onCreate(db);
    }
}