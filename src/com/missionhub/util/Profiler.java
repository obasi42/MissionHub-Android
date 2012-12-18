package com.missionhub.util;

import java.util.HashMap;

import android.util.Log;

public class Profiler {

	static HashMap<String, Long> mTimes = new HashMap<String, Long>();

	public static void start(final String action) {
		mTimes.put(action, System.nanoTime());
	}

	public static void stop(final String action) {
		final long start = mTimes.get(action);
		final long total = System.nanoTime() - start;

		Log.e("PROFILE", action + ": " + Math.round(total / 1000000) + " ms");
	}

}