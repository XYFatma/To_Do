package com.example.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class TodoDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "todo_db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_TODO = "todos";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TASK = "task";
    public static final String COLUMN_URGENCY = "urgency";

    public TodoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE =
                "CREATE TABLE " + TABLE_TODO + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TASK + " TEXT, " +
                        COLUMN_URGENCY + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }

    public void insertTodo(String task, String urgency) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, task);
        values.put(COLUMN_URGENCY, urgency);
        db.insert(TABLE_TODO, null, values);
        db.close();
    }

    public Cursor getAllTodos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TODO, null);
    }

    public void deleteTodoById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAllTodos() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, null, null);
        db.close();
    }
}
