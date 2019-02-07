package com.servicenow.skilledserviceapp.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.servicenow.skilledserviceapp.R;
import com.servicenow.skilledserviceapp.data.DatabaseManager;
import com.servicenow.skilledserviceapp.fragment.DashboardFragment;
import com.servicenow.skilledserviceapp.fragment.ProfileFragment;
import com.servicenow.skilledserviceapp.utils.Constants;
import com.servicenow.skilledserviceapp.utils.LogUtils;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private DatabaseManager manager;
    private Cursor mCursor;

    private String loadFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.e(TAG, "#onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActivity();
        addListeners();
        loadUserInfo();
    }

    @Override
    protected void onResume() {
        LogUtils.e(TAG, "#onResume");
        super.onResume();
        if (loadFragment != null) {
            if (loadFragment.equalsIgnoreCase(DashboardFragment.class.getSimpleName()))
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new DashboardFragment()).commit();
            else getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new ProfileFragment()).commit();
            loadFragment = null;
        }


        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        if (mFragment != null && mFragment instanceof DashboardFragment) {
            mFragment.onResume();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LogUtils.e(TAG, "#onSaveInstanceState");
        super.onSaveInstanceState(outState);
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        if (mFragment != null) {
            if (mFragment instanceof DashboardFragment)
                outState.putString(Constants.KEY_LOAD_FRAGMENT, DashboardFragment.class.getSimpleName());
            else
                outState.putString(Constants.KEY_LOAD_FRAGMENT, ProfileFragment.class.getSimpleName());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        LogUtils.e(TAG, "#onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);

        loadFragment = savedInstanceState.getString(Constants.KEY_LOAD_FRAGMENT);
    }

    /**
     * initialization of Activity
     */
    private void initActivity(){
        LogUtils.e(TAG, "#initActivity");
        manager = new DatabaseManager(MainActivity.this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        if (loadFragment == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new DashboardFragment()).commit();
    }

    private void addListeners() {
    }

    private void loadUserInfo(){
        LogUtils.d(TAG, "#loadUserInfo");
        if (manager != null) {
            try {
                manager.openDatabase();
                mCursor = manager.getLoggedInUserData();
                if (mCursor != null && mCursor.moveToFirst()) {
                    do {

                       // mUserNameTV.setText(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
                        //mUserRatings.setText(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_RATING)));
                    } while (mCursor.moveToNext());
                }
                mCursor.close();
                manager.closeDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.navigation_dashboard) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new DashboardFragment()).commit();
        }else if (id == R.id.navigation_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new ProfileFragment()).commit();
        }
        return true;
    }
}