package com.servicenow.skilledserviceapp.fragment.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RatingBar;

import com.servicenow.skilledserviceapp.R;
import com.servicenow.skilledserviceapp.interfaces.DialogListener;
import com.servicenow.skilledserviceapp.utils.Constants;
import com.servicenow.skilledserviceapp.utils.DialogType;
import com.servicenow.skilledserviceapp.utils.LogUtils;
import com.servicenow.skilledserviceapp.utils.PreferenceUtils;

import java.util.HashMap;

import static com.servicenow.skilledserviceapp.utils.Constants.DIALOG_KEY_ACTION_LEFT_LABEL;
import static com.servicenow.skilledserviceapp.utils.Constants.DIALOG_KEY_ACTION_RIGHT_LABEL;
import static com.servicenow.skilledserviceapp.utils.Constants.DIALOG_KEY_MESSAGE;

public class DialogFragmentHelper extends DialogFragment{
    private static final String TAG = DialogFragmentHelper.class.getSimpleName();

    private Dialog mDialog;
    private DialogType mDialogType;
    private static DialogListener mDialogListener;
    private static HashMap inputHashMap;
    private Activity mActivity;

    public static DialogFragmentHelper getInstance(DialogType mDialogType, HashMap inputMap, DialogListener listener) {
        DialogFragmentHelper mDialogFragment = new DialogFragmentHelper ();
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(Constants.KEY_DIALOG_MAP, inputMap);
        mBundle.putSerializable(Constants.KEY_DIALOG_TYPE, mDialogType);
        mDialogFragment.setArguments(mBundle);
        inputHashMap = inputMap;
        mDialogListener = listener;
        return mDialogFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        mDialogType = (DialogType) this.getArguments().getSerializable(Constants.KEY_DIALOG_TYPE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        if (isAdded()) {
            switch (mDialogType) {
                case DIALOG_FAILURE:
                    return dialogFailure();
                case DIALOG_SUCCESS:
                    return dialogSuccess();
                case DIALOG_TASK_ACTION:
                    return dialogTaskAction();
                case DIALOG_RATING:
                    return dialogRatings();
            }
        }

        return mDialog;
    }

    /**
     * creates Success Message Dialog
     * Constants.DIALOG_KEY_MESSAGE is being used to set alert message
     *
     * @return - {@link Dialog}
     */
    private Dialog dialogSuccess() {
        mDialog = new Dialog(mActivity);
        mDialog.setContentView(R.layout.dialog_success);
        final AppCompatTextView mDialogMessage = mDialog.findViewById(R.id.alert_message);
        final AppCompatButton mActionClose = mDialog.findViewById(R.id.action_close);
        mActionClose.setTag(getString(R.string.action_close));
        mActionClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogListener != null)
                    mDialogListener.onButtonClicked(mActionClose);
                dismiss();
            }
        });

        if (inputHashMap != null) {
            if (inputHashMap.containsKey(DIALOG_KEY_MESSAGE))
                mDialogMessage.setText((CharSequence) inputHashMap.get(DIALOG_KEY_MESSAGE));
        }

        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        return mDialog;
    }

    /**
     * creates Failure Message Dialog
     * Constants.DIALOG_KEY_MESSAGE is being used to set alert message
     *
     * @return - {@link Dialog}
     */
    private Dialog dialogFailure() {
        mDialog = new Dialog(mActivity);
        mDialog.setContentView(R.layout.dialog_failure);
        final AppCompatTextView mDialogMessage = mDialog.findViewById(R.id.alert_message);
        final AppCompatButton mActionClose = mDialog.findViewById(R.id.action_close);
        mActionClose.setTag(getString(R.string.action_close));
        mActionClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogListener != null)
                    mDialogListener.onButtonClicked(mActionClose);
                dismiss();
            }
        });

        if (inputHashMap != null) {
            if (inputHashMap.containsKey(DIALOG_KEY_MESSAGE))
                mDialogMessage.setText((CharSequence) inputHashMap.get(DIALOG_KEY_MESSAGE));
        }
        return mDialog;
    }

    /**
     * shows a dialog to perform action on the Task
     * Constants.DIALOG_KEY_MESSAGE is being used to set alert message
     * Constants.DIALOG_KEY_ACTION_LEFT_LABEL is being used to set left action text
     * Constants.DIALOG_KEY_ACTION_RIGHT_LABEL is being used to set right action text
     *
     * @return - {@link Dialog}
     */
    private Dialog dialogTaskAction() {
        mDialog = new Dialog(mActivity);
        mDialog.setContentView(R.layout.dialog_task_action);
        final AppCompatTextView mDialogMessage = mDialog.findViewById(R.id.alert_message);
        final AppCompatButton mActionLeft = mDialog.findViewById(R.id.action_left);
        final AppCompatButton mActionRight = mDialog.findViewById(R.id.action_right);
        mActionRight.setVisibility(View.GONE);

        mActionLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogListener != null)
                    mDialogListener.onButtonClicked(mActionLeft);
                dismiss();
            }
        });

        mActionRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogListener != null)
                    mDialogListener.onButtonClicked(mActionRight);
                dismiss();
            }
        });

        if (inputHashMap != null) {
            if (inputHashMap.containsKey(DIALOG_KEY_MESSAGE))
                mDialogMessage.setText((CharSequence) inputHashMap.get(DIALOG_KEY_MESSAGE));

            if (inputHashMap.containsKey(DIALOG_KEY_ACTION_LEFT_LABEL)) {
                mActionLeft.setText((CharSequence) inputHashMap.get(DIALOG_KEY_ACTION_LEFT_LABEL));
                mActionLeft.setTag(inputHashMap.get(DIALOG_KEY_ACTION_LEFT_LABEL));
            }

            if (inputHashMap.containsKey(DIALOG_KEY_ACTION_RIGHT_LABEL)) {
                mActionRight.setText((CharSequence) inputHashMap.get(DIALOG_KEY_ACTION_RIGHT_LABEL));
                mActionRight.setTag(inputHashMap.get(DIALOG_KEY_ACTION_RIGHT_LABEL));
                mActionRight.setVisibility(View.VISIBLE);
            }
        }
        return mDialog;
    }

    /**
     * shows a rating dialog
     * @return - {@link Dialog}
     */
    private Dialog dialogRatings() {
        PreferenceUtils.getInstance(mActivity).setFloatPreference(Constants.PREF_KEY_RATINGS, 0f);
        mDialog = new Dialog(mActivity);
        mDialog.setContentView(R.layout.dialog_ratings);
        final AppCompatTextView mDialogMessage = mDialog.findViewById(R.id.alert_message);
        final AppCompatButton mActionSave = mDialog.findViewById(R.id.action_save);
        final RatingBar mRatingBar = mDialog.findViewById(R.id.ratingBar);
        mDialogMessage.setText(getString(R.string.rate_your_experience));
        mActionSave.setTag(getString(R.string.action_save));
        mActionSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogListener != null)
                    mDialogListener.onButtonClicked(mActionSave);
                dismiss();
            }
        });

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                LogUtils.e(TAG, "#dialogRatings#onRatingChanged#rating :: " + rating);
                LogUtils.e(TAG, "#dialogRatings#onRatingChanged#fromUser :: " + fromUser);
                PreferenceUtils.getInstance(mActivity).setFloatPreference(Constants.PREF_KEY_RATINGS, rating);
            }
        });

        if (inputHashMap != null) {
            if (inputHashMap.containsKey(DIALOG_KEY_MESSAGE))
                mDialogMessage.setText((CharSequence) inputHashMap.get(DIALOG_KEY_MESSAGE));
        }
        return mDialog;
    }
}
