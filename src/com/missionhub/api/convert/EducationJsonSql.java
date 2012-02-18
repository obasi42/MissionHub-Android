package com.missionhub.api.convert;

import android.content.Context;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.model.GEducation;
import com.missionhub.api.model.sql.Education;
import com.missionhub.api.model.sql.EducationDao;
import com.missionhub.api.model.sql.EducationDao.Properties;

import de.greenrobot.dao.CloseableListIterator;
import de.greenrobot.dao.LazyList;

public class EducationJsonSql {

	public static void update(final Context context, final long personId, final GEducation[] educations, final String... categories) {
		update(context, personId, educations, true, true, categories);
	}

	public static void update(final Context context, final long personId, final GEducation[] educations, final boolean threaded, final boolean notify,
			final String... categories) {
		
		try {
			privateUpdate(context, personId, educations, threaded, notify, categories);
		} catch (final Exception e) {
			// TODO:
		}
	}

	private static void privateUpdate(final Context context, final long personId, final GEducation[] educations, final boolean threaded, final boolean notify,
			final String... categories) throws Exception {
		if (threaded) {
			new Thread(new Runnable() {
				@Override public void run() {
					// call update again, only this time we are in a thread, so
					// set threaded=false
					update(context, personId, educations, false, notify, categories);
				}
			}).start();
			return;
		}

		final MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		final EducationDao ed = app.getDbSession().getEducationDao();

		// delete current educations
		final LazyList<Education> currentEducation = ed.queryBuilder().where(Properties.Person_id.eq(personId)).listLazy();
		final CloseableListIterator<Education> itr = currentEducation.listIteratorAutoClose();
		while (itr.hasNext()) {
			final Education edu = itr.next();
			ed.delete(edu);
		}

		// insert new educations
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