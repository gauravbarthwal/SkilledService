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
import com.servicenow.skilledserviceapp.utils.LogUtils;
import com.servicenow.skilledserviceapp.utils.PreferenceUtils;

public class DatabaseManager {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
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

    /**
     * insert skills to table
     */
    public void insertSkillsToDatabase() {
        try {
            final String[] skills = new String[]{"Plumbing","Electrician","Painter","Carpenter","Home Cleaning","Beautician", "Moving Homes", "Physiotherapist"};
            mSqLiteDatabase.execSQL("delete from "+ DatabaseHelper.TABLE_SKILL);
            ContentValues contentValue = new ContentValues();
            for (String skill : skills) {
                contentValue.put(DatabaseHelper.COLUMN_SKILL_TYPE, skill);
                mSqLiteDatabase.insert(DatabaseHelper.TABLE_SKILL, null, contentValue);
            }
        } catch (SQLException ignored) {
        }
    }

    /**
     * insertUserData to DatabaseHelper.TABLE_USER and DatabaseHelper.TABLE_REQUESTER or DatabaseHelper.TABLE_WORKER
     * @param userName - Unique username
     * @param name - User's full name
     * @param userType - Requester/Worker
     * @return - UserId
     */
    public String insertUserData(String userName, String name, String password, String userType) {
        try {
            long insertedRow;
            String userId = generateUserId(userName);
            ContentValues contentValue = new ContentValues();
            contentValue.put(DatabaseHelper.COLUMN_USER_ID, userId);
            contentValue.put(DatabaseHelper.COLUMN_USER_NAME, userName);
            contentValue.put(DatabaseHelper.COLUMN_USER_TYPE, userType);
            contentValue.put(DatabaseHelper.COLUMN_USER_PASSWORD, password);
            insertedRow = mSqLiteDatabase.insert(DatabaseHelper.TABLE_USER, null, contentValue);

            if (insertedRow > -1) {
                if (userType.equalsIgnoreCase(Constants.USER_REQUESTER)) {
                    ContentValues requesterContentValues = new ContentValues();
                    requesterContentValues.put(DatabaseHelper.COLUMN_REQUESTER_ID, userId);
                    requesterContentValues.put(DatabaseHelper.COLUMN_NAME, name);
                    insertedRow = mSqLiteDatabase.insert(DatabaseHelper.TABLE_REQUESTER, null, requesterContentValues);
                    PreferenceUtils.getInstance(mContext).setBooleanPreference(Constants.PREF_KEY_IS_REQUESTER,true);
                } else {
                    ContentValues workerContentValues = new ContentValues();
                    workerContentValues.put(DatabaseHelper.COLUMN_WORKER_ID, userId);
                    workerContentValues.put(DatabaseHelper.COLUMN_NAME, name);
                    workerContentValues.put(DatabaseHelper.COLUMN_SKILL_ID, -1);
                    insertedRow = mSqLiteDatabase.insert(DatabaseHelper.TABLE_WORKER, null, workerContentValues);
                    PreferenceUtils.getInstance(mContext).setBooleanPreference(Constants.PREF_KEY_IS_REQUESTER,false);
                }
            }
            if (insertedRow == -1) {
                deleteUser(userId);
                PreferenceUtils.getInstance(mContext).setBooleanPreference(Constants.PREF_KEY_IS_REQUESTER,false);
                return null;
            }
            return userId;
        } catch (SQLException e) {
            if (Constants.PRINT_LOGS)
                e.printStackTrace();
            deleteUser(generateUserId(userName));
            PreferenceUtils.getInstance(mContext).setBooleanPreference(Constants.PREF_KEY_IS_REQUESTER,false);
            return null;
        }
    }

    /**
     * updates worker skill
     * @param skillId - skill id
     * @return - true if updated else false
     */
    public boolean updateSkill(int skillId) {
        String userId = PreferenceUtils.getInstance(mContext).getStringPreference(Constants.PREF_KEY_LOGGED_IN_USER_ID);
        if (userId != null) {
            ContentValues workerContentValues = new ContentValues();
            workerContentValues.put(DatabaseHelper.COLUMN_SKILL_ID, skillId);
            int i = mSqLiteDatabase.update(DatabaseHelper.TABLE_WORKER, workerContentValues, DatabaseHelper.COLUMN_WORKER_ID + " = ?", new String[]{userId});
            return i > -1;
        }
        return true;
    }

