package com.missionhub.android.api.old.convert;

import java.util.ArrayList;

import android.content.Context;

import com.missionhub.android.api.old.model.GGroupLabel;
import com.missionhub.android.api.old.model.sql.DaoSession;
import com.missionhub.android.api.old.model.sql.GroupLabel;
import com.missionhub.android.api.old.model.sql.GroupLabelDao;
import com.missionhub.android.api.old.model.sql.GroupLabels;
import com.missionhub.android.api.old.model.sql.GroupLabelsDao;
import com.missionhub.android.api.old.model.sql.GroupLabelsDao.Properties;
import com.missionhub.android.app.MissionHubApplication;
import com.missionhub.android.broadcast.GenericCUDEBroadcast;
import com.missionhub.android.util.U;

import de.greenrobot.dao.CloseableListIterator;
import de.greenrobot.dao.LazyList;

public class GroupLabelJsonSql {

	public static void update(final Context context, final long groupId, final GGroupLabel[] labels, final String... categories) {
		update(context, groupId, labels, true, true, categories);
	}

	public static void update(final Context context, final long groupId, final GGroupLabel[] labels, final boolean threaded, final boolean notify, final String... categories) {
		try {
			privateUpdate(context, groupId, labels, threaded, notify, categories);
		} catch (final Exception e) {
			if (notify) {
				final long[] ids = new long[labels.length];
				for (int i = 0; i < labels.length; i++) {
					ids[i] = labels[i].getId();
				}
				GenericCUDEBroadcast.broadcastError(context, GroupLabel.class, ids, e, categories);
			}
		}
	}

	private static void privateUpdate(final Context context, final long groupId, final GGroupLabel[] labels, final boolean threaded, final boolean notify,
			final String... categories) throws Exception {
		if (threaded) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// call update again, only this time we are in a thread, so
					// set threaded=false
					update(context, groupId, labels, false, notify, categories);
				}
			}).start();
			return;
		}

		final MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		final DaoSession session = app.getDbSession();
		final GroupLabelDao gld = session.getGroupLabelDao();
		final GroupLabelsDao glsd = session.getGroupLabelsDao();

		session.runInTx(new Runnable() {
			@Override
			public void run() {
				// delete current GroupLabels joins for group
				final LazyList<GroupLabels> currentGroupLabels = glsd.queryBuilder().where(Properties.Group_id.eq(groupId)).listLazyUncached();
				final CloseableListIterator<GroupLabels> itr = currentGroupLabels.listIteratorAutoClose();
				final ArrayList<Long> deletedGroupLabelsIds = new ArrayList<Long>();
				while (itr.hasNext()) {
					final GroupLabels gls = itr.next();
					deletedGroupLabelsIds.add(gls.getId());
					session.delete(itr.next());
				}
				if (notify) {
					GenericCUDEBroadcast.broadcastDelete(context, GroupLabels.class, deletedGroupLabelsIds, categories);
				}

				final ArrayList<Long> createdLabelIds = new ArrayList<Long>();
				final ArrayList<Long> updatedLabelIds = new ArrayList<Long>();
				final ArrayList<Long> createdGroupLabelsIds = new ArrayList<Long>();

				// create/update labels and add GroupLabels join
				for (final GGroupLabel label : labels) {
					GroupLabel gl = gld.load(label.getId());

					if (gl == null) {
						gl = new GroupLabel();
						createdLabelIds.add(label.getId());
					} else {
						gl.refresh();
						updatedLabelIds.add(label.getId());
					}

					gl.setId(label.getId());

					if (label.getName() != null) {
						gl.setName(label.getName());
					}

					gl.setOrganization_id(label.getOrganization_id());

					if (label.getAncestry() != null) {
						gl.setAncestry(label.getAncestry());
					}

					if (label.getCreated_at() != null) {
						gl.setCreated_at(U.parseISO8601(label.getCreated_at()));
					}

					if (label.getUpdated_at() != null) {
						gl.setUpdated_at(U.parseISO8601(label.getUpdated_at()));
					}

					gl.setGroup_labelings_count(label.getGroup_labelings_count());

					gld.insertOrReplace(gl);

					final GroupLabels gls = new GroupLabels();
					gls.setGroup_id(groupId);
					gls.setLabel_id(label.getId());
					createdGroupLabelsIds.add(glsd.insert(gls));
				}
				if (notify) {
					GenericCUDEBroadcast.broadcastCreate(context, GroupLabel.class, createdLabelIds, categories);
					GenericCUDEBroadcast.broadcastUpdate(context, GroupLabel.class, updatedLabelIds, categories);
					GenericCUDEBroadcast.broadcastCreate(context, GroupLabels.class, createdGroupLabelsIds, categories);
				}
			}
		});
	}
}