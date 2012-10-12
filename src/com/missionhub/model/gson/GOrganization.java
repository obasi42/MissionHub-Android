package com.missionhub.model.gson;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

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

	public FutureTask<Organization> save() {
		return save(true);
	}

	/**
	 * Saves the Organization object to the local database.
	 * 
	 * @param execute
	 *            true if the task should be automatically executed, false otherwise
	 * @return
	 */
	public FutureTask<Organization> save(final boolean threaded) {
		final FutureTask<Organization> task = new FutureTask<Organization>(new Callable<Organization>() {
			@Override
			public Organization call() throws Exception {

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
							person.save(false);
						}

						for (final GKeyword keyword : keywords) {
							keyword.save(id, false);
						}

						od.insertOrReplace(org);

						Application.postEvent(new OrganizationUpdatedEvent(org));

						return org;
					}
				};
				if (threaded) {
					// since we are executing this, it is safe to assume it is the top call,
					// so we should wrap it in a transaction for performance
					return Application.getDb().callInTx(callable);
				} else {
					return callable.call();
				}
			}
		});

		if (threaded) {
			Application.getExecutor().execute(task);
		} else {
			task.run();
		}

		return task;
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