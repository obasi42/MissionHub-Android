package com.missionhub.android.fragment;

import com.missionhub.android.fragment.dialog.FragmentResult;
import org.holoeverywhere.app.Fragment;

public abstract class BaseFragment extends Fragment implements FragmentResult {

    @Override
    public boolean onFragmentResult(int requestCode, int resultCode, Object data) {
        return true;
    }

}