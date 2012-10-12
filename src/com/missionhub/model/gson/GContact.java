package com.missionhub.model.gson;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.missionhub.application.Application;
import com.missionhub.model.Person;

public class GContact {
	public GPerson person;
	public GQuestionAnswer[] form;

	public FutureTask<Person> save() {
		return save(true);
	}

	public FutureTask<Person> save(final boolean threaded) {
		final FutureTask<Person> task = new FutureTask<Person>(new Callable<Person>() {
			@Override
			public Person call() throws Exception {

				final Callable<Person> callable = new Callable<Person>() {
					@Override
					public Person call() throws Exception {
						final Person p = person.save(false).get();
						GQuestionAnswer.save(form, p, person.request_org_id);

						Application.postEvent(new ContactUpdatedEvent(p));

						return p;
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
	 * Event posted after updating a contact
	 */
	public static class ContactUpdatedEvent {
		public Person person;

		public ContactUpdatedEvent(final Person p) {
			person = p;
		}
	}
}
