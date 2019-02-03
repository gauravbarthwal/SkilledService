package com.servicenow.skilledserviceapp.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.servicenow.skilledserviceapp.R;
import com.servicenow.skilledserviceapp.data.DatabaseManager;
import com.servicenow.skilledserviceapp.fragment.DashbardFragment;
import com.servicenow.skilledserviceapp.fragment.ProfileFragment;
import com.servicenow.skilledserviceapp.utils.LogUtils;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private DatabaseManager manager;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActivity();
        addListeners();
        loadUserInfo();
    }

    /**
     * initialization of Activity
     */
    private void initActivity(){
        manager = new DatabaseManager(MainActivity.this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new DashbardFragment()).commit();
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
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new DashbardFragment()).commit();
        }else if (id == R.id.navigation_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new ProfileFragment()).commit();
        }
        return true;
    }
}
