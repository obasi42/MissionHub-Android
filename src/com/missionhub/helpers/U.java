package com.missionhub.helpers;

public class U {
	public static boolean nullOrEmpty(Object obj) {
		if (obj instanceof String) {
			if (obj == null || ((String) obj).length() <= 0)
				return true;
		} else {
			if (obj == null)
				return true;
		}
		return false;
	}
}
