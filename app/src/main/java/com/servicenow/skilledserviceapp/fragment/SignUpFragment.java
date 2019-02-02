package com.servicenow.skilledserviceapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.servicenow.skilledserviceapp.R;
import com.servicenow.skilledserviceapp.activity.HelperActivity;
import com.servicenow.skilledserviceapp.data.DatabaseHelper;
import com.servicenow.skilledserviceapp.data.DatabaseManager;
import com.servicenow.skilledserviceapp.utils.Constants;
import com.servicenow.skilledserviceapp.utils.DialogType;
import com.servicenow.skilledserviceapp.utils.LogUtils;
import com.servicenow.skilledserviceapp.utils.NavigationHelper;
import com.servicenow.skilledserviceapp.utils.PreferenceUtils;

import java.util.HashMap;

public class SignUpFragment extends Fragment {
    private static final String TAG = SignUpFragment.class.getSimpleName();

    private Activity mActivity;
    private ProgressBar mProgressBar;
    private AppCompatButton mActionLogin;
    private AppCompatButton mActionSignUp;

    private Switch mWorkerSwitch;
    private TextInputLayout mInputNameLayout;
    private TextInputLayout mInputUserNameLayout;
    private TextInputLayout mInputPasswordLayout;
    private TextInputLayout mInputConfirmPasswordLayout;
    private AppCompatEditText mInputName;
    private AppCompatEditText mInputUserName;
    private AppCompatEditText mInputPassword;
    private AppCompatEditText mInputConfirmPassword;

    private DatabaseManager manager;
    private String userName = "";

