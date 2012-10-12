package com.missionhub.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service to handle Account authentication. It instantiates the authenticator and returns its IBinder.
 */
public class AuthenticationService extends Service {

	/** the authenticator object */
	private Authenticator mAuthenticator;

	@Override
	public void onCreate() {
		mAuthenticator = new Authenticator(this);
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return mAuthenticator.getIBinder();
	}
}
