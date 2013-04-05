package com.missionhub.fragment;

import android.os.Bundle;
import com.missionhub.fragment.dialog.FragmentResult;
import org.holoeverywhere.app.Fragment;

public abstract class BaseFragment extends Fragment implements FragmentResult {

    public BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDestoryChildFragments(false);
    }

    @Override
    public boolean onFragmentResult(int requestCode, int resultCode, Object data) {
        return true;
    }

}