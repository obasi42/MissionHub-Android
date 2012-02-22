package com.missionhub.api.convert;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.model.GOrganization;
import com.missionhub.api.model.sql.DaoSession;
import com.missionhub.api.model.sql.Organization;
import com.missionhub.api.model.sql.OrganizationDao;
import com.missionhub.broadcast.OrganizationBroadcast;

public class OrganizationJsonSql {

	public static void update(final Context context, final GOrganization[] organizations, final String... categories) {
		update(context, organizations, true, true, categories);
	}

	public static void update(final Context context, final GOrganization[] organizations, final boolean threaded, final boolean notify, final String... categories) {
		try {
			privateUpdate(context, organizations, threaded, notify, categories);
		} catch (final Exception e) {
			long[] orgs = new long[organizations.length];
			for (int i = 0; i < organizations.length; i++) {
				orgs[i] = organizations[i].getId();
			}
			OrganizationBroadcast.broadcastError(context, orgs, e, categories);
		}
	}

	private static void privateUpdate(final Context context, final GOrganization[] organizations, final boolean threaded, final boolean notify, final String... categories)
			throws Exception {
		if (threaded) {
			new Thread(new Runnable() {
				@Override public void run() {
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

		for (final GOrganization organization : organizations) {
			Organization org = od.load(organization.getId());

			if (org == null) {
				org = new Organization();
			} else {
				org.refresh();
			}

			org.setId(organization.getId());

			if (organization.getName() != null) {
				org.setName(organization.getName());
			}

			if (organization.getAncestry() != null) {
				org.setAncestry(organization.getAncestry());
			}

			if (organization.getKeywords() != null) {
				KeywordJsonSql.update(context, organization.getId(), organization.getKeywords(), false, false, categories);
			}

			if (organization.getLeaders() != null) {
				PersonJsonSql.update(context, organization.getLeaders(), false, false, categories);
			}

			orgs.add(org);
			organizationIds.add(organization.getId());
		}

		session.runInTx(new Runnable() {
			@Override public void run() {
				for (final Organization org : orgs) {
					session.insertOrReplace(org);
				}
			}
		});

		if (notify) {
			final long[] orgIds = new long[organizationIds.size()];
			for (int i = 0; i < organizationIds.size(); i++) {
				OrganizationBroadcast.broadcastUpdate(context, organizationIds.get(i), categories);
				orgIds[i] = organizationIds.get(i);
			}
			OrganizationBroadcast.broadcastComplete(context, orgIds, categories);
		}
	}
}