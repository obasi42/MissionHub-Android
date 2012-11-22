package com.missionhub.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.Organization;
import com.missionhub.model.OrganizationDao;
import com.missionhub.util.U;

public class GOrganization {

	public long id;
	public String name;
	public String ancestry;
	public GPerson[] leaders;
	public GKeyword[] keywords;

	/**
	 * Saves the Organization object to the local database.
	 * 
	 * @return
	 * @throws Exception
	 */
	public Organization save(final boolean inTx) throws Exception {
		final Callable<Organization> callable = new Callable<Organization>() {
			@Override
			public Organization call() throws Exception {
				final OrganizationDao od = Application.getDb().getOrganizationDao();

				Organization org = od.load(id);
				if (org == null) {
					org = new Organization();
					org.setId(id);
				}

				if (!U.isNullEmpty(name)) org.setName(name);
				if (!U.isNullEmpty(ancestry)) org.setAncestry(ancestry);

				for (final GPerson person : leaders) {
					person.save(true);
				}

				for (final GKeyword keyword : keywords) {
					keyword.save(id, true);
				}

				od.insertOrReplace(org);

				Application.postEvent(new OrganizationUpdatedEvent(org));

				return org;
			}
		};
		if (!inTx) {
			return Application.getDb().callInTx(callable);
		} else {
			return callable.call();
		}
	}

	/**
	 * Event posted when a Organization is updated
	 */
	public static class OrganizationUpdatedEvent {

		public Organization organization;

		public OrganizationUpdatedEvent(final Organization organization) {
			this.organization = organization;
		}

	}

}