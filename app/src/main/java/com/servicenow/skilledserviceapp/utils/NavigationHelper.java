package com.servicenow.skilledserviceapp.utils;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.servicenow.skilledserviceapp.activity.HelperActivity;
import com.servicenow.skilledserviceapp.activity.MainActivity;

public class NavigationHelper {

    public static void navigateToHome(FragmentActivity mFragmentActivity) {
        Intent intent = new Intent(mFragmentActivity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mFragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        mFragmentActivity.startActivity(intent);
    }
    /**
     * navigates to {@link com.servicenow.skilledserviceapp.fragment.SignInFragment}
     * @param mFragmentActivity - {@link FragmentActivity}
     */
    public static void navigateToLogin(FragmentActivity mFragmentActivity) {
        Intent mIntent = new Intent(mFragmentActivity, HelperActivity.class);
        mIntent.putExtra(Constants.KEY_LOAD_FRAGMENT, NavigationType.NAV_LOGIN.getValue());
        mFragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        mFragmentActivity.startActivity(mIntent);
    }

    /**
     * navigates to {@link com.servicenow.skilledserviceapp.fragment.SignUpFragment}
     * @param mFragmentActivity - {@link FragmentActivity}
     */
    public static void navigateToSignUp(FragmentActivity mFragmentActivity) {
        Intent mIntent = new Intent(mFragmentActivity, HelperActivity.class);
        mIntent.putExtra(Constants.KEY_LOAD_FRAGMENT, NavigationType.NAV_SIGNUP.getValue());
        mFragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        mFragmentActivity.startActivity(mIntent);
    }
}
