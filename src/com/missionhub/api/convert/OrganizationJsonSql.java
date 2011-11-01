package com.missionhub.api.convert;

import com.missionhub.Application;
import com.missionhub.api.ApiNotifier;
import com.missionhub.api.model.json.GOrganization;
import com.missionhub.api.model.json.GPerson;
import com.missionhub.api.model.sql.Organization;
import com.missionhub.api.model.sql.OrganizationDao;

import android.content.Context;
import android.os.Bundle;

public class OrganizationJsonSql {

	public static void update(Context context, GOrganization organization) {
		update(context, organization, null);
	}

	public static void update(final Context context, final GOrganization organization, final String tag) {
		if (organization == null)
			return;

		Thread t = new Thread(new Runnable() {
			public void run() {
				Application app = (Application) context.getApplicationContext();
				OrganizationDao od = app.getDbSession().getOrganizationDao();

				Organization org = od.load(organization.getId());
				if (org == null)
					org = new Organization();
				org.set_id(organization.getId());
				if (organization.getName() != null) {
					org.setName(organization.getName());
				}
				if (organization.getAncestry() != null) {
					org.setAncestry(organization.getAncestry());
				}

				KeywordJsonSql.update(context, organization.getId(), organization.getKeywords(), tag);

				for (GPerson leader : organization.getLeaders()) {
					PersonJsonSql.update(context, leader, tag);
				}

				long id = od.insertOrReplace(org);

				Bundle b = new Bundle();
				b.putLong("id", id);
				if (tag != null)
					b.putString("tag", tag);
				app.getApiNotifier().postMessage(ApiNotifier.Type.UPDATE_ORGANIZATION, b);
			}
		});
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}
}