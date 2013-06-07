package com.missionhub.event;

import com.missionhub.fragment.HostedFragment;

public class ChangeHostFragmentEvent {

    private Class<? extends HostedFragment> mClss;
    private boolean mAddToBackstack;
    private int mInAnimation = android.R.anim.fade_in;
    private int mOutAnimation = android.R.anim.fade_out;
    private int mPopInAnimation = mInAnimation;
    private int mPopOutAnimation = mOutAnimation;
    private boolean mNewInstance;
    private String mFragmentTag;
    private OnFragmentChangedCallback mCallback;

    public ChangeHostFragmentEvent(final Class<? extends HostedFragment> clss) {
        mClss = clss;
    }

    public interface OnFragmentChangedCallback {
        public void onFragmentChanged(HostedFragment fragment);
    }

    public Class<? extends HostedFragment> getFragmentClass() {
        return mClss;
    }

    public boolean isAddToBackstack() {
        return mAddToBackstack;
    }

    public void setAddToBackstack(boolean addToBackstack) {
        mAddToBackstack = addToBackstack;
    }

    public int getInAnimation() {
        return mInAnimation;
    }

    public void setInAnimation(int inAnimation) {
        mInAnimation = inAnimation;
    }

    public int getOutAnimation() {
        return mOutAnimation;
    }

    public void setOutAnimation(int outAnimation) {
        mOutAnimation = outAnimation;
    }

    public int getPopInAnimation() {
        return mPopInAnimation;
    }

    public void setPopInAnimation(int popInAnimation) {
        mPopInAnimation = popInAnimation;
    }

    public int getPopOutAnimation() {
        return mPopOutAnimation;
    }

    public void setPopOutAnimation(int popOutAnimation) {
        mPopOutAnimation = popOutAnimation;
    }

    public boolean isNewInstance() {
        return mNewInstance;
    }

    public void setNewInstance(boolean newInstance) {
        mNewInstance = newInstance;
    }

    public String getFragmentTag() {
        if (mFragmentTag == null) {
            return mClss.getName();
        }
        return mFragmentTag;
    }

    public void setFragmentTag(String fragmentTag) {
        mFragmentTag = fragmentTag;
    }

    public OnFragmentChangedCallback getCallback() {
        return mCallback;
    }

    public void setCallback(OnFragmentChangedCallback callback) {
        mCallback = callback;
    }
}
