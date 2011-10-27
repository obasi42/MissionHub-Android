package com.missionhub.sql.convert;

import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.missionhub.Application;
import com.missionhub.api.json.GIdNameProvider;
import com.missionhub.sql.Location;
import com.missionhub.sql.LocationDao;
import com.missionhub.sql.LocationDao.Properties;

public class LocationJsonSql {
	
	public static void update(Context context, int personId, GIdNameProvider location) {
		if (location == null) return;
		
		Application app = (Application) context.getApplicationContext();
		LocationDao ld = app.getDbSession().getLocationDao();
		
		// Delete current location
		List<Location> currentLocation = ld.queryBuilder().where(Properties.Person_id.eq(personId)).list();
		Iterator<Location> itr = currentLocation.iterator();
		while(itr.hasNext()) {
			ld.delete(itr.next());
		}
		
		Location l = new Location();
		l.setLocation_id(location.getId());
		l.setName(location.getName());
		l.setPerson_id(personId);
		l.setProvider(location.getProvider());
		ld.insert(l);
	}
}