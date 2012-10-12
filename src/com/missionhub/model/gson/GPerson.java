package com.missionhub.model.gson;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.missionhub.application.Application;
import com.missionhub.model.Interest;
import com.missionhub.model.Location;
import com.missionhub.model.OrganizationalRole;
import com.missionhub.model.Person;
import com.missionhub.model.PersonDao;
import com.missionhub.util.U;

/**
 * The PJO of a JSON Person
 */
public class GPerson {

	public String first_name;
	public String last_name;
	public String name;
	public long id;
	public String birthday;
	public String locale;
	public String gender;
	public String fb_id;
	public String picture;
	public String status;
	public String request_org_id;
	public String phone_number;
	public String email_address;
	public GIdNameProvider location;
	public GIdNameProvider[] interests;
	public GAssignment assignment;
	public GOrgGeneric[] organizational_roles;
	public GGroupMembership[] group_memberships;
	public GEducation[] education;
	public String num_contacts;

	/**
	 * Saves the person object to the local database.
	 * 
	 * @return
	 */
	public FutureTask<Person> save() {
		return save(true);
	}

	/**
	 * Saves the person object to the local database.
	 * 
	 * @param execute
	 *            true if the task should be automatically executed, false otherwise
	 * @return
	 */
	public FutureTask<Person> save(final boolean threaded) {
		final FutureTask<Person> task = new FutureTask<Person>(new Callable<Person>() {
			@Override
			public Person call() throws Exception {

				final Callable<Person> callable = new Callable<Person>() {
					@Override
					public Person call() throws Exception {
						final PersonDao pdao = Application.getDb().getPersonDao();
						Person p = pdao.load(id);
						if (p == null) {
							p = new Person();
						}

						if (!U.isNullEmptyNegative(id)) p.setId(id);
						if (!U.isNullEmpty(first_name)) p.setFirst_name(first_name);
						if (!U.isNullEmpty(last_name)) p.setLast_name(last_name);
						if (!U.isNullEmpty(name)) p.setName(name);
						if (!U.isNullEmpty(birthday)) p.setBirthday(birthday);
						if (!U.isNullEmpty(locale)) p.setLocale(locale);
						if (!U.isNullEmpty(gender)) p.setGender(gender);
						if (!U.isNullEmpty(fb_id)) p.setFb_id(fb_id);
						if (!U.isNullEmpty(picture)) p.setPicture(picture);
						if (!U.isNullEmpty(status)) p.setStatus(status);
						if (!U.isNullEmpty(phone_number)) p.setPhone_number(phone_number);
						if (!U.isNullEmpty(email_address)) p.setEmail_address(email_address);

						GIdNameProvider.save(Location.class, location, p);
						GIdNameProvider.save(Interest.class, interests, p);

						if (!U.isNullEmpty(assignment)) assignment.save(p, request_org_id, false);

						if (organizational_roles != null && organizational_roles.length > 0) {
							GOrgGeneric.save(OrganizationalRole.class, organizational_roles, p);
						}

						if (group_memberships != null && group_memberships.length > 0) {
							GGroupMembership.save(group_memberships, p);
						}

						if (education != null && education.length > 0) {
							GEducation.save(education, p);
						}

						if (!U.isNullEmpty(num_contacts)) p.setNum_contacts(num_contacts);

						pdao.insertOrReplace(p);

						Application.postEvent(new PersonUpdatedEvent(p));

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
	 * Event posted when a person is updated
	 */
	public static class PersonUpdatedEvent {

		public Person person;

		public PersonUpdatedEvent(final Person p) {
			this.person = p;
		}

	}

}
