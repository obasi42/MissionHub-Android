package com.missionhub.model.gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.Person;

public class GMetaContact {
	public GMeta meta;
	public GContact[] contacts;

	public List<Person> save(final boolean inTx) throws Exception {
		final Callable<List<Person>> callable = new Callable<List<Person>>() {
			@Override
			public List<Person> call() throws Exception {
				final List<Person> people = new ArrayList<Person>();

				if (contacts != null) {
					for (final GContact contact : contacts) {
						people.add(contact.save(true));
					}
				}

				return people;
			}
		};
		if (!inTx) {
			return Application.getDb().callInTx(callable);
		} else {
			return callable.call();
		}
	}
}
