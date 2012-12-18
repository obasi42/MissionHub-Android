package com.missionhub.sync;

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class SyncService extends Service {
	private static final String TAG = SyncService.class.getSimpleName();

	private static SyncAdapterImpl sSyncAdapter = null;
	private static ContentResolver mContentResolver = null;

	public SyncService() {
		super();
	}

	private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
		private final Context mContext;

		public SyncAdapterImpl(final Context context) {
			super(context, true);
			mContext = context;
		}

		@Override
		public void onPerformSync(final Account account, final Bundle extras, final String authority, final ContentProviderClient provider, final SyncResult syncResult) {
			try {
				SyncService.performSync(mContext, account, extras, authority, provider, syncResult);
			} catch (final OperationCanceledException e) {}
		}
	}

	@Override
	public IBinder onBind(final Intent intent) {
		IBinder ret = null;
		ret = getSyncAdapter().getSyncAdapterBinder();
		return ret;
	}

	private SyncAdapterImpl getSyncAdapter() {
		if (sSyncAdapter == null) sSyncAdapter = new SyncAdapterImpl(this);
		return sSyncAdapter;
	}

	private static void performSync(final Context context, final Account account, final Bundle extras, final String authority, final ContentProviderClient provider, final SyncResult syncResult)
			throws OperationCanceledException {
		mContentResolver = context.getContentResolver();
		Log.i(TAG, "performSync: " + account.toString());
		// This is where the magic will happen!
	}
}