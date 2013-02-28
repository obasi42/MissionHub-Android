package com.missionhub.android.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.android.application.Application;
import com.missionhub.android.model.Organization;
import com.missionhub.android.model.OrganizationDao;
import com.missionhub.android.util.U;

public class GOrganization {

	public long id;
	public String name;
	public String terminology;
	public String ancestry;
	public Boolean show_sub_orgs;
	public String status;
	public String created_at;
	public String updated_at;

	public GPerson[] contacts;
	public GPerson[] admins;
	public GPerson[] leaders;
	public GPerson[] people;
	public GSurvey[] surveys;
	public GGroup[] groups;
	public GSmsKeyword[] keywords;

	public static final Object lock = new Object();

	/**
	 * Saves the organization to the SQLite database.
	 * 
	 * @param inTx
	 * @return
	 * @throws Exception
	 */
	public Organization save(final boolean inTx) throws Exception {
		final Callable<Organization> callable = new Callable<Organization>() {
			@Override
			public Organization call() throws Exception {
				synchronized (lock) {
					final OrganizationDao dao = Application.getDb().getOrganizationDao();

					final Organization org = new Organization();
					org.setId(id);
					org.setName(name);
					org.setTerminology(terminology);
					org.setAncestry(ancestry);
					org.setShow_sub_orgs(show_sub_orgs);
					org.setStatus(status);
					org.setCreated_at(U.parseISO8601(created_at));
					org.setUpdated_at(U.parseISO8601(updated_at));
					dao.insertOrReplace(org);

					if (contacts != null) {
						for (final GPerson person : contacts) {
							person.save(true);
						}
					}

					if (admins != null) {
						for (final GPerson person : admins) {
							person.save(true);
						}
					}

					if (leaders != null) {
						for (final GPerson person : leaders) {
							person.save(true);
						}
					}

					if (people != null) {
						for (final GPerson person : people) {
							person.save(true);
						}
					}

					if (surveys != null) {
						for (final GSurvey survey : surveys) {
							survey.save(true);
						}
					}

					if (groups != null) {
						for (final GGroup group : groups) {
							group.save(true);
						}
					}

					if (keywords != null) {
						for (final GSmsKeyword keyword : keywords) {
							keyword.save(true);
						}
					}

					return org;
				}
			}
		};
		if (inTx) {
			return callable.call();
		} else {
			return Application.getDb().callInTx(callable);
		}
	}

}