    /**
     * click listener for mActionLogin
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action_login:
                    if (mActivity != null && mActivity instanceof HelperActivity) {
                        ((HelperActivity) mActivity).switchToSignInFragment();
                    }
                    break;
                case R.id.action_signup:
                    if (isValidated()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        manager.openDatabase();
                        String userId = manager.insertUserData(mInputUserName.getEditableText().toString(), mInputName.getEditableText().toString(),
                                mInputPassword.getEditableText().toString(), mWorkerSwitch.isChecked() ? Constants.USER_WORKER : Constants.USER_REQUESTER);
                        if (userId == null) {
                            final HashMap<String, String> inputMap = new HashMap<>();
                            inputMap.put(Constants.DIALOG_KEY_MESSAGE, getString(R.string.error_registration_failed));
                            NavigationHelper.showDialog(getActivity(), DialogType.DIALOG_FAILURE, inputMap, null);
                            mProgressBar.setVisibility(View.GONE);
                        } else {
                            mProgressBar.setVisibility(View.GONE);
                            Snackbar.make(v, "Registration successful", Snackbar.LENGTH_LONG).show();
                                    //.setAction("Action", null).show();
                            PreferenceUtils.getInstance(mActivity).setStringPreference(Constants.PREF_KEY_LOGGED_IN_USER_ID, userId);
                            if (mWorkerSwitch.isChecked()) {
                                if (mActivity != null && mActivity instanceof HelperActivity) {
                                    ((HelperActivity) mActivity).switchToSkillsFragment();
                                }
                            } else {
                                //Toast.makeText(mActivity, "Signup success", Toast.LENGTH_SHORT).show();
                                NavigationHelper.navigateToHome(getActivity());
                                mActivity.finish();
                            }
                        }
                    }
                    break;
            }
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mInputNameLayout.setErrorEnabled(false);
            mInputNameLayout.setError("");
            mInputPasswordLayout.setErrorEnabled(false);
            mInputPasswordLayout.setError("");
            mInputConfirmPasswordLayout.setErrorEnabled(false);
            mInputConfirmPasswordLayout.setError("");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher mUserNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            LogUtils.e(TAG, "#onTextChanged :: " + s);
            mInputUserNameLayout.setErrorEnabled(false);
            mInputUserNameLayout.setError("");
            /*if (manager != null) {
                manager.openDatabase();
                Cursor mCursor = manager.checkUserNameExists(s.toString());
                if (mCursor != null) {
                    while (mCursor.moveToFirst())
                        LogUtils.d(TAG, "#onTextChanged#userName :: " + mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_USER_NAME)));
                }
                mCursor.close();
                manager.closeDatabase();
            }*/
            if (count > 0) {
                userName = s.toString();
                mUserNameCheckHandler.removeCallbacks(mUserNameCheckRunnable);
                mUserNameCheckHandler.postDelayed(mUserNameCheckRunnable, 150);
            }
            else userName = "";
        }

        @Override
        public void afterTextChanged(Editable s) {
            LogUtils.e(TAG, "#afterTextChanged :: " + s.toString());

        }
    };

    private Handler mUserNameCheckHandler = new Handler();
    private Runnable mUserNameCheckRunnable = new Runnable() {
        @Override
        public void run() {
            if (manager != null) {
                try {
                    manager.openDatabase();
                    Cursor mCursor = manager.checkUserNameExists(userName);
                    if (mCursor != null && mCursor.moveToFirst()) {
                        do {
                            if (userName.equals(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_USER_NAME)))) {
                                mInputUserNameLayout.setErrorEnabled(true);
                                mInputUserNameLayout.setError(getString(R.string.error_username_already_exists));
                            }
                        } while (mCursor.moveToNext());
                    }
                    mCursor.close();
                    manager.closeDatabase();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        manager = new DatabaseManager(mActivity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mFragmentView = inflater.inflate(R.layout.fragment_signup, container, false);
        initFragmentView(mFragmentView);
        setUpListener();
        return mFragmentView;
    }

    /**
     * initialize fragment view
     *
     * @param mView - {@link Fragment}
     */
    private void initFragmentView(View mView) {
        mProgressBar = mView.findViewById(R.id.progressBar);
        mWorkerSwitch = mView.findViewById(R.id.worker_switch);

        mInputNameLayout = mView.findViewById(R.id.input_layout_name);
        mInputUserNameLayout = mView.findViewById(R.id.input_layout_username);
        mInputPasswordLayout = mView.findViewById(R.id.input_layout_password);
        mInputConfirmPasswordLayout = mView.findViewById(R.id.input_layout_confirm_password);

        mInputName = mView.findViewById(R.id.input_name);
        mInputUserName = mView.findViewById(R.id.input_username);
        mInputPassword = mView.findViewById(R.id.input_password);
        mInputConfirmPassword = mView.findViewById(R.id.input_confirm_password);

        mActionLogin = mView.findViewById(R.id.action_login);
        mActionSignUp = mView.findViewById(R.id.action_signup);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * sets up listeners
     */
    private void setUpListener() {
        mInputName.addTextChangedListener(mTextWatcher);
        mInputUserName.addTextChangedListener(mUserNameTextWatcher);
        mInputPassword.addTextChangedListener(mTextWatcher);
        mInputConfirmPassword.addTextChangedListener(mTextWatcher);

        mActionLogin.setOnClickListener(onClickListener);
        mActionSignUp.setOnClickListener(onClickListener);
    }

    /**
     * validates all the fields
     *
     * @return - true if all the fields are validated else false
     */
    private boolean isValidated() {
        String userName = mInputUserName.getEditableText().toString();
        String name = mInputName.getEditableText().toString();
        String password = mInputPassword.getEditableText().toString();
        String confirmPassword = mInputConfirmPassword.getEditableText().toString();
        if (name.isEmpty()) {
            mInputNameLayout.setErrorEnabled(true);
            mInputNameLayout.setError(getString(R.string.error_please_enter_proper_name));
            return false;
        } else if (userName.isEmpty()) {
            mInputUserNameLayout.setErrorEnabled(true);
            mInputUserNameLayout.setError(getString(R.string.error_username_length));
            return false;
        } else if (userName.length() < getResources().getInteger(R.integer.username_min_length)
                || userName.length() > getResources().getInteger(R.integer.username_max_length)) {
            mInputUserNameLayout.setErrorEnabled(true);
            mInputUserNameLayout.setError(getString(R.string.error_username_length));
            return false;
        } else if (password.isEmpty()) {
            mInputPasswordLayout.setErrorEnabled(true);
            mInputPasswordLayout.setError(getString(R.string.error_password_length));
            return false;
        } else if (password.length() < getResources().getInteger(R.integer.password_min_length)
                || password.length() > getResources().getInteger(R.integer.password_max_length)) {
            mInputPasswordLayout.setErrorEnabled(true);
            mInputPasswordLayout.setError(getString(R.string.error_password_length));
            return false;
        } else if (!confirmPassword.equals(password)) {
            mInputConfirmPasswordLayout.setErrorEnabled(true);
            mInputConfirmPasswordLayout.setError(getString(R.string.error_confirm_password));
            return false;
        }
        return true;
    }
}
