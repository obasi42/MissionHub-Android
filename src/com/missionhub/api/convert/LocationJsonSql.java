package com.missionhub.api.convert;

import android.content.Context;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.model.GIdNameProvider;
import com.missionhub.api.model.sql.DaoSession;
import com.missionhub.api.model.sql.Location;
import com.missionhub.api.model.sql.LocationDao;
import com.missionhub.api.model.sql.LocationDao.Properties;

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
			// TODO:
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
				while (itr.hasNext()) {
					final Location loc = itr.next();
					session.delete(loc);
				}

				// insert location
				final Location l = new Location();
				l.setLocation_id(location.getId());
				l.setName(location.getName());
				l.setPerson_id(personId);
				l.setProvider(location.getProvider());
				session.insert(l);
			}
		});
	}
}