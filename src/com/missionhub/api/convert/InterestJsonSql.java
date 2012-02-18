package com.missionhub.api.convert;

import android.content.Context;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.model.GIdNameProvider;
import com.missionhub.api.model.sql.Interest;
import com.missionhub.api.model.sql.InterestDao;
import com.missionhub.api.model.sql.InterestDao.Properties;

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
			// TODO:
		}
	}

	private static void privateUpdate(final Context context, final long personId, final GIdNameProvider[] interests, final boolean threaded, final boolean notify,
			final String... categories) throws Exception {
		if (threaded) {
			new Thread(new Runnable() {
				@Override public void run() {
					// call update again, only this time we are in a thread, so
					// set threaded=false
					update(context, personId, interests, false, notify, categories);
				}
			}).start();
			return;
		}

		final MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		final InterestDao id = app.getDbSession().getInterestDao();

		// delete current interests in db
		final LazyList<Interest> currentInterests = id.queryBuilder().where(Properties.Person_id.eq(personId)).listLazy();
		final CloseableListIterator<Interest> itr = currentInterests.listIteratorAutoClose();
		while (itr.hasNext()) {
			final Interest interest = itr.next();
			id.delete(interest);
		}

		// insert interests
		for (final GIdNameProvider interest : interests) {
			final Interest i = new Interest();
			i.setCategory(interest.getCategory());
			i.setInterest_id(interest.getId());
			i.setName(interest.getName());
			i.setPerson_id(personId);
			i.setProvider(interest.getProvider());
			id.insert(i);
		}
	}
}