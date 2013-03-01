package com.missionhub.android.fragment.dialog;

import android.os.Bundle;
import com.missionhub.android.util.U;
import org.holoeverywhere.app.DialogFragment;

public class BaseDialogFragment extends DialogFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!U.superGetRetainInstance(this)) {
            setRetainInstance(true);
        }
    }

    /**
     * Override to keep dialog from being dismissed on rotation
     */
    @Override
    public void onDestroyView() {
        if (getDialog() != null && U.superGetRetainInstance(this)) getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

}