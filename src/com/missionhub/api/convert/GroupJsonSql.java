package com.missionhub.api.convert;

import java.util.ArrayList;

import android.content.Context;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.model.GGroup;
import com.missionhub.api.model.sql.DaoSession;
import com.missionhub.api.model.sql.Group;
import com.missionhub.api.model.sql.GroupDao;
import com.missionhub.broadcast.GenericCUDEBroadcast;
import com.missionhub.util.U;

public class GroupJsonSql {

	public static void update(final Context context, final GGroup[] groups, final String... categories) {
		update(context, groups, true, true, categories);
	}

	public static void update(final Context context, final GGroup[] groups, final boolean threaded, final boolean notify, final String... categories) {
		try {
			privateUpdate(context, groups, threaded, notify, categories);
		} catch (final Exception e) {
			if (notify) {
				final long[] ids = new long[groups.length];
				for (int i = 0; i < groups.length; i++) {
					ids[i] = groups[i].getId();
				}
				GenericCUDEBroadcast.broadcastError(context, Group.class, ids, e, categories);
			}
		}
	}

	private static void privateUpdate(final Context context, final GGroup[] groups, final boolean threaded, final boolean notify, final String... categories) throws Exception {
		if (threaded) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// call update again, only this time we are in a thread, so
					// set threaded=false
					update(context, groups, false, notify, categories);
				}
			}).start();
			return;
		}

		final MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		final DaoSession session = app.getDbSession();
		final GroupDao gd = session.getGroupDao();

		final ArrayList<Group> gs = new ArrayList<Group>();

		final ArrayList<Long> createdGroupIds = new ArrayList<Long>();
		final ArrayList<Long> updatedGroupIds = new ArrayList<Long>();
		for (final GGroup group : groups) {
			Group g = gd.load(group.getId());

			if (g == null) {
				g = new Group();
				createdGroupIds.add(group.getId());
			} else {
				g.refresh();
				updatedGroupIds.add(group.getId());
			}

			g.setId(group.getId());

			g.setOrganization_id(group.getOrganization_id());

			if (group.getName() != null) {
				g.setName(group.getName());
			}

			if (group.getCreated_at() != null) {
				g.setCreated_at(U.getDateFromUTCString(group.getCreated_at()));
			}

			if (group.getUpdated_at() != null) {
				g.setUpdated_at(U.getDateFromUTCString(group.getCreated_at()));
			}

			g.setStart_time(group.getStart_time());
			g.setEnd_time(group.getEnd_time());

			if (group.getLabels() != null && group.getLabels().length > 0) {
				GroupLabelJsonSql.update(context, group.getId(), group.getLabels(), false, notify, categories);
			}

			if (group.getLocation() != null) {
				g.setLocation(group.getLocation());
			}

			if (group.getMeets() != null) {
				g.setMeets(group.getMeets());
			}

			gs.add(g);
		}

		session.runInTx(new Runnable() {
			@Override
			public void run() {
				for (final Group g : gs) {
					gd.insertOrReplace(g);
				}
			}
		});

		if (notify) {
			GenericCUDEBroadcast.broadcastCreate(context, Group.class, createdGroupIds, categories);
			GenericCUDEBroadcast.broadcastUpdate(context, Group.class, updatedGroupIds, categories);
		}
	}
}