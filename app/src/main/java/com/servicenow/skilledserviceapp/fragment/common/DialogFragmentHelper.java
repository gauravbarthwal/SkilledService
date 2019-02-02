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

import com.servicenow.skilledserviceapp.R;
import com.servicenow.skilledserviceapp.interfaces.DialogListener;
import com.servicenow.skilledserviceapp.utils.Constants;
import com.servicenow.skilledserviceapp.utils.DialogType;

import java.util.HashMap;

import static com.servicenow.skilledserviceapp.utils.Constants.DIALOG_KEY_MESSAGE;

public class DialogFragmentHelper extends DialogFragment{
    private static final String TAG = DialogFragmentHelper.class.getSimpleName();

    private Dialog mDialog;
    private DialogType mDialogType;
    private DialogListener mDialogListener;
    private static HashMap inputHashMap;
    private Activity mActivity;

    public static DialogFragmentHelper getInstance(DialogType mDialogType, HashMap inputMap, DialogListener mDialogListener) {
        DialogFragmentHelper mDialogFragment = new DialogFragmentHelper ();
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(Constants.KEY_DIALOG_MAP, inputMap);
        mBundle.putSerializable(Constants.KEY_DIALOG_TYPE, mDialogType);
        mDialogFragment.setArguments(mBundle);
        inputHashMap = inputMap;
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
            }
        }

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
}
