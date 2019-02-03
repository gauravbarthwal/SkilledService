package com.servicenow.skilledserviceapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);

        try {
            final Bundle mBundle = getIntent().getExtras();
            if (mBundle != null) {
                if (mBundle.containsKey(Constants.KEY_LOAD_FRAGMENT))
                    mNavigationType = NavigationType.getNavigationType(mBundle.getString(Constants.KEY_LOAD_FRAGMENT));
            }
        } catch (Exception ignored){}
        if (mNavigationType != null)
            loadFragment();
        else {
            Toast.makeText(HelperActivity.this, getString(R.string.error_oops_something_went_wrong), Toast.LENGTH_LONG).show();
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
                        .commit();
                break;
            case NAV_SIGNUP:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_frame, new SignUpFragment())
                        .commit();
                break;
            case NAV_SKILLS_FRAGMENT:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_frame, new SkillsFragment())
                        .commit();
                break;
             case NAV_REQUEST_TASK:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_frame, new RequestNewTaskFragment())
                        .commit();
                break;
        }
    }

    /**
     * switch to {@link SignInFragment}
     */
    public void switchToSignInFragment() {
        getSupportFragmentManager().beginTransaction()
                //.setCustomAnimations(R.anim.animation_slide_in, R.anim.animation_slide_out)
                //.setCustomAnimations(R.anim.animation_fade_in, R.anim.animation_fade_out)
                .replace(R.id.fragment_frame, new SignInFragment())
                .commit();
    }

    /**
     * switch to {@link SignUpFragment}
     */
    public void switchToSignUpFragment() {
        getSupportFragmentManager().beginTransaction()
                //.setCustomAnimations(R.anim.animation_slide_in, R.anim.animation_slide_out)
                //.setCustomAnimations(R.anim.animation_fade_in, R.anim.animation_fade_out)
                .replace(R.id.fragment_frame, new SignUpFragment())
                .commit();
    }

    /**
     * switch to {@link SkillsFragment}
     */
    public void switchToSkillsFragment() {
        getSupportFragmentManager().beginTransaction()
                //.setCustomAnimations(R.anim.animation_slide_in, R.anim.animation_slide_out)
                //.setCustomAnimations(R.anim.animation_fade_in, R.anim.animation_fade_out)
                .replace(R.id.fragment_frame, new SkillsFragment())
                .commit();
    }
}
