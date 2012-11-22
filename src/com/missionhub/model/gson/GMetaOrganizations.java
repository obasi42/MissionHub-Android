package com.missionhub.model.gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.Organization;

public class GMetaOrganizations {
	public GMeta meta;
	public GOrganization[] organizations;

	public List<Organization> save(final boolean inTx) throws Exception {
		final Callable<List<Organization>> callable = new Callable<List<Organization>>() {
			@Override
			public List<Organization> call() throws Exception {
				final List<Organization> orgs = new ArrayList<Organization>();

				for (final GOrganization org : organizations) {
					orgs.add(org.save(true));
				}

				return orgs;
			}
		};
		if (!inTx) {
			return Application.getDb().callInTx(callable);
		} else {
			return callable.call();
		}
	}
}
