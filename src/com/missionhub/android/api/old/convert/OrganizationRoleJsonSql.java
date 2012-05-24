package com.missionhub.android.api.old.convert;

import java.util.ArrayList;

import android.content.Context;

import com.missionhub.android.api.old.model.GOrgGeneric;
import com.missionhub.android.api.old.model.sql.DaoSession;
import com.missionhub.android.api.old.model.sql.Organization;
import com.missionhub.android.api.old.model.sql.OrganizationDao;
import com.missionhub.android.api.old.model.sql.OrganizationalRole;
import com.missionhub.android.api.old.model.sql.OrganizationalRoleDao;
import com.missionhub.android.api.old.model.sql.OrganizationalRoleDao.Properties;
import com.missionhub.android.app.MissionHubApplication;
import com.missionhub.android.broadcast.GenericCUDEBroadcast;

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
			if (notify) {
				GenericCUDEBroadcast.broadcastError(context, OrganizationalRole.class, -1, e, categories);
			}
		}
	}

	private static void privateUpdate(final Context context, final long personId, final GOrgGeneric[] roles, final boolean threaded, final boolean notify,
			final String... categories) throws Exception {
		if (threaded) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					update(context, personId, roles, false, notify, categories);
				}
			}).start();
			return;
		}

		final MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		final DaoSession session = app.getDbSession();
		final OrganizationalRoleDao ord = session.getOrganizationalRoleDao();
		final OrganizationDao od = session.getOrganizationDao();

		session.runInTx(new Runnable() {
			@Override
			public void run() {

				// delete current roles in db
				final LazyList<OrganizationalRole> currentRoles = ord.queryBuilder().where(Properties.Person_id.eq(personId)).listLazyUncached();
				final CloseableListIterator<OrganizationalRole> itr = currentRoles.listIteratorAutoClose();
				final ArrayList<Long> deletedIds = new ArrayList<Long>();
				while (itr.hasNext()) {
					final OrganizationalRole or = itr.next();
					deletedIds.add(or.getId());
					session.delete(or);
				}
				if (notify) {
					GenericCUDEBroadcast.broadcastDelete(context, OrganizationalRole.class, deletedIds, categories);
				}

				// insert new roles
				final ArrayList<Long> createdIds = new ArrayList<Long>();
				for (final GOrgGeneric role : roles) {
					final OrganizationalRole or = new OrganizationalRole();
					or.setOrganization_id(role.getOrg_id());
					or.setPerson_id(personId);
					if (role.getPrimary() != null) {
						or.setPrimary(Boolean.parseBoolean(role.getPrimary()));
					}
					or.setRole(role.getRole());
					createdIds.add(session.insert(or));

					// insert organization stub
					Organization org = od.load(role.getOrg_id());
					if (org == null) {
						org = new Organization();
					}

					if (role.getOrg_id() != null) {
						org.setId(role.getOrg_id());
					}

					if (role.getName() != null) {
						org.setName(role.getName());
					}
					session.insertOrReplace(org);
				}
				if (notify) {
					GenericCUDEBroadcast.broadcastCreate(context, OrganizationalRole.class, createdIds, categories);
				}
			}
		});
	}
}