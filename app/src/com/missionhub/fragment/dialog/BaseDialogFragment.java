package com.missionhub.fragment.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.missionhub.util.FragmentUtils;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialogFragment;

public class BaseDialogFragment extends AlertDialogFragment implements FragmentResult {

    private boolean mSetResult = false;
    private int mRequestCode = Integer.MIN_VALUE;
    private int mResultCode = RESULT_OK;
    private Object mResultData;

    public BaseDialogFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentUtils.retainInstance(this);

        if (getArguments() != null) {
            mRequestCode = getArguments().getInt("requestCode", Integer.MIN_VALUE);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (!mSetResult) {
            mResultCode = RESULT_OK;
        }
        super.onDismiss(dialog);
        onClose();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (!mSetResult) {
            mResultCode = RESULT_CANCELED;
        }
        super.onCancel(dialog);
    }

    public void cancel() {
        setResult(RESULT_CANCELED, mResultData);
        dismiss();
    }

    /**
     * Override to keep dialog from being dismissed on rotation
     */
    @Override
    public void onDestroyView() {
        if (getDialog() != null) getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    private void onClose() {
        if (mRequestCode != Integer.MIN_VALUE) {
            try {
                Fragment fragment = getParentFragment();
                if (fragment != null && fragment instanceof FragmentResult) {
                    if (((FragmentResult) fragment).onFragmentResult(mRequestCode, mResultCode, mResultData)) {
                        return;
                    }
                }
            } catch (Exception e) {
                // result of ui spam
            }
            try {
                Activity activity = getSupportActivity();
                if (activity != null && activity instanceof FragmentResult) {
                    ((FragmentResult) activity).onFragmentResult(mRequestCode, mResultCode, mResultData);
                }
            } catch (Exception e) {
                // result of ui spam
            }
        }
    }

    public void setResult(int code) {
        setResult(code, null);
    }

    public void setResult(int code, Object data) {
        mResultCode = code;
        mResultData = data;
        mSetResult = true;
    }

    @Override
    public boolean onFragmentResult(int requestCode, int resultCode, Object data) {
        return true;
    }

    protected static <T extends BaseDialogFragment> T show(Class<T> clss, FragmentManager fm) {
        return show(clss, fm, null, null);
    }

    protected static <T extends BaseDialogFragment> T show(Class<T> clss, FragmentManager fm, Bundle args) {
        return show(clss, fm, args, null);
    }

    protected static <T extends BaseDialogFragment> T show(Class<T> clazz, FragmentManager fm, Bundle args, Integer requestCode) {
        T fragment = findInstance(fm, clazz, true);
        if (args == null) {
            args = new Bundle();
        }
        try {
            if (requestCode != null && requestCode != 0) {
                args.putInt("requestCode", requestCode);
            }
            fragment.setArguments(args);
        } catch (Exception e) {
            // result of ui spam
        }
        fragment.show(fm);
        return fragment;
    }

    protected static <T extends BaseDialogFragment> T showForResult(Class<T> clss, FragmentManager fm, Integer requestCode) {
        return show(clss, fm, null, requestCode);
    }

    protected static <T extends BaseDialogFragment> T showForResult(Class<T> clss, FragmentManager fm, Bundle args, Integer requestCode) {
        return show(clss, fm, args, requestCode);
    }
}