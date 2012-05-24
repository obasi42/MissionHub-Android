package com.missionhub.android.app;


import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;

public class DisplayMode {

	private final MissionHubApplication mApplication;

	private boolean mIsW1024dp = false;

	private boolean mIsH600dp = false;

	private boolean mIsSW720dp = false;

	private final int mSdkVersion = Build.VERSION.SDK_INT;

	private boolean mIsWide = false;

	public DisplayMode(final MissionHubApplication application) {
		mApplication = application;
		calculateSizes();
	}

	protected void onConfigurationChanged(final Configuration newConfig) {
		calculateSizes();
	}

	private void calculateSizes() {
		final DisplayMetrics dm = mApplication.getResources().getDisplayMetrics();
		final int widthdp = (int) (dm.widthPixels / dm.density + 0.5f);
		final int heightdp = (int) (dm.heightPixels / dm.density + 0.5f);

		if (widthdp > heightdp) {
			mIsWide = true;
		} else {
			mIsWide = false;
		}

		if (widthdp >= 720 && heightdp >= 720) {
			mIsSW720dp = true;
		} else {
			mIsSW720dp = false;
		}

		if (widthdp >= 1024) {
			mIsW1024dp = true;
		} else {
			mIsW1024dp = false;
		}

		if (heightdp >= 600) {
			mIsH600dp = true;
		} else {
			mIsH600dp = false;
		}
	}

	public boolean isW1024dp() {
		return mIsW1024dp;
	}

	public boolean isH600dp() {
		return mIsH600dp;
	}

	public boolean isSW720dp() {
		return mIsSW720dp;
	}

	public int getSdkVersion() {
		return mSdkVersion;
	}

	public boolean isWide() {
		return mIsWide;
	}

	public boolean isTablet() {
		return isSW720dp() || (isW1024dp() && isH600dp());
	}

	public boolean isOldTablet() {
		if (isTablet() && mSdkVersion < Build.VERSION_CODES.HONEYCOMB) {
			return true;
		}
		return false;
	}

	public boolean isPhone() {
		return !isTablet();
	}
}