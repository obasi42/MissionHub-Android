package com.missionhub.fragment.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.missionhub.util.U;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.DialogFragment;

public class BaseDialogFragment extends DialogFragment implements FragmentResult {

    private boolean mSetResult = false;
    private int mRequestCode;
    private int mResultCode = RESULT_OK;
    private Object mResultData;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!U.superGetRetainInstance(this)) {
            setRetainInstance(true);
        }

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
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (!mSetResult) {
            mResultCode = RESULT_CANCELED;
        }
        super.onCancel(dialog);
    }

    public void cancel() {
        setResult(RESULT_CANCELED);
        dismiss();
    }

    /**
     * Override to keep dialog from being dismissed on rotation
     */
    @Override
    public void onDestroyView() {
        if (getDialog() != null && U.superGetRetainInstance(this)) getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    @Override
    public synchronized void onDestroy() {
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
        super.onDestroy();
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
        try {
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