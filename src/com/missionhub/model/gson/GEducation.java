package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Education;
import com.missionhub.model.EducationDao;
import com.missionhub.model.Person;
import com.missionhub.util.U;

import de.greenrobot.dao.CloseableListIterator;
import de.greenrobot.dao.LazyList;

public class GEducation {
	public String type;
	public GIdNameProvider school;
	public GIdNameProvider year;
	public String provider;
	public GIdNameProvider[] concentration;
	public GIdNameProvider degree;

	/**
	 * Saves the education for a person This is not run in a transaction and should be wrapped by the calling method to
	 * ensure decent performance.
	 * 
	 * @param education
	 * @param p
	 */
	public static void save(final GEducation[] education, final Person p) {
		final EducationDao ed = Application.getDb().getEducationDao();

		// delete current records as we don't have stable id's currently
		final LazyList<Education> delEdus = ed.queryBuilder().where(com.missionhub.model.EducationDao.Properties.Person_id.eq(p.getId())).listLazyUncached();
		final CloseableListIterator<Education> itr = delEdus.listIteratorAutoClose();
		while (itr.hasNext()) {
			final Education edu = itr.next();
			ed.delete(edu);
		}

		// insert the new records
		for (final GEducation edu : education) {
			if (edu == null) continue;

			final Education e = new Education();

			if (!U.isNullEmptyNegative(p, p.getId())) e.setPerson_id(p.getId());
			if (!U.isNullEmpty(edu.provider)) e.setProvider(edu.provider);
			if (!U.isNullEmpty(edu.type)) e.setType(edu.type);
			if (!U.isNullEmpty(edu.school)) {
				if (!U.isNullEmpty(edu.school.id)) e.setSchool_id(edu.school.id);
				if (!U.isNullEmpty(edu.school.name)) e.setSchool_name(edu.school.name);
			}
			if (!U.isNullEmpty(edu.year)) {
				if (!U.isNullEmpty(edu.year.id)) e.setYear_id(edu.year.id);
				if (!U.isNullEmpty(edu.year.name)) e.setYear_name(edu.year.name);
			}

			ed.insertOrReplace(e);
		}
	}
}
