package com.servicenow.skilledserviceapp.utils;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.servicenow.skilledserviceapp.activity.HelperActivity;
import com.servicenow.skilledserviceapp.activity.MainActivity;
import com.servicenow.skilledserviceapp.fragment.common.DialogFragmentHelper;
import com.servicenow.skilledserviceapp.interfaces.DialogListener;

import java.util.HashMap;

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

    /**
     * navigates to {@link com.servicenow.skilledserviceapp.fragment.SkillsFragment}
     * @param mFragmentActivity - {@link FragmentActivity}
     */
    public static void navigateToSkillsFragment(FragmentActivity mFragmentActivity) {
        Intent mIntent = new Intent(mFragmentActivity, HelperActivity.class);
        mIntent.putExtra(Constants.KEY_LOAD_FRAGMENT, NavigationType.NAV_SKILLS_FRAGMENT.getValue());
        mFragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        mFragmentActivity.startActivity(mIntent);
    }

    /**
     * navigates to {@link com.servicenow.skilledserviceapp.fragment.RequestNewTaskFragment}
     * @param mFragmentActivity - {@link FragmentActivity}
     */
    public static void navigateToRequestNewTaskFragment(FragmentActivity mFragmentActivity) {
        Intent mIntent = new Intent(mFragmentActivity, HelperActivity.class);
        mIntent.putExtra(Constants.KEY_LOAD_FRAGMENT, NavigationType.NAV_REQUEST_TASK.getValue());
        mFragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        mFragmentActivity.startActivity(mIntent);
    }

    /**
     * navigates to {@link com.servicenow.skilledserviceapp.fragment.RequestNewTaskFragment}
     * @param mFragmentActivity - {@link FragmentActivity}
     */
    public static void navigateToRequestNewTaskWithSkillsFragment(FragmentActivity mFragmentActivity, int requestedSkill) {
        Intent mIntent = new Intent(mFragmentActivity, HelperActivity.class);
        mIntent.putExtra(Constants.KEY_LOAD_FRAGMENT, NavigationType.NAV_REQUEST_TASK_WITH_SKILL.getValue());
        mIntent.putExtra(Constants.KEY_SKILL_ID, requestedSkill);
        mFragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        mFragmentActivity.startActivity(mIntent);
    }

    /**
     * launches {@link DialogFragmentHelper}
     *
     * @param mContext        - {@link FragmentActivity}
     * @param mDialogType     - {@link DialogType}
     * @param inputMap        - {@link HashMap} required to display data
     * @param mDialogListener - {@link DialogListener}
     */
    public static void showDialog(FragmentActivity mContext, DialogType mDialogType, HashMap inputMap, DialogListener mDialogListener) {
        DialogFragmentHelper mDialogFragmentHelper;
        if(mContext == null)
            return;
        FragmentManager mFragmentManager = mContext.getSupportFragmentManager();
        switch (mDialogType) {
            case DIALOG_FAILURE:
                mDialogFragmentHelper = DialogFragmentHelper.getInstance(mDialogType, inputMap, mDialogListener);
                mDialogFragmentHelper.show(mFragmentManager, Constants.DIALOG_FAILURE);
                break;
            case DIALOG_SUCCESS:
                mDialogFragmentHelper = DialogFragmentHelper.getInstance(mDialogType, inputMap, mDialogListener);
                mDialogFragmentHelper.show(mFragmentManager, Constants.DIALOG_SUCCESS);
                break;
            case DIALOG_TASK_ACTION:
                mDialogFragmentHelper = DialogFragmentHelper.getInstance(mDialogType, inputMap, mDialogListener);
                mDialogFragmentHelper.show(mFragmentManager, Constants.DIALOG_TASK_ACTION);
                break;
            case DIALOG_RATING:
                mDialogFragmentHelper = DialogFragmentHelper.getInstance(mDialogType, inputMap, mDialogListener);
                mDialogFragmentHelper.show(mFragmentManager, Constants.DIALOG_RATING);
                break;
        }
    }
}
