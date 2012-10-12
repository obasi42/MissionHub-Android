package com.missionhub.model.gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.missionhub.application.Application;
import com.missionhub.model.Organization;

public class GMetaOrganizations {
	public GMeta meta;
	public GOrganization[] organizations;

	public FutureTask<List<Organization>> save() {
		return save(true);
	}

	public FutureTask<List<Organization>> save(final boolean threaded) {
		final FutureTask<List<Organization>> task = new FutureTask<List<Organization>>(new Callable<List<Organization>>() {
			@Override
			public List<Organization> call() throws Exception {

				final Callable<List<Organization>> callable = new Callable<List<Organization>>() {
					@Override
					public List<Organization> call() throws Exception {
						final List<Organization> orgs = new ArrayList<Organization>();

						for (final GOrganization org : organizations) {
							orgs.add(org.save(false).get());
						}

						return orgs;
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
}
