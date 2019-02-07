package com.servicenow.skilledserviceapp.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.Settings;

import com.servicenow.skilledserviceapp.data.model.Task;
import com.servicenow.skilledserviceapp.data.model.TaskType;
import com.servicenow.skilledserviceapp.utils.Constants;
import com.servicenow.skilledserviceapp.utils.LogUtils;
import com.servicenow.skilledserviceapp.utils.PreferenceUtils;

public class DatabaseManager {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private DatabaseHelper mDatabaseHelper;
    private Context mContext;
    private SQLiteDatabase mSqLiteDatabase;
    private final String[] skills = new String[]{
            "Barber",
            "Beautician",
            "Carpenter",
            "Chef",
            "Electrician",
            "House Cleaning",
            "Movers",
            "Painter",
            "Plumber",
            "Seamstress",
            "Stylist"
    };

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
            Cursor mCursor = mSqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_SKILL, null);
            if (mCursor.getCount() == 0) {
                ContentValues contentValue = new ContentValues();
                for (int i = 0; i < skills.length; i++) {
                    contentValue.put(DatabaseHelper.COLUMN_SKILL_TYPE, skills[i]);
                    contentValue.put(DatabaseHelper.COLUMN_SKILL_ID, i + 1);
                    mSqLiteDatabase.insert(DatabaseHelper.TABLE_SKILL, null, contentValue);
                }
            }
        } catch (SQLException ignored) {
        }
    }

    /**
     * insert dummy data to table
     */
    public void insertDummyDataToDatabase() {
        try {
            Cursor mCursor = mSqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_WORKER, null);
            if (mCursor.getCount() == 0) {
                for (int i = 1; i <= 15; i++)
                    insertUserData("worker" + i, "Worker" + i, "123456", Constants.USER_WORKER);

                for (int i = 1; i <= 5; i++) {
                    String userId = generateUserId("worker" + i);
                    ContentValues workerContentValues = new ContentValues();
                    workerContentValues.put(DatabaseHelper.COLUMN_SKILL_ID, 1);
                    mSqLiteDatabase.update(DatabaseHelper.TABLE_WORKER, workerContentValues, DatabaseHelper.COLUMN_WORKER_ID + " = ?", new String[]{userId});
                }

                for (int i = 6; i <= 10; i++) {
                    String userId = generateUserId("worker" + i);
                    ContentValues workerContentValues = new ContentValues();
                    workerContentValues.put(DatabaseHelper.COLUMN_SKILL_ID, 2);
                    mSqLiteDatabase.update(DatabaseHelper.TABLE_WORKER, workerContentValues, DatabaseHelper.COLUMN_WORKER_ID + " = ?", new String[]{userId});
                }

                for (int i = 11; i < 16; i++) {
                    String userId = generateUserId("worker" + i);
                    ContentValues workerContentValues = new ContentValues();
                    workerContentValues.put(DatabaseHelper.COLUMN_SKILL_ID, 3);
                    mSqLiteDatabase.update(DatabaseHelper.TABLE_WORKER, workerContentValues, DatabaseHelper.COLUMN_WORKER_ID + " = ?", new String[]{userId});
                }
            }
        } catch (SQLException ignored) {
        }
    }

    /**
     * insertUserData to DatabaseHelper.TABLE_USER and DatabaseHelper.TABLE_REQUESTER or DatabaseHelper.TABLE_WORKER
     *
     * @param userName - Unique username
     * @param name     - User's full name
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
                    PreferenceUtils.getInstance(mContext).setBooleanPreference(Constants.PREF_KEY_IS_REQUESTER, true);
                } else {
                    ContentValues workerContentValues = new ContentValues();
                    workerContentValues.put(DatabaseHelper.COLUMN_WORKER_ID, userId);
                    workerContentValues.put(DatabaseHelper.COLUMN_NAME, name);
                    workerContentValues.put(DatabaseHelper.COLUMN_SKILL_ID, -1);
                    insertedRow = mSqLiteDatabase.insert(DatabaseHelper.TABLE_WORKER, null, workerContentValues);
                    PreferenceUtils.getInstance(mContext).setBooleanPreference(Constants.PREF_KEY_IS_REQUESTER, false);
                }
            }
            if (insertedRow == -1) {
                deleteUser(userId);
                PreferenceUtils.getInstance(mContext).setBooleanPreference(Constants.PREF_KEY_IS_REQUESTER, false);
                return null;
            }
            return userId;
        } catch (SQLException e) {
            if (Constants.PRINT_LOGS)
                e.printStackTrace();
            deleteUser(generateUserId(userName));
            PreferenceUtils.getInstance(mContext).setBooleanPreference(Constants.PREF_KEY_IS_REQUESTER, false);
            return null;
        }
    }

    /**
     * updates worker skill
     *
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
     *
     * @return - {@link Cursor}
     */
    public Cursor loginUser(String userName, String userPassword) {
        try {
            String rawQuery;
            rawQuery = "SELECT * FROM " + DatabaseHelper.TABLE_USER
                    + " WHERE " + DatabaseHelper.COLUMN_USER_NAME + "='" + userName + "' AND " + DatabaseHelper.COLUMN_USER_PASSWORD + "='" + userPassword +"'";
            LogUtils.d(TAG, "#loginUser#query ::" + rawQuery);
            Cursor mCursor = mSqLiteDatabase.rawQuery(rawQuery, null);

            /*String[] columns = new String[]{DatabaseHelper.COLUMN_USER_ID, DatabaseHelper.COLUMN_USER_NAME, DatabaseHelper.COLUMN_USER_TYPE};
            Cursor mCursor = mSqLiteDatabase.query(DatabaseHelper.TABLE_USER, columns, DatabaseHelper.COLUMN_USER_NAME + "=?," + DatabaseHelper.COLUMN_USER_PASSWORD + "=?",
                    new String[]{userName,userPassword}, null, null, null);*/
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
     * store task information in table
     * @param skillId - Skill id for the task
     * @param workerId - worker id to whom request has been sent
     * @return - true if inserted successfully else false
     */
    public boolean sendRequestOfTask(int skillId, String workerId) {
        LogUtils.e(TAG, "#sendRequestOfTask#skillId :: " + skillId + " #workerId :: " + workerId );
        try {
            PreferenceUtils mPreferenceUtils = PreferenceUtils.getInstance(mContext);
            String userId = mPreferenceUtils.getStringPreference(Constants.PREF_KEY_LOGGED_IN_USER_ID);
            boolean isRequester = mPreferenceUtils.getBooleanPreference(Constants.PREF_KEY_IS_REQUESTER);
            if (isRequester) {

                String rawQuery;
                rawQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASK
                        + " WHERE " + DatabaseHelper.COLUMN_TASK_FROM + "='" + userId + "' AND " + DatabaseHelper.COLUMN_TASK_TO + "='" + workerId + "'"
                        + " AND " + DatabaseHelper.COLUMN_TASK_STATUS + " NOT IN ('" + TaskType.TASK_PENDING + "','" + TaskType.TASK_ONGOING + "')";
                LogUtils.e(TAG, "#sendRequestOfTask#rawQuery :: " + rawQuery);
                Cursor mCursor = mSqLiteDatabase.rawQuery(rawQuery, null);
                if (mCursor != null && mCursor.getCount() > 0) {
                    return false;
                }

                ContentValues contentValue = new ContentValues();
                contentValue.put(DatabaseHelper.COLUMN_TASK_SKILL, skillId);
                contentValue.put(DatabaseHelper.COLUMN_TASK_FROM, userId);
                contentValue.put(DatabaseHelper.COLUMN_TASK_TO, workerId);
                contentValue.put(DatabaseHelper.COLUMN_TASK_STATUS, TaskType.TASK_PENDING.getValue());
                contentValue.put(DatabaseHelper.COLUMN_TASK_RATING, 0f);
                contentValue.put(DatabaseHelper.COLUMN_TASK_DESCRIPTION, "");
                long inserted  = mSqLiteDatabase.insert(DatabaseHelper.TABLE_TASK, null, contentValue);
                if (inserted > -1)
                    return true;
                else return false;
            }
        } catch (Exception e){
            if (Constants.PRINT_LOGS)
                e.printStackTrace();
        }
        return false;
    }

    /**
     * gets logged in user information
     *
     * @return - {@link Cursor}
     */
    public Cursor getLoggedInUserData() {
        try {
            String rawQuery;
            PreferenceUtils mPreferenceUtils = PreferenceUtils.getInstance(mContext);
            String userId = mPreferenceUtils.getStringPreference(Constants.PREF_KEY_LOGGED_IN_USER_ID);
            boolean isRequester = mPreferenceUtils.getBooleanPreference(Constants.PREF_KEY_IS_REQUESTER);
            if (isRequester) {
                rawQuery = "SELECT * FROM " + DatabaseHelper.TABLE_REQUESTER + " WHERE " + DatabaseHelper.COLUMN_REQUESTER_ID + " = '" + userId + "'";
            } else {
                rawQuery = "SELECT * FROM " + DatabaseHelper.TABLE_WORKER + " WHERE " + DatabaseHelper.COLUMN_WORKER_ID + " = '" + userId + "'" ;
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
     *
     * @return - {@link Cursor}
     */
    public Cursor fetchSkillData() {
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
     * fetches all the skilled worked who are already got request
     *
     * @return - {@link Cursor}
     */
    public Cursor fetchAlreadyRequestedWorkers(int skillId) {
        try {
            String rawQuery;
            PreferenceUtils mPreferenceUtils = PreferenceUtils.getInstance(mContext);
            String userId = mPreferenceUtils.getStringPreference(Constants.PREF_KEY_LOGGED_IN_USER_ID);
            rawQuery = "SELECT "+ DatabaseHelper.COLUMN_TASK_TO +" FROM " + DatabaseHelper.TABLE_TASK
                    + " WHERE " + DatabaseHelper.COLUMN_TASK_SKILL + " = " + skillId
                    + " AND " + DatabaseHelper.COLUMN_TASK_FROM +  " = '" + userId + "'"
                    + " AND " + DatabaseHelper.COLUMN_TASK_STATUS + " NOT IN('" + TaskType.TASK_COMPLETED.getValue() +"')";
            LogUtils.d(TAG, "#fetchAlreadyRequestedWorkers#query ::" + rawQuery);
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
     * fetches all the skilled worked based on the skillId
     *
     * @return - {@link Cursor}
     */
    public Cursor fetchSkilledWorkerData(int skillId) {
        try {
            String rawQuery;
            rawQuery = "SELECT * FROM " + DatabaseHelper.TABLE_WORKER + ", " + DatabaseHelper.TABLE_SKILL
            + " WHERE " + DatabaseHelper.TABLE_WORKER + "." + DatabaseHelper.COLUMN_SKILL_ID + "=" + skillId
                    + " AND " + DatabaseHelper.TABLE_WORKER + "." + DatabaseHelper.COLUMN_SKILL_ID +  " = " + DatabaseHelper.TABLE_SKILL + "." + DatabaseHelper.COLUMN_SKILL_ID
                    + " ORDER BY " + DatabaseHelper.COLUMN_RATING + " DESC";
            LogUtils.d(TAG, "#fetchSkilledWorkerData#query ::" + rawQuery);
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
     *
     * @return - {@link Cursor}
     */
    public Cursor getTaskInfo() {
        try {
            String rawQuery;
            PreferenceUtils mPreferenceUtils = PreferenceUtils.getInstance(mContext);
            String userId = mPreferenceUtils.getStringPreference(Constants.PREF_KEY_LOGGED_IN_USER_ID);
            boolean isRequester = mPreferenceUtils.getBooleanPreference(Constants.PREF_KEY_IS_REQUESTER);
            LogUtils.d(TAG, "#getTaskInfo#isRequester ::" + isRequester);
            if (isRequester) {
                rawQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASK + " , " + DatabaseHelper.TABLE_SKILL + " , " + DatabaseHelper.TABLE_WORKER
                        + " WHERE " + DatabaseHelper.TABLE_TASK + "." + DatabaseHelper.COLUMN_TASK_FROM + "= '" + userId + "' AND "
                        + DatabaseHelper.TABLE_TASK + "." + DatabaseHelper.COLUMN_TASK_SKILL +"=" + DatabaseHelper.TABLE_SKILL + "." + DatabaseHelper.COLUMN_SKILL_ID + " AND "
                        + DatabaseHelper.TABLE_TASK + "." + DatabaseHelper.COLUMN_TASK_TO + "=" + DatabaseHelper.TABLE_WORKER + "." + DatabaseHelper.COLUMN_WORKER_ID
                        + " ORDER BY " + DatabaseHelper.TABLE_TASK + "." + DatabaseHelper.COLUMN_TASK_CREATED_AT + " DESC";
            } else {
                rawQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASK + " , " + DatabaseHelper.TABLE_SKILL + " , " + DatabaseHelper.TABLE_REQUESTER
                        + " WHERE " + DatabaseHelper.TABLE_TASK + "." + DatabaseHelper.COLUMN_TASK_TO + "= '" + userId + "' AND "
                        + DatabaseHelper.TABLE_TASK + "." + DatabaseHelper.COLUMN_TASK_SKILL +"=" + DatabaseHelper.TABLE_SKILL + "." + DatabaseHelper.COLUMN_SKILL_ID + " AND "
                        + DatabaseHelper.TABLE_REQUESTER + "." + DatabaseHelper.COLUMN_REQUESTER_ID + "=" + DatabaseHelper.TABLE_TASK + "." + DatabaseHelper.COLUMN_TASK_FROM
                        + " ORDER BY " + DatabaseHelper.TABLE_TASK + "." + DatabaseHelper.COLUMN_TASK_CREATED_AT + " DESC";
            }
            LogUtils.d(TAG, "#getTaskInfo#query ::" + rawQuery);
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
     * updates task status
     * @param taskId - TaskID to be updated
     * @param mTaskType - {@link TaskType}
     */
    public boolean updateTaskStatus(int taskId, TaskType mTaskType) {
        String userId = PreferenceUtils.getInstance(mContext).getStringPreference(Constants.PREF_KEY_LOGGED_IN_USER_ID);
        if (userId != null) {
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(DatabaseHelper.COLUMN_TASK_STATUS, mTaskType.getValue());
            int i = mSqLiteDatabase.update(DatabaseHelper.TABLE_TASK, mContentValues, DatabaseHelper.COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
            return i > -1;
        }
        return false;
    }

    /**
     * updates task ratings
     * @param ratings - ratings
     * @param taskId - task id
     * @return - true if updated else false
     */
    public boolean updateTaskRatings(float ratings, int taskId) {
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(DatabaseHelper.COLUMN_TASK_RATING, ratings);
        int i = mSqLiteDatabase.update(DatabaseHelper.TABLE_TASK, mContentValues, DatabaseHelper.COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
        return i > -1;
    }

    /**
     * updates worker ratings based on the task
     * @param workerId - worker id
     */
    public void updateWorkerRatings(String workerId) {
        try {
            String rawQuery = "SELECT SUM(" + DatabaseHelper.COLUMN_TASK_RATING + ") AS '" + DatabaseHelper.COLUMN_TASK_TOTAL_RATING +"'"
                    + " COUNT(" + DatabaseHelper.COLUMN_TASK_RATING + ")"
                    + " FROM " + DatabaseHelper.TABLE_TASK + " WHERE " + DatabaseHelper.COLUMN_TASK_TO + " = '" + workerId +"'";
            LogUtils.e(TAG, "#updateWorkerRatings#rawQuery :: " + rawQuery);
            Cursor mCursor = mSqLiteDatabase.rawQuery(rawQuery, null);
            if (mCursor != null && mCursor.moveToFirst()) {
                float totalRatings = mCursor.getFloat(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_TOTAL_RATING));
                int count = mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_RATING));
                float averageRating = totalRatings / count;
                ContentValues mContentValues = new ContentValues();
                mContentValues.put(DatabaseHelper.COLUMN_RATING, averageRating);
                mSqLiteDatabase.update(DatabaseHelper.TABLE_WORKER, mContentValues, DatabaseHelper.COLUMN_WORKER_ID + " = ?", new String[]{workerId});
            }
        } catch (Exception e){
            if (Constants.PRINT_LOGS)
                e.printStackTrace();
        }
    }

    /**
     * checks if user name is exists or not
     *
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
     *
     * @param userId - user's id
     */
    void deleteUser(String userId) {
        try {
            mSqLiteDatabase.delete(DatabaseHelper.TABLE_USER, DatabaseHelper.COLUMN_USER_ID + "=" + userId, null);
            mSqLiteDatabase.delete(DatabaseHelper.TABLE_REQUESTER, DatabaseHelper.COLUMN_REQUESTER_ID + "=" + userId, null);
            mSqLiteDatabase.delete(DatabaseHelper.TABLE_WORKER, DatabaseHelper.COLUMN_WORKER_ID + "=" + userId, null);
        } catch (SQLException e) {
            if (Constants.PRINT_LOGS)
                e.printStackTrace();
        }
    }

    /**
     * generates unique user id
     *
     * @return - user id
     */
    @SuppressLint("HardwareIds")
    private String generateUserId(String userName) {
        try {
            if (Build.SERIAL == null || Build.SERIAL.length() == 0
                    || Build.SERIAL.equalsIgnoreCase("unknown"))
                return userName + Settings.Secure.getString(mContext.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            return userName + Build.SERIAL;
        } catch (Exception e) {
            return userName + System.currentTimeMillis();
        }
    }
}