    /**
     * gets logged in user information
     * @return - {@link Cursor}
     */
    public Cursor loginUser (String userName, String userPassword) {
        try {
            String rawQuery;
            rawQuery = "SELECT "+ DatabaseHelper.COLUMN_USER_ID + ", " + DatabaseHelper.COLUMN_USER_TYPE +" FROM " + DatabaseHelper.TABLE_USER
            + " WHERE " + DatabaseHelper.COLUMN_USER_NAME + "=" + userName + " AND " + DatabaseHelper.COLUMN_USER_PASSWORD + "=" + userPassword;
            LogUtils.d(TAG, "#loginUser#query ::" + rawQuery);
            Cursor mCursor = mSqLiteDatabase.rawQuery(rawQuery, null);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            return mCursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * gets logged in user information
     * @return - {@link Cursor}
     */
    public Cursor getLoggedInUserData () {
        try {
            String rawQuery;
            if (PreferenceUtils.getInstance(mContext).getBooleanPreference(Constants.PREF_KEY_IS_REQUESTER)) {
                rawQuery = "SELECT * FROM " + DatabaseHelper.TABLE_REQUESTER;
            } else {
                rawQuery = "SELECT * FROM " + DatabaseHelper.TABLE_WORKER;
            }
            LogUtils.d(TAG, "#getLoggedInUserData#query ::" + rawQuery);
            Cursor mCursor = mSqLiteDatabase.rawQuery(rawQuery, null);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            return mCursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * fetches all the skills
     * @return - {@link Cursor}
     */
    public Cursor fetchSkillData () {
        try {
            String rawQuery;
            rawQuery = "SELECT * FROM " + DatabaseHelper.TABLE_SKILL;
            LogUtils.d(TAG, "#fetchSkillData#query ::" + rawQuery);
            Cursor mCursor = mSqLiteDatabase.rawQuery(rawQuery, null);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            return mCursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * gets logged in user information
     * @return - {@link Cursor}
     */
    public Cursor getTaskInfo() {
        try {
            String rawQuery;
            String userId = PreferenceUtils.getInstance(mContext).getStringPreference(Constants.PREF_KEY_LOGGED_IN_USER_ID);
            rawQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASK
                    + " WHERE " + userId + "=" + DatabaseHelper.TABLE_TASK + "." + DatabaseHelper.COLUMN_TASK_FROM;
            LogUtils.d(TAG, "#getLoggedInUserData#query ::" + rawQuery);
            Cursor mCursor = mSqLiteDatabase.rawQuery(rawQuery, null);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            return mCursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * checks if user name is exists or not
     * @param userName - username to be checked
     * @return - {@link Cursor}
     */
    public Cursor checkUserNameExists(String userName) {
        String[] columns = new String[]{DatabaseHelper.COLUMN_USER_NAME};
        Cursor mCursor = mSqLiteDatabase.query(DatabaseHelper.TABLE_USER, columns, DatabaseHelper.COLUMN_USER_NAME + "=?",
                new String[]{userName}, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * deletes the existing user entry
     * @param userId - user's id
     */
    void deleteUser(String userId) {
        try {
            mSqLiteDatabase.delete(DatabaseHelper.TABLE_USER, DatabaseHelper.COLUMN_USER_ID + "=" + userId, null);
            mSqLiteDatabase.delete(DatabaseHelper.TABLE_REQUESTER, DatabaseHelper.COLUMN_REQUESTER_ID + "=" + userId, null);
            mSqLiteDatabase.delete(DatabaseHelper.TABLE_WORKER, DatabaseHelper.COLUMN_WORKER_ID + "=" + userId, null);
        } catch (SQLException e){
            if (Constants.PRINT_LOGS)
                e.printStackTrace();
        }
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
                return userName + Settings.Secure.getString(mContext.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            return Build.SERIAL;
        } catch (Exception e) {
            return userName + System.currentTimeMillis();
        }
    }
}
