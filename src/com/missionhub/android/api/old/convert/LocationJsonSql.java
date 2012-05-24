package com.missionhub.android.api.old.convert;

import java.util.ArrayList;

import android.content.Context;

import com.missionhub.android.api.old.model.GIdNameProvider;
import com.missionhub.android.api.old.model.sql.DaoSession;
import com.missionhub.android.api.old.model.sql.Location;
import com.missionhub.android.api.old.model.sql.LocationDao;
import com.missionhub.android.api.old.model.sql.LocationDao.Properties;
import com.missionhub.android.app.MissionHubApplication;
import com.missionhub.android.broadcast.GenericCUDEBroadcast;

import de.greenrobot.dao.CloseableListIterator;
import de.greenrobot.dao.LazyList;

public class LocationJsonSql {

	public static void update(final Context context, final long personId, final GIdNameProvider location, final String... categories) {
		update(context, personId, location, true, true, categories);
	}

	public static void update(final Context context, final long personId, final GIdNameProvider location, final boolean threaded, final boolean notify, final String... categories) {

		try {
			privateUpdate(context, personId, location, threaded, notify, categories);
		} catch (final Exception e) {
			if (notify) {
				GenericCUDEBroadcast.broadcastError(context, Location.class, -1, e, categories);
			}
		}
	}

	private static void privateUpdate(final Context context, final long personId, final GIdNameProvider location, final boolean threaded, final boolean notify,
			final String... categories) throws Exception {
		if (threaded) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// call update again, only this time we are in a thread, so
					// set threaded=false
					update(context, personId, location, false, notify, categories);
				}
			}).start();
			return;
		}

		final MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		final DaoSession session = app.getDbSession();
		final LocationDao ld = session.getLocationDao();

		session.runInTx(new Runnable() {
			@Override
			public void run() {
				// delete current interests in db
				final LazyList<Location> currentInterests = ld.queryBuilder().where(Properties.Person_id.eq(personId)).listLazyUncached();
				final CloseableListIterator<Location> itr = currentInterests.listIteratorAutoClose();
				final ArrayList<Long> deletedIds = new ArrayList<Long>();
				while (itr.hasNext()) {
					final Location loc = itr.next();
					deletedIds.add(loc.getId());
					session.delete(loc);
				}
				if (notify) {
					GenericCUDEBroadcast.broadcastDelete(context, Location.class, deletedIds, categories);
				}

				// insert location
				final ArrayList<Long> createdIds = new ArrayList<Long>();
				final Location l = new Location();
				l.setLocation_id(location.getId());
				l.setName(location.getName());
				l.setPerson_id(personId);
				l.setProvider(location.getProvider());
				createdIds.add(session.insert(l));
				if (notify) {
					GenericCUDEBroadcast.broadcastCreate(context, Location.class, createdIds, categories);
				}
			}
		});
	}
}