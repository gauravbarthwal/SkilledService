package com.servicenow.skilledserviceapp.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.servicenow.skilledserviceapp.R;
import com.servicenow.skilledserviceapp.data.DatabaseHelper;
import com.servicenow.skilledserviceapp.data.DatabaseManager;
import com.servicenow.skilledserviceapp.utils.Constants;
import com.servicenow.skilledserviceapp.utils.LogUtils;
import com.servicenow.skilledserviceapp.utils.NavigationHelper;
import com.servicenow.skilledserviceapp.utils.PreferenceUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mUserNameTV;
    private TextView mUserRatings;
    private FloatingActionButton mFloatingActionButton;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFloatingActionButton = findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        mUserNameTV = headerView.findViewById(R.id.userName);
        mUserRatings = headerView.findViewById(R.id.userRating);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void addListeners() {
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void loadUserInfo(){
        LogUtils.d(TAG, "#loadUserInfo");
        if (manager != null) {
            try {
                manager.openDatabase();
                mCursor = manager.getLoggedInUserData();
                if (mCursor != null && mCursor.moveToFirst()) {
                    do {

                        mUserNameTV.setText(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
                        mUserRatings.setText(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_RATING)));
                    } while (mCursor.moveToNext());
                }
                mCursor.close();
                manager.closeDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout) {
            PreferenceUtils.getInstance(MainActivity.this).setStringPreference(Constants.PREF_KEY_LOGGED_IN_USER_ID, "");
            PreferenceUtils.getInstance(MainActivity.this).setBooleanPreference(Constants.PREF_KEY_IS_REQUESTER, false);
            NavigationHelper.navigateToLogin(MainActivity.this);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
