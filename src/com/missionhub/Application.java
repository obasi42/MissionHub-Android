package com.missionhub;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.missionhub.auth.Auth;
import com.missionhub.auth.User;

public class Application {

	/* Running Application Version */
	private static String version;
	
	public static Bundle saveApplicationState(Bundle b) {
		b.putString("_version", version);
		Auth.saveState(b);
		User.saveState(b);
		return b;
	}
	
	public static void restoreApplicationState(Bundle b) {
		if (b == null || b.isEmpty()) return;
		version = b.getString("_appVersion");
		Auth.restoreState(b);
		User.restoreState(b);
	}
	
	public static synchronized void initVersion(Context ctx) {
		try {
			Application.setVersion(String.valueOf(ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode));
		} catch (NameNotFoundException e) {}
	}
	
	public static synchronized void setVersion(String version) {
		Application.version = version;
	}
	
	public static synchronized String getVersion() {
		return version;
	}
	
}