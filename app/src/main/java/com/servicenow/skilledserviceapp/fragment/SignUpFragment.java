package com.servicenow.skilledserviceapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.servicenow.skilledserviceapp.R;
import com.servicenow.skilledserviceapp.activity.HelperActivity;
import com.servicenow.skilledserviceapp.data.DatabaseManager;
import com.servicenow.skilledserviceapp.utils.Constants;

public class SignUpFragment extends Fragment {

    private Activity mActivity;
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mFragmentView  = inflater.inflate(R.layout.fragment_signup, container, false);
        initFragmentView(mFragmentView);
        setUpListener();
        return mFragmentView;
    }

    /**
     * initialize fragment view
     * @param mView - {@link Fragment}
     */
    private void initFragmentView(View mView) {
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
    }

    /**
     * sets up listeners
     */
    private void setUpListener() {
        mInputName.addTextChangedListener(mTextWatcher);
        mInputUserName.addTextChangedListener(mTextWatcher);
        mInputPassword.addTextChangedListener(mTextWatcher);
        mInputConfirmPassword.addTextChangedListener(mTextWatcher);

        mActionLogin.setOnClickListener(onClickListener);
        mActionSignUp.setOnClickListener(onClickListener);
    }

    /**
     * click listener for mActionLogin
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action_login:
                    if (mActivity != null && mActivity instanceof HelperActivity) {
                        ((HelperActivity)mActivity).switchToSignInFragment();
                    }
                    break;
                case R.id.action_signup:
                    if (isValidated()) {
                        DatabaseManager manager = new DatabaseManager(mActivity);
                        manager.openDatabase();
                        boolean isInserted = manager.insertUserData(mInputUserName.getEditableText().toString(), mInputName.getEditableText().toString(), -1, mWorkerSwitch.isChecked() ? Constants.USER_WORKER : Constants.USER_REQUESTER);
                        if (!isInserted) {

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
            mInputUserNameLayout.setErrorEnabled(false);
            mInputUserNameLayout.setError("");
            mInputPasswordLayout.setErrorEnabled(false);
            mInputPasswordLayout.setError("");
            mInputConfirmPasswordLayout.setErrorEnabled(false);
            mInputConfirmPasswordLayout.setError("");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * validates all the fields
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
