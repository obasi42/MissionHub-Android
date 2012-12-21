package com.missionhub.model.gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.Person;

public class GPeople {

	public GPerson[] people;
	public GPerson person;

	/**
	 * Saves the people to the SQLite database.
	 * 
	 * @param inTx
	 * @return
	 * @throws Exception
	 */
	public List<Person> save(final boolean inTx) throws Exception {
		final Callable<List<Person>> callable = new Callable<List<Person>>() {
			@Override
			public List<Person> call() throws Exception {
				final List<Person> ps = new ArrayList<Person>();

				if (person != null) {
					ps.add(person.save(true));
				}

				if (people != null) {
					for (final GPerson person : people) {
						ps.add(person.save(true));
					}
				}

				return ps;
			}
		};
		if (inTx) {
			return callable.call();
		} else {
			return Application.getDb().callInTx(callable);
		}
	}

}