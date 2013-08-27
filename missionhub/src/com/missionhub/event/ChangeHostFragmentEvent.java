package com.missionhub.event;

import android.os.Bundle;

import com.missionhub.R;
import com.missionhub.fragment.HostedFragment;

public class ChangeHostFragmentEvent {

    private Class<? extends HostedFragment> mClss;
    private boolean mAddToBackstack;
    private int mInAnimation = R.anim.fade_in;
    private int mOutAnimation = R.anim.fade_out;
    private int mPopInAnimation = mInAnimation;
    private int mPopOutAnimation = mOutAnimation;
    private boolean mNewInstance;
    private String mFragmentTag;
    private OnFragmentChangedCallback mCallback;
    private Bundle mArguments;

    public ChangeHostFragmentEvent(final Class<? extends HostedFragment> clss) {
        this(clss, null);
    }

    public ChangeHostFragmentEvent(final Class<? extends HostedFragment> clss, OnFragmentChangedCallback callback) {
        mClss = clss;
        mCallback = callback;
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
        setInAnimation(inAnimation, inAnimation);
    }

    public void setInAnimation(int inAnimation, int popInAnimation) {
        mInAnimation = inAnimation;
        mPopInAnimation = popInAnimation;
    }

    public int getOutAnimation() {
        return mOutAnimation;
    }

    public void setOutAnimation(int outAnimation) {
        setOutAnimation(outAnimation, outAnimation);
    }

    public void setOutAnimation(int outAnimation, int popOutAnimation) {
        mOutAnimation = outAnimation;
        mPopOutAnimation = popOutAnimation;
    }

    public int getPopInAnimation() {
        return mPopInAnimation;
    }

    public int getPopOutAnimation() {
        return mPopOutAnimation;
    }

    public boolean isNewInstance() {
        return mNewInstance;
    }

    public void setNewInstance(boolean newInstance) {
        mNewInstance = newInstance;
    }

    public void setNewInstance(boolean newInstance, Bundle arguments) {
        mNewInstance = newInstance;
        mArguments = arguments;
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

    public Bundle getArguments() {
        return mArguments;
    }
}
