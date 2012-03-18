package com.missionhub.api.convert;

import android.content.Context;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.model.GMetaMeta;
import com.missionhub.broadcast.GenericSEBroadcast;
import com.missionhub.broadcast.GenericSEBroadcast.Type;

public class MetaJsonSql {

	public static void update(final Context context, final GMetaMeta meta, final String... categories) {
		update(context, meta, true, true, categories);
	}

	public static void update(final Context context, final GMetaMeta meta, final boolean threaded, final boolean notify, final String... categories) {
		try {
			privateUpdate(context, meta, threaded, notify, categories);
		} catch (final Exception e) {
			if (notify) {
				GenericSEBroadcast.broadcastError(context, Type.MetaJsonSql, e, categories);
			}
		}
	}

	private static void privateUpdate(final Context context, final GMetaMeta meta, final boolean threaded, final boolean notify, final String... categories) throws Exception {
		if (threaded) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					update(context, meta, false, notify, categories);
				}
			}).start();
			return;
		}

		final MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();

		OrganizationJsonSql.update(context, meta.getOrganizations(), false, true);

		// GroupJsonSql.update(context, meta.getGroups(), false, true);

		PersonJsonSql.update(context, meta.getPerson(), false, true);

		if (notify) {
			GenericSEBroadcast.broadcastSuccess(context, Type.MetaJsonSql, categories);
		}
	}
}