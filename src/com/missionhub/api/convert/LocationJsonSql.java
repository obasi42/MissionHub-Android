package com.missionhub.api.convert;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.Bundle;

import com.missionhub.Application;
import com.missionhub.api.ApiNotifier;
import com.missionhub.api.model.json.GIdNameProvider;
import com.missionhub.api.model.sql.Location;
import com.missionhub.api.model.sql.LocationDao;
import com.missionhub.api.model.sql.LocationDao.Properties;

public class LocationJsonSql {
	
	public static void update(Context context, int personId, GIdNameProvider location) {
		update(context, personId, location, null);
	}
	
	public static void update(Context context, int personId, GIdNameProvider location, String tag) {
		if (location == null) return;
		
		Application app = (Application) context.getApplicationContext();
		LocationDao ld = app.getDbSession().getLocationDao();
		
		// Delete current location
		List<Location> currentLocation = ld.queryBuilder().where(Properties.Person_id.eq(personId)).list();
		Iterator<Location> itr = currentLocation.iterator();
		while(itr.hasNext()) {
			Location l = itr.next();
			ld.delete(l);
			
			Bundle b = new Bundle();
			b.putLong("id", l.get_id());
			b.putInt("personId", l.getPerson_id());
			if (tag != null) b.putString("tag", tag);
			app.getApiNotifier().postMessage(ApiNotifier.Type.DELETE_LOCATION, b);
		}
		
		Location l = new Location();
		l.setLocation_id(location.getId());
		l.setName(location.getName());
		l.setPerson_id(personId);
		l.setProvider(location.getProvider());
		long id = ld.insert(l);
		
		Bundle b = new Bundle();
		b.putLong("id", id);
		b.putInt("personId", l.getPerson_id());
		if (tag != null) b.putString("tag", tag);
		app.getApiNotifier().postMessage(ApiNotifier.Type.UPDATE_LOCATION, b);
	}
}