package com.missionhub.sql.convert;

import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.json.GEducation;
import com.missionhub.sql.Education;
import com.missionhub.sql.EducationDao;
import com.missionhub.sql.EducationDao.Properties;

public class EducationJsonSql {
	
	public static void update(Context context, int personId, GEducation[] educations) {
		if (educations == null) return;
		
		MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		EducationDao ed = app.getDbSession().getEducationDao();
		
		// Delete all current stored educations for this person
		List<Education> currentEducation = ed.queryBuilder().where(Properties.Person_id.eq(personId)).list();
		Iterator<Education> itr = currentEducation.iterator();
		while(itr.hasNext()) {
			ed.delete(itr.next());
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
			ed.insert(edu);
		}
	}
}