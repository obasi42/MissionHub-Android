package com.missionhub.util;

import java.util.Arrays;

public class U {

	public static boolean contains(final long[] values, final long value) {
		Arrays.sort(values);
		return Arrays.binarySearch(values, value) >= 0;
	}

}