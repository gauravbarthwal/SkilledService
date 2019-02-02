package com.servicenow.skilledserviceapp.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.Settings;

import com.servicenow.skilledserviceapp.utils.Constants;

public class DatabaseManager {
    private DatabaseHelper mDatabaseHelper;
    private Context mContext;
    private SQLiteDatabase mSqLiteDatabase;

    public DatabaseManager(Context mContext) {
        this.mContext = mContext;
    }

    public DatabaseManager openDatabase() throws SQLException {
        mDatabaseHelper = new DatabaseHelper(mContext);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        return this;
    }

    public void closeDatabase() {
        if (mDatabaseHelper != null) {
            mDatabaseHelper.close();
        }
    }

    public boolean insertUserData(String userName, String name, int skillId, String userType) {
        long insertedRow;
        String userId = generateUserId(userName);
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.COLUMN_USER_ID, userId);
        contentValue.put(DatabaseHelper.COLUMN_USER_NAME, userName);
        contentValue.put(DatabaseHelper.COLUMN_USER_TYPE, userType);
        insertedRow = mSqLiteDatabase.insert(DatabaseHelper.TABLE_USER, null, contentValue);

        if (insertedRow > -1) {
            if (userType.equalsIgnoreCase(Constants.USER_REQUESTER)) {
                ContentValues requesterContentValues = new ContentValues();
                requesterContentValues.put(DatabaseHelper.COLUMN_REQUESTER_ID, userId);
                requesterContentValues.put(DatabaseHelper.COLUMN_NAME, name);
                insertedRow = mSqLiteDatabase.insert(DatabaseHelper.TABLE_REQUESTER, null, contentValue);
            } else {
                ContentValues workerContentValues = new ContentValues();
                workerContentValues.put(DatabaseHelper.COLUMN_WORKER_ID, userId);
                workerContentValues.put(DatabaseHelper.COLUMN_NAME, name);
                workerContentValues.put(DatabaseHelper.COLUMN_SKILL_ID, skillId);
                insertedRow = mSqLiteDatabase.insert(DatabaseHelper.TABLE_WORKER, null, contentValue);
            }
        }
        return insertedRow > -1;
    }

    public Cursor getUserName(String userName) {
        String[] columns = new String[]{DatabaseHelper.COLUMN_USER_NAME};
        Cursor mCursor = mSqLiteDatabase.query(DatabaseHelper.TABLE_USER, columns, null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * generates unique user id
     * @return - user id
     */
    @SuppressLint("HardwareIds")
    private String generateUserId(String userName) {
        try {
            if (Build.SERIAL == null || Build.SERIAL.length() == 0
                    || Build.SERIAL.equalsIgnoreCase("unknown"))
                return Settings.Secure.getString(mContext.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            return Build.SERIAL;
        } catch (Exception e) {
            return userName + System.currentTimeMillis();
        }
    }
}
