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

import com.servicenow.skilledserviceapp.R;
import com.servicenow.skilledserviceapp.activity.HelperActivity;
import com.servicenow.skilledserviceapp.data.DatabaseManager;
import com.servicenow.skilledserviceapp.utils.Constants;
import com.servicenow.skilledserviceapp.utils.NavigationHelper;

public class SignInFragment extends Fragment {
    public static final String TAG = SignInFragment.class.getSimpleName();

    private Activity mActivity;
    private AppCompatButton mActionLogin;
    private AppCompatButton mActionSignUp;

    private TextInputLayout mInputUsernameLayout;
    private TextInputLayout mInputPasswordLayout;

    private AppCompatEditText mInputUserName;
    private AppCompatEditText mInputPassword;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mFragmentView  = inflater.inflate(R.layout.fragment_signin, container, false);
        initFragmentView(mFragmentView);
        setUpListener();
        return mFragmentView;
    }

    /**
     * initialize fragment view
     * @param mView - {@link Fragment}
     */
    private void initFragmentView(View mView) {
        mInputUsernameLayout = mView.findViewById(R.id.input_layout_username);
        mInputPasswordLayout = mView.findViewById(R.id.input_layout_password);

        mInputUserName = mView.findViewById(R.id.input_username);
        mInputPassword = mView.findViewById(R.id.input_password);

        mActionLogin = mView.findViewById(R.id.action_login);
        mActionSignUp = mView.findViewById(R.id.action_signup);
    }

    /**
     * sets up listeners
     */
    private void setUpListener() {
        mInputUserName.addTextChangedListener(mTextWatcher);
        mInputPassword.addTextChangedListener(mTextWatcher);

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
                    if (isValidated()) {
//                        DatabaseManager manager = new DatabaseManager(mActivity);
//                        manager.openDatabase();
//                        manager.insertUserData(mInputUserName.getEditableText().toString(), mInputName.getEditableText().toString(), -1, mWorkerSwitch.isChecked() ? Constants.USER_WORKER : Constants.USER_REQUESTER);
                    }
                    break;
                case R.id.action_signup:
                    if (mActivity != null && mActivity instanceof HelperActivity) {
                        ((HelperActivity)mActivity).switchToSignUpFragment();
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
            mInputUsernameLayout.setErrorEnabled(false);
            mInputUsernameLayout.setError("");
            mInputPasswordLayout.setErrorEnabled(false);
            mInputPasswordLayout.setError("");
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
        String password = mInputPassword.getEditableText().toString();
        if (userName.isEmpty()) {
            mInputUsernameLayout.setErrorEnabled(true);
            mInputUsernameLayout.setError(getString(R.string.error_username_length));
            return false;
        } else if (userName.length() < getResources().getInteger(R.integer.username_min_length)
                || userName.length() > getResources().getInteger(R.integer.username_max_length)) {
            mInputUsernameLayout.setErrorEnabled(true);
            mInputUsernameLayout.setError(getString(R.string.error_username_length));
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
        }
        return true;
    }
}