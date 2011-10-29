package com.missionhub.sql.convert;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.Bundle;

import com.missionhub.Application;
import com.missionhub.api.ApiNotifier;
import com.missionhub.model.json.GEducation;
import com.missionhub.model.sql.Education;
import com.missionhub.model.sql.EducationDao;
import com.missionhub.model.sql.EducationDao.Properties;

public class EducationJsonSql {
	
	public static void update(Context context, int personId, GEducation[] educations) {
		update(context, personId, educations, null);
	}
	
	public static void update(Context context, int personId, GEducation[] educations, String tag) {
		if (educations == null) return;
		
		Application app = (Application) context.getApplicationContext();
		EducationDao ed = app.getDbSession().getEducationDao();
		
		// Delete all current stored educations for this person
		List<Education> currentEducation = ed.queryBuilder().where(Properties.Person_id.eq(personId)).list();
		Iterator<Education> itr = currentEducation.iterator();
		while(itr.hasNext()) {
			Education edu = itr.next();
			ed.delete(edu);
			
			Bundle b = new Bundle();
			b.putLong("id", edu.get_id());
			b.putInt("personId", edu.getPerson_id());
			if (tag != null) b.putString("tag", tag);
			app.getApiNotifier().postMessage(ApiNotifier.Type.DELETE_EDUCATION, b);
		}
		
		// Insert educations
		for (GEducation education : educations) {
			Education edu = new Education();
			edu.setPerson_id(personId);
			edu.setProvider(education.getProvider());
			edu.setType(education.getType());
			if (education.getSchool() != null) {
				edu.setSchool_id(education.getSchool().getId());
				edu.setSchool_name(education.getSchool().getName());
			}
			if (education.getYear() != null) {
				edu.setYear_id(education.getYear().getId());
				edu.setYear_name(education.getYear().getName());
			}
			long id = ed.insert(edu);
			
			Bundle b = new Bundle();
			b.putLong("id", id);
			b.putInt("personId", edu.getPerson_id());
			if (tag != null) b.putString("tag", tag);
			app.getApiNotifier().postMessage(ApiNotifier.Type.DELETE_EDUCATION, b);
		}
	}
}