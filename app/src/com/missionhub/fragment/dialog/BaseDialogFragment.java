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
    public void onDestroy() {
        if (mRequestCode != Integer.MIN_VALUE) {
            Fragment fragment = getParentFragment();
            if (fragment != null && fragment instanceof FragmentResult) {
                ((FragmentResult) fragment).onFragmentResult(mRequestCode, mResultCode, mResultData);
            }
            Activity activity = getSupportActivity();
            if (activity != null && activity instanceof FragmentResult) {
                ((FragmentResult) activity).onFragmentResult(mRequestCode, mResultCode, mResultData);
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

    protected static <T extends BaseDialogFragment> T show(Class<T> clss, Activity activity, FragmentManager fm) {
        return show(clss, activity, fm, null, null);
    }

    protected static <T extends BaseDialogFragment> T show(Class<T> clss, Activity activity, FragmentManager fm, Bundle args) {
        return show(clss, activity, fm, args, null);
    }

    protected static <T extends BaseDialogFragment> T show(Class<T> clss, Activity activity, FragmentManager fm, Bundle args, Integer requestCode) {
        T fragment = DialogFragment.findInstance(activity, clss, true);
        if (args == null) {
            args = new Bundle();
        }
        if (requestCode != null) {
            args.putInt("requestCode", requestCode);
        }
        fragment.setArguments(args);
        fragment.show(fm);
        return fragment;
    }

    protected static <T extends BaseDialogFragment> T showForResult(Class<T> clss, Activity activity, FragmentManager fm, Integer requestCode) {
        return show(clss, activity, fm, null, requestCode);
    }

    protected static <T extends BaseDialogFragment> T showForResult(Class<T> clss, Activity activity, FragmentManager fm, Bundle args, Integer requestCode) {
        return show(clss, activity, fm, args, requestCode);
    }
}