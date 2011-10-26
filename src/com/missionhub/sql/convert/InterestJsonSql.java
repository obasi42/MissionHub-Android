package com.missionhub.sql.convert;

import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.json.GIdNameProvider;
import com.missionhub.sql.Interest;
import com.missionhub.sql.InterestDao;
import com.missionhub.sql.InterestDao.Properties;

public class InterestJsonSql {
	
	public static void update(Context context, int personId, GIdNameProvider[] interests) {
		if (interests == null) return;
		
		MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		InterestDao id = app.getDbSession().getInterestDao();
		
		// Delete all current stored interests for this contact
		List<Interest> currentInterests = id.queryBuilder().where(Properties.Person_id.eq(personId)).list();
		Iterator<Interest> itr = currentInterests.iterator();
		while(itr.hasNext()) {
			id.delete(itr.next());
		}
		
		// Insert interests
		for (GIdNameProvider interest : interests) {
			Interest i = new Interest();
			i.setCategory(interest.getCategory());
			i.setInterest_id(interest.getId());
			i.setName(interest.getName());
			i.setPerson_id(personId);
			i.setProvider(interest.getProvider());
			id.insert(i);
		}
	}
}