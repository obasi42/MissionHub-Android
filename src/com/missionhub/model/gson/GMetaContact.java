package com.missionhub.model.gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.missionhub.application.Application;
import com.missionhub.model.Person;

public class GMetaContact {
	public GMeta meta;
	public GContact[] contacts;

	public FutureTask<List<Person>> save() {
		return save(true);
	}

	public FutureTask<List<Person>> save(final boolean threaded) {
		final FutureTask<List<Person>> task = new FutureTask<List<Person>>(new Callable<List<Person>>() {
			@Override
			public List<Person> call() throws Exception {

				final Callable<List<Person>> callable = new Callable<List<Person>>() {
					@Override
					public List<Person> call() throws Exception {
						final List<Person> people = new ArrayList<Person>();

						for (final GContact contact : contacts) {
							people.add(contact.save(false).get());
						}

						return people;
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
