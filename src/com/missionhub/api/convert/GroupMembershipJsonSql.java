package com.missionhub.api.convert;

import java.util.ArrayList;

import android.content.Context;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.model.GGroupMembership;
import com.missionhub.api.model.sql.DaoSession;
import com.missionhub.api.model.sql.GroupMembership;
import com.missionhub.api.model.sql.GroupMembershipDao;
import com.missionhub.api.model.sql.GroupMembershipDao.Properties;
import com.missionhub.broadcast.GenericCUDEBroadcast;

import de.greenrobot.dao.CloseableListIterator;
import de.greenrobot.dao.LazyList;

public class GroupMembershipJsonSql {

	public static void update(final Context context, final long personId, final GGroupMembership[] memberships, final String... categories) {
		update(context, personId, memberships, true, true, categories);
	}

	public static void update(final Context context, final long personId, final GGroupMembership[] memberships, final boolean threaded, final boolean notify,
			final String... categories) {
		try {
			privateUpdate(context, personId, memberships, threaded, notify, categories);
		} catch (final Exception e) {
			if (notify) {
				GenericCUDEBroadcast.broadcastError(context, GroupMembership.class, -1, e, categories);
			}
		}
	}

	private static void privateUpdate(final Context context, final long personId, final GGroupMembership[] memberships, final boolean threaded, final boolean notify,
			final String... categories) throws Exception {
		if (threaded) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// call update again, only this time we are in a thread, so
					// set threaded=false
					update(context, personId, memberships, false, notify, categories);
				}
			}).start();
			return;
		}

		final MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		final DaoSession session = app.getDbSession();
		final GroupMembershipDao gmd = session.getGroupMembershipDao();

		session.runInTx(new Runnable() {
			@Override
			public void run() {
				// delete current memberships in db
				final LazyList<GroupMembership> currentMemberships = gmd.queryBuilder().where(Properties.Person_id.eq(personId)).listLazyUncached();
				final CloseableListIterator<GroupMembership> itr = currentMemberships.listIteratorAutoClose();
				final ArrayList<Long> deletedIds = new ArrayList<Long>();
				while (itr.hasNext()) {
					final GroupMembership gm = itr.next();
					deletedIds.add(gm.getId());
					session.delete(gm);
				}
				if (notify) {
					GenericCUDEBroadcast.broadcastDelete(context, GroupMembership.class, deletedIds, categories);
				}

				// insert new memberships
				final ArrayList<Long> createIds = new ArrayList<Long>();
				for (final GGroupMembership membership : memberships) {
					final GroupMembership gm = new GroupMembership();
					gm.setPerson_id(personId);
					gm.setGroup_id(membership.getGroup_id());
					gm.setName(membership.getName());
					gm.setRole(membership.getRole());
					createIds.add(session.insert(gm));
				}
				if (notify) {
					GenericCUDEBroadcast.broadcastCreate(context, GroupMembership.class, deletedIds, categories);
				}
			}
		});
	}
}