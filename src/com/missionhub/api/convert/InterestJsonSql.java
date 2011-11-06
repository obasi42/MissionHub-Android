package com.missionhub.api.convert;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.Bundle;

import com.missionhub.Application;
import com.missionhub.api.ApiNotifier;
import com.missionhub.api.model.json.GIdNameProvider;
import com.missionhub.api.model.sql.Interest;
import com.missionhub.api.model.sql.InterestDao;
import com.missionhub.api.model.sql.InterestDao.Properties;

public class InterestJsonSql {

	public static void update(Context context, int personId, GIdNameProvider[] interests) {
		update(context, personId, interests, null);
	}

	public static void update(final Context context, final int personId, final GIdNameProvider[] interests, final String tag) {
		if (interests == null)
			return;

		Application app = (Application) context.getApplicationContext();
		InterestDao id = app.getDbSession().getInterestDao();

		// Delete all current stored interests for this contact
		List<Interest> currentInterests = id.queryBuilder().where(Properties.Person_id.eq(personId)).list();
		Iterator<Interest> itr = currentInterests.iterator();
		while (itr.hasNext()) {
			Interest i = itr.next();
			id.delete(i);

			Bundle b = new Bundle();
			b.putLong("id", i.get_id());
			b.putInt("personId", i.getPerson_id());
			if (tag != null)
				b.putString("tag", tag);
			app.getApiNotifier().postMessage(ApiNotifier.Type.DELETE_INTEREST, b);
		}

		// Insert interests
		for (GIdNameProvider interest : interests) {
			Interest i = new Interest();
			i.setCategory(interest.getCategory());
			i.setInterest_id(interest.getId());
			i.setName(interest.getName());
			i.setPerson_id(personId);
			i.setProvider(interest.getProvider());
			long id2 = id.insert(i);

			Bundle b = new Bundle();
			b.putLong("id", id2);
			b.putInt("personId", i.getPerson_id());
			if (tag != null)
				b.putString("tag", tag);
			app.getApiNotifier().postMessage(ApiNotifier.Type.UPDATE_INTEREST, b);
		}

	}
}