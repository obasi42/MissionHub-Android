package com.missionhub.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.Person;

public class GContact {
	public GPerson person;
	public GQuestionAnswer[] form;

	public Person save(final boolean inTx) throws Exception {
		final Callable<Person> callable = new Callable<Person>() {
			@Override
			public Person call() throws Exception {
				final Person p = person.save(true);
				GQuestionAnswer.save(form, p, person.request_org_id, true);

				Application.postEvent(new ContactUpdatedEvent(p));

				return p;
			}
		};
		if (!inTx) {
			return Application.getDb().callInTx(callable);
		} else {
			return callable.call();
		}
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
