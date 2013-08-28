package com.missionhub.fragment.dialog;

public interface FragmentResult {

    public static final int RESULT_OK = -1;
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_FIRST_USER = 1;

    public boolean onFragmentResult(int requestCode, int resultCode, Object data);

}
