package com.missionhub.api.convert;

import android.content.Context;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.model.GOrgGeneric;
import com.missionhub.api.model.sql.Organization;
import com.missionhub.api.model.sql.OrganizationDao;
import com.missionhub.api.model.sql.OrganizationalRole;
import com.missionhub.api.model.sql.OrganizationalRoleDao;
import com.missionhub.api.model.sql.OrganizationalRoleDao.Properties;
import com.missionhub.broadcast.OrganizationBroadcast;

import de.greenrobot.dao.CloseableListIterator;
import de.greenrobot.dao.LazyList;

public class OrganizationRoleJsonSql {

	public static void update(final Context context, final long personId, final GOrgGeneric[] roles, final String... categories) {
		update(context, personId, roles, true, true, categories);
	}

	public static void update(final Context context, final long personId, final GOrgGeneric[] roles, final boolean threaded, final boolean notify, final String... categories) {
		try {
			privateUpdate(context, personId, roles, threaded, notify, categories);
		} catch (final Exception e) {
			// TODO:
			// long personId = -1;
			// if (person != null) {
			// personId = person.getId();
			// }
			// PersonJsonSqlBroadcast.broadcastError(context, personId, e,
			// categories);
		}
	}

	private static void privateUpdate(final Context context, final long personId, final GOrgGeneric[] roles, final boolean threaded, final boolean notify,
			final String... categories) throws Exception {
		if (threaded) {
			new Thread(new Runnable() {
				@Override public void run() {
					// call update again, only this time we are in a thread, so
					// set threaded=false
					update(context, personId, roles, false, notify, categories);
				}
			}).start();
			return;
		}

		final MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		final OrganizationalRoleDao ord = app.getDbSession().getOrganizationalRoleDao();
		final OrganizationDao od = app.getDbSession().getOrganizationDao();

		// delete current roles in db
		final LazyList<OrganizationalRole> currentRoles = ord.queryBuilder().where(Properties.Person_id.eq(personId)).listLazy();
		final CloseableListIterator<OrganizationalRole> itr = currentRoles.listIteratorAutoClose();
		while (itr.hasNext()) {
			final OrganizationalRole role = itr.next();
			ord.delete(role);
			// broadcast this?
		}

		// Insert new roles
		for (final GOrgGeneric role : roles) {
			final OrganizationalRole or = new OrganizationalRole();
			or.setName(role.getName());
			or.setOrganization_id(role.getOrg_id());
			or.setPerson_id(personId);
			if (role.getPrimary() != null) {
				or.setPrimary(Boolean.parseBoolean(role.getPrimary()));
			}
			or.setRole(role.getRole());
			ord.insert(or);
			// broadcast this?

			// insert organization stub
			Organization org = od.load(role.getOrg_id());
			boolean newOrg = false;
			if (org == null) {
				org = new Organization();
				newOrg = true;
			}
			org.setId(role.getOrg_id());
			org.setName(role.getName());
			final long id2 = od.insertOrReplace(org);
			if (newOrg) {
				OrganizationBroadcast.broadcastCreate(context, id2, categories);
			}
			OrganizationBroadcast.broadcastUpdate(context, id2, categories);
		}
	}
}