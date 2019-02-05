package com.servicenow.skilledserviceapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.servicenow.skilledserviceapp.R;
import com.servicenow.skilledserviceapp.fragment.RequestNewTaskFragment;
import com.servicenow.skilledserviceapp.fragment.SignInFragment;
import com.servicenow.skilledserviceapp.fragment.SignUpFragment;
import com.servicenow.skilledserviceapp.fragment.SkillsFragment;
import com.servicenow.skilledserviceapp.utils.Constants;
import com.servicenow.skilledserviceapp.utils.NavigationType;

public class HelperActivity extends FragmentActivity {
    private NavigationType mNavigationType;
    private Bundle mBundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);

        try {
            mBundle = getIntent().getExtras();
            if (mBundle != null) {
                if (mBundle.containsKey(Constants.KEY_LOAD_FRAGMENT))
                    mNavigationType = NavigationType.getNavigationType(mBundle.getString(Constants.KEY_LOAD_FRAGMENT));
            }
        } catch (Exception ignored){}
        if (mNavigationType != null)
            loadFragment();
        else {
            Toast.makeText(HelperActivity.this, getString(R.string.error_oops_something_went_wrong), Toast.LENGTH_LONG).show();
            return;
        }
    }

    /**
     * loads fragment
     */
    private void loadFragment() {
        switch (mNavigationType) {
            case NAV_LOGIN:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_frame, new SignInFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                break;
            case NAV_SIGNUP:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_frame, new SignUpFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                break;
            case NAV_SKILLS_FRAGMENT:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_frame, new SkillsFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                break;
            case NAV_REQUEST_TASK_WITH_SKILL:
            case NAV_REQUEST_TASK:
                 RequestNewTaskFragment mRequestNewTaskFragment = new RequestNewTaskFragment();
                 mRequestNewTaskFragment.setArguments(mBundle);
                 getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_frame, mRequestNewTaskFragment)
                         .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                break;
        }
    }

    /**
     * switch to {@link SignInFragment}
     */
    public void switchToSignInFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_frame, new SignInFragment())
                .commit();
    }

    /**
     * switch to {@link SignUpFragment}
     */
    public void switchToSignUpFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_frame, new SignUpFragment())
                .commit();
    }

    /**
     * switch to {@link SkillsFragment}
     */
    public void switchToSkillsFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_frame, new SkillsFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
        if (mFragment != null && mFragment instanceof RequestNewTaskFragment) {
            if (((RequestNewTaskFragment)mFragment).closeActivity()) {
                super.onBackPressed();
            }
        } else
            super.onBackPressed();
    }
}