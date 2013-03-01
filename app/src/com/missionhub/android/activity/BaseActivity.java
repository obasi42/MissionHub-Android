package com.missionhub.android.activity;

import android.os.Bundle;
import com.missionhub.android.fragment.dialog.FragmentResult;
import com.missionhub.android.util.EasyTracker;
import org.holoeverywhere.app.Activity;

/**
 * The base missionhub activity.
 * Manages the life of the EasyTracker.
 */
public abstract class BaseActivity extends Activity implements FragmentResult {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasyTracker.getTracker().setContext(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getTracker().trackActivityStart(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        final Object o = super.onRetainCustomNonConfigurationInstance();
        EasyTracker.getTracker().trackActivityRetainNonConfigurationInstance();
        return o;
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getTracker().trackActivityStop(this);
    }

    @Override
    public boolean onFragmentResult(int requestCode, int resultCode, Object data) {
        return false;
    }
}