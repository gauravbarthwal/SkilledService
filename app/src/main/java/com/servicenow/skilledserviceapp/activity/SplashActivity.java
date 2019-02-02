package com.servicenow.skilledserviceapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.servicenow.skilledserviceapp.R;
import com.servicenow.skilledserviceapp.utils.Constants;
import com.servicenow.skilledserviceapp.utils.NavigationHelper;
import com.servicenow.skilledserviceapp.utils.PreferenceUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (PreferenceUtils.getInstance(SplashActivity.this).getBooleanPreference(Constants.PREF_KEY_USER_LOGGED_IN)) {
            NavigationHelper.navigateToHome(SplashActivity.this);
        } else {
            NavigationHelper.navigateToLogin(SplashActivity.this);
        }

        finish();
    }
}
