package com.example.reneewu.simpletodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by reneewu on 1/28/2017.
 */

public class TodoDatabaseHelper extends SQLiteOpenHelper {
    private static TodoDatabaseHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "todoItemsDatabase";
    private static final int DATABASE_VERSION = 3;

    // Table Names
    private static final String TABLE_TODOS = "todos";

    // TODO Table Columns
    private static final String KEY_TODO_ID = "id";
    private static final String KEY_TODO_TaskName = "taskName";
    private static final String KEY_TODO_DueDate = "dueDate";
    private static final String KEY_TODO_Pri = "pri";
    private static final String KEY_TODO_Status = "status";
    private static final String KEY_TODO_markAsDelete= "markAsDelete"; // 0 (false) and 1 (true)

    public static synchronized TodoDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TodoDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private TodoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODOS_TABLE = "CREATE TABLE " + TABLE_TODOS +
                "(" +
                KEY_TODO_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_TODO_TaskName + " TEXT," +
                KEY_TODO_Pri + " TEXT," +
                KEY_TODO_Status + " TEXT," +
                KEY_TODO_DueDate + " TEXT," +
                KEY_TODO_markAsDelete + " INTEGER DEFAULT 0" +
                ")";

        db.execSQL(CREATE_TODOS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOS);
            onCreate(db);
        }
    }

    public ArrayList<todoItem> getAllTodos() {
        ArrayList<todoItem> items = new ArrayList<>();

        String SELECT_QUERY =
                /*String.format("SELECT * FROM %s",
                        TABLE_TODOS);*/
                String.format("SELECT * FROM %s WHERE %s = %s",
                        TABLE_TODOS,
                        KEY_TODO_markAsDelete,
                        0);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    todoItem newTodoItem = new todoItem();
                    newTodoItem.id = cursor.getInt(cursor.getColumnIndex(KEY_TODO_ID));
                    newTodoItem.taskName = cursor.getString(cursor.getColumnIndex(KEY_TODO_TaskName));
                    newTodoItem.pri = cursor.getString(cursor.getColumnIndex(KEY_TODO_Pri));
                    newTodoItem.status = cursor.getString(cursor.getColumnIndex(KEY_TODO_Status));
                    newTodoItem.dueDate = cursor.getString(cursor.getColumnIndex(KEY_TODO_DueDate));

                    items.add(newTodoItem);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return items;
    }

    // Insert
    public todoItem addTodo(todoItem item) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TODO_TaskName, item.taskName);
            values.put(KEY_TODO_Pri, "high");
            values.put(KEY_TODO_Status, "todo");
            values.put(KEY_TODO_DueDate, "2017-01-31"); //todo: need to figure out how to store in datetime

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            long id = db.insertOrThrow(TABLE_TODOS, null, values);
            db.setTransactionSuccessful();
            item.id = (int)id;
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }

        return item;
    }

    // Update
    public void updateTodo(todoItem item) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TODO_TaskName, item.taskName);
            //values.put(KEY_TODO_Pri, item.pri);
            //values.put(KEY_TODO_Status, item.status);
            //values.put(KEY_TODO_DueDate, item.dueDate.toString()); //todo: need to figure out how to store in datetime

            // First try to update the items
            int rows = db.update(TABLE_TODOS, values, KEY_TODO_ID + "=" + item.id, null);

            // Check if update succeeded
            if (rows == 1) {
                db.setTransactionSuccessful();
            } else {
                Log.d(TAG, "Error while trying to update todo");
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
    }

    public void deleteTodo(todoItem item) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TODO_markAsDelete, 1/*TRUE*/);

            // First try to update the items
            int rows = db.update(TABLE_TODOS, values, KEY_TODO_ID + "=" + item.id, null);

            // Check if update succeeded
            if (rows == 1) {
                db.setTransactionSuccessful();
            } else {
                // do nothing
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
    }
}