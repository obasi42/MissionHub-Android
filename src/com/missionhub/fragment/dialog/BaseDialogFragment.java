package com.missionhub.fragment.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockDialogFragment;
import com.missionhub.R;
import com.missionhub.util.U;

public class BaseDialogFragment extends RoboSherlockDialogFragment {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!U.superGetRetainInstance(this)) {
			setRetainInstance(true);
		}
		setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Sherlock_Light_Dialog);
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