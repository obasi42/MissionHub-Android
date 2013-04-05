package com.missionhub.activity;

import com.missionhub.fragment.dialog.FragmentResult;
import org.holoeverywhere.app.Activity;

/**
 * The base missionhub activity.
 * Manages the life of the EasyTracker.
 */
public abstract class BaseActivity extends Activity implements FragmentResult {
    @Override
    public boolean onFragmentResult(int requestCode, int resultCode, Object data) {
        return false;
    }
}