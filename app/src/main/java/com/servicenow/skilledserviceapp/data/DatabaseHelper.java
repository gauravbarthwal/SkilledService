package com.servicenow.skilledserviceapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // database name
    private static final String DB_NAME = "SkilledServiceDB";

    // database version
    private static int DB_VERSION = 1;

    // table name
    static final String TABLE_USER = "tbl_user";
    static final String TABLE_REQUESTER = "tbl_requester";
    static final String TABLE_WORKER = "tbl_worker";
    static final String TABLE_TASK = "tbl_task";
    static final String TABLE_SKILL = "tbl_skill";

    // columns
    static final String COLUMN_USER_ID = "user_id";
    static final String COLUMN_USER_NAME = "user_name";
    static final String COLUMN_USER_TYPE = "user_type";

    static final String COLUMN_REQUESTER_ID = "requester_id";

    static final String COLUMN_WORKER_ID = "worker_id";
    static final String COLUMN_WORKER_SKILL_ID = "skill_id";

    static final String COLUMN_TASK_ID = "task_id";
    static final String COLUMN_TASK_DESCRIPTION = "task_description";
    static final String COLUMN_TASK_SKILL = "task_skill";
    static final String COLUMN_TASK_FROM = "task_from";
    static final String COLUMN_TASK_TO = "task_to";
    static final String COLUMN_TASK_STATUS = "task_status";
    static final String COLUMN_TASK_RATING = "task_rating";

    static final String COLUMN_SKILL_ID = "skill_id";
    static final String COLUMN_SKILL_TYPE = "skill_type";

    static final String COLUMN_NAME = "name";
    static final String COLUMN_RATING = "rating";
    static final String COLUMN_REVIEW = "review";

    // create table query
    private static final String SQL_CREATE_USER_TABLE = "create table " + TABLE_USER
            + "(" + COLUMN_USER_ID + " TEXT PRIMARY KEY, " + COLUMN_USER_NAME + " TEXT NOT NULL UNIQUE, "
            + COLUMN_USER_TYPE + " TEXT NOT NULL);";

    private static final String SQL_CREATE_REQUESTER_TABLE = "create table " + TABLE_REQUESTER
            + "(" + COLUMN_REQUESTER_ID + " TEXT PRIMARY KEY, " + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_RATING + " REAL);";

    private static final String SQL_CREATE_WORKER_TABLE = "create table " + TABLE_WORKER
            + "(" + COLUMN_WORKER_ID + " TEXT PRIMARY KEY, " + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_SKILL_ID + " INTEGER, " + COLUMN_RATING + " REAL);";

    private static final String SQL_CREATE_TASK_TABLE = "create table " + TABLE_TASK
            + "(" + COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TASK_DESCRIPTION + " TEXT NOT NULL, "
            + COLUMN_TASK_SKILL + " INTEGER NOT NULL, " + COLUMN_TASK_FROM + " TEXT NOT NULL, "
            + COLUMN_TASK_TO + " TEXT NOT NULL," + COLUMN_TASK_STATUS + " TEXT NOT NULL," + COLUMN_TASK_RATING + " REAL);";

    private static final String SQL_CREATE_SKILL_TABLE = "create table " + TABLE_SKILL
            + "(" + COLUMN_SKILL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_SKILL_TYPE + " TEXT NOT NULL);";


    public DatabaseHelper(Context mContext) {
        super(mContext, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_REQUESTER_TABLE);
        db.execSQL(SQL_CREATE_WORKER_TABLE);
        db.execSQL(SQL_CREATE_TASK_TABLE);
        db.execSQL(SQL_CREATE_SKILL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUESTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SKILL);
        onCreate(db);
    }
}