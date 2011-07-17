package com.missionhub;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.missionhub.auth.Auth;
import com.missionhub.auth.User;

public class Application {

	/* Running Application Version */
	private static String version;

	/**
	 * Saves the application state to a bundle
	 * @param b the bundle
	 * @return the bundle with saved state
	 */
	public static Bundle saveApplicationState(Bundle b) {
		b.putString("_version", version);
		Auth.saveState(b);
		User.saveState(b);
		return b;
	}
	
	/**
	 * Restores the application's state from a bundle
	 * @param b the bundle
	 */
	public static void restoreApplicationState(Bundle b) {
		if (b == null || b.isEmpty()) return;
		version = b.getString("_appVersion");
		Auth.restoreState(b);
		User.restoreState(b);
	}
	
	/**
	 * Sets the version number for the app from the package manager
	 * Only needs to be run once, version is saved by saveApplicationState
	 * @param ctx
	 */
	public static synchronized void initVersion(Context ctx) {
		try {
			Application.setVersion(String.valueOf(ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode));
		} catch (NameNotFoundException e) {}
	}
	
	/**
	 * Set the version number
	 * @param version Application Version
	 */
	public static synchronized void setVersion(String version) {
		Application.version = version;
	}
	
	/**
	 * Returns the application's version number
	 * @return
	 */
	public static synchronized String getVersion() {
		return version;
	}
}
