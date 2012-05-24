package com.missionhub.android.api.old.convert;

import java.util.ArrayList;

import android.content.Context;

import com.missionhub.android.api.old.model.GIdNameProvider;
import com.missionhub.android.api.old.model.sql.DaoSession;
import com.missionhub.android.api.old.model.sql.Interest;
import com.missionhub.android.api.old.model.sql.InterestDao;
import com.missionhub.android.api.old.model.sql.InterestDao.Properties;
import com.missionhub.android.app.MissionHubApplication;
import com.missionhub.android.broadcast.GenericCUDEBroadcast;

import de.greenrobot.dao.CloseableListIterator;
import de.greenrobot.dao.LazyList;

public class InterestJsonSql {

	public static void update(final Context context, final long personId, final GIdNameProvider[] interests, final String... categories) {
		update(context, personId, interests, true, true, categories);
	}

	public static void update(final Context context, final long personId, final GIdNameProvider[] interests, final boolean threaded, final boolean notify,
			final String... categories) {

		try {
			privateUpdate(context, personId, interests, threaded, notify, categories);
		} catch (final Exception e) {
			if (notify) {
				GenericCUDEBroadcast.broadcastError(context, Interest.class, -1, e, categories);
			}
		}
	}

	private static void privateUpdate(final Context context, final long personId, final GIdNameProvider[] interests, final boolean threaded, final boolean notify,
			final String... categories) throws Exception {
		if (threaded) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// call update again, only this time we are in a thread, so
					// set threaded=false
					update(context, personId, interests, false, notify, categories);
				}
			}).start();
			return;
		}

		final MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		final DaoSession session = app.getDbSession();
		final InterestDao id = session.getInterestDao();

		app.getDbSession().runInTx(new Runnable() {
			@Override
			public void run() {
				// delete current interests in db
				final LazyList<Interest> currentInterests = id.queryBuilder().where(Properties.Person_id.eq(personId)).listLazyUncached();
				final CloseableListIterator<Interest> itr = currentInterests.listIteratorAutoClose();
				final ArrayList<Long> deletedIds = new ArrayList<Long>();
				while (itr.hasNext()) {
					final Interest interest = itr.next();
					deletedIds.add(interest.getId());
					session.delete(interest);
				}
				if (notify) {
					GenericCUDEBroadcast.broadcastDelete(context, Interest.class, deletedIds, categories);
				}

				// insert interests
				final ArrayList<Long> createdIds = new ArrayList<Long>();
				for (final GIdNameProvider interest : interests) {
					final Interest i = new Interest();
					i.setCategory(interest.getCategory());
					i.setInterest_id(interest.getId());
					i.setName(interest.getName());
					i.setPerson_id(personId);
					i.setProvider(interest.getProvider());
					createdIds.add(session.insert(i));
				}
				if (notify) {
					GenericCUDEBroadcast.broadcastCreate(context, Interest.class, createdIds, categories);
				}
			}
		});
	}
}