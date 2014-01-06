package com.missionhub.util;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.missionhub.R;

import org.holoeverywhere.app.Activity;

public class FragmentUtils {

    /**
     * Safely sets the fragment's retain instance value to true
     *
     * @param fragment
     */
    public static void retainInstance(Fragment fragment) {
        Fragment parentFragment = fragment.getParentFragment();
        while (parentFragment != null) {
            if (parentFragment.getRetainInstance()) {
                return; // already set on parent
            }
            parentFragment = parentFragment.getParentFragment();
        }
        fragment.setRetainInstance(true);
    }

    /**
     * Resets the actionbar state
     *
     * @param activity
     */
    public static void resetActionBar(final Activity activity) {
        activity.setSupportProgressBarIndeterminateVisibility(false);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowCustomEnabled(false);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        activity.getSupportActionBar().setDisplayUseLogoEnabled(false);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setIcon(R.drawable.ic_launcher);
        activity.getSupportActionBar().setListNavigationCallbacks(null, null);
        activity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        activity.getSupportActionBar().setSubtitle(null);
        activity.getSupportActionBar().setTitle(R.string.app_name);
    }

}
