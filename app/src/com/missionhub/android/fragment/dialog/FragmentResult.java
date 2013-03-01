package com.missionhub.android.fragment.dialog;

public interface FragmentResult {

    public static final int RESULT_OK = -1;
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_FIRST_USER = 1;

    public boolean onFragmentResult(int requestCode, int resultCode, Object data);

    public static class FragmentResultEvent {

        public final int requestCode;
        public final int resultCode;
        public final Object data;

        public FragmentResultEvent(int requestCode, int resultCode, Object data) {
            this.requestCode = requestCode;
            this.resultCode = resultCode;
            this.data = data;
        }

    }

}
