package com.servicenow.skilledserviceapp.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.servicenow.skilledserviceapp.R;
import com.servicenow.skilledserviceapp.data.DatabaseHelper;
import com.servicenow.skilledserviceapp.data.DatabaseManager;
import com.servicenow.skilledserviceapp.utils.Constants;
import com.servicenow.skilledserviceapp.utils.NavigationHelper;
import com.servicenow.skilledserviceapp.utils.PreferenceUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try {
            DatabaseManager manager = new DatabaseManager(SplashActivity.this);
            manager.openDatabase();
            manager.insertSkillsToDatabase();
            manager.insertDummyDataToDatabase();
            manager.closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String userId = PreferenceUtils.getInstance(SplashActivity.this).getStringPreference(Constants.PREF_KEY_LOGGED_IN_USER_ID);
        if (userId != null && !userId.isEmpty()) {
            if (PreferenceUtils.getInstance(SplashActivity.this).getBooleanPreference(Constants.PREF_KEY_IS_REQUESTER))
                NavigationHelper.navigateToHome(SplashActivity.this);
            else {
                // skill required for Worker
                int skillId = 0;
                try {
                    DatabaseManager manager = new DatabaseManager(SplashActivity.this);
                    manager.openDatabase();
                    Cursor mCursor = manager.getLoggedInUserData();
                    skillId = mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.COLUMN_SKILL_ID));
                    manager.closeDatabase();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (skillId == -1) {
                    NavigationHelper.navigateToSkillsFragment(SplashActivity.this);
                } else NavigationHelper.navigateToHome(SplashActivity.this);
            }
        } else {
            NavigationHelper.navigateToLogin(SplashActivity.this);
        }

        finish();
    }
}
