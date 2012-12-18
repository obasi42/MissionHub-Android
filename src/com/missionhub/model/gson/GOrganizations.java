package com.missionhub.model.gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.Organization;

public class GOrganizations {

	GOrganization[] organizations;

	/**
	 * Saves the organizations to the SQLite database.
	 * 
	 * @param inTx
	 * @return
	 * @throws Exception
	 */
	public List<Organization> save(final boolean inTx) throws Exception {
		final Callable<List<Organization>> callable = new Callable<List<Organization>>() {
			@Override
			public List<Organization> call() throws Exception {
				final List<Organization> orgs = new ArrayList<Organization>();
				for (final GOrganization organization : organizations) {
					final Organization org = organization.save(true);
					if (org != null) {
						orgs.add(org);
					}
				}
				return orgs;
			}
		};
		if (inTx) {
			return callable.call();
		} else {
			return Application.getDb().callInTx(callable);
		}
	}

}