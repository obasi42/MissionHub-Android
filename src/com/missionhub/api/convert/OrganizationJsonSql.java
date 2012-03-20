package com.missionhub.api.convert;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.model.GOrganization;
import com.missionhub.api.model.sql.DaoSession;
import com.missionhub.api.model.sql.Organization;
import com.missionhub.api.model.sql.OrganizationDao;
import com.missionhub.broadcast.GenericCUDEBroadcast;

public class OrganizationJsonSql {

	public static void update(final Context context, final GOrganization[] organizations, final String... categories) {
		update(context, organizations, true, true, categories);
	}

	public static void update(final Context context, final GOrganization[] organizations, final boolean threaded, final boolean notify, final String... categories) {
		try {
			privateUpdate(context, organizations, threaded, notify, categories);
		} catch (final Exception e) {
			if (notify) {
				final long[] ids = new long[organizations.length];
				for (int i = 0; i < organizations.length; i++) {
					ids[i] = organizations[i].getId();
				}
				GenericCUDEBroadcast.broadcastError(context, Organization.class, ids, e, categories);
			}
		}
	}

	private static void privateUpdate(final Context context, final GOrganization[] organizations, final boolean threaded, final boolean notify, final String... categories)
			throws Exception {
		if (threaded) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// call update again, only this time we are in a thread, so
					// set threaded=false
					update(context, organizations, false, notify, categories);
				}
			}).start();
			return;
		}

		final MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		final DaoSession session = app.getDbSession();
		final OrganizationDao od = session.getOrganizationDao();

		final List<Long> organizationIds = new ArrayList<Long>();
		final List<Organization> orgs = new ArrayList<Organization>();

		final ArrayList<Long> createdIds = new ArrayList<Long>();
		final ArrayList<Long> updatedIds = new ArrayList<Long>();

		for (final GOrganization organization : organizations) {
			Organization org = od.load(organization.getId());

			if (org == null) {
				org = new Organization();
				createdIds.add(organization.getId());
			} else {
				org.refresh();
				updatedIds.add(organization.getId());
			}

			org.setId(organization.getId());

			if (organization.getName() != null) {
				org.setName(organization.getName());
			}

			if (organization.getAncestry() != null) {
				org.setAncestry(organization.getAncestry());
			}

			if (organization.getKeywords() != null) {
				KeywordJsonSql.update(context, organization.getId(), organization.getKeywords(), false, notify, categories);
			}

			if (organization.getLeaders() != null) {
				PersonJsonSql.update(context, organization.getLeaders(), false, notify, categories);
			}

			orgs.add(org);
			organizationIds.add(organization.getId());
		}

		session.runInTx(new Runnable() {
			@Override
			public void run() {
				for (final Organization org : orgs) {
					session.insertOrReplace(org);
				}
			}
		});

		if (notify) {
			GenericCUDEBroadcast.broadcastCreate(context, Organization.class, createdIds, categories);
			GenericCUDEBroadcast.broadcastUpdate(context, Organization.class, updatedIds, categories);
		}
	}
}