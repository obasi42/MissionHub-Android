package com.missionhub.model.gson;

import java.util.Set;
import java.util.concurrent.Callable;

import com.google.common.collect.HashMultimap;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.model.ContactAssignmentDao;
import com.missionhub.model.OrganizationalRoleDao;
import com.missionhub.model.Person;
import com.missionhub.model.PersonDao;
import com.missionhub.network.HttpParams;
import com.missionhub.util.U;

public class GPerson {

	public long id;
	public String first_name;
	public String last_name;
	public String gender;
	public String campus;
	public String year_in_school;
	public String major;
	public String minor;
	public String birth_date;
	public String date_became_christian;
	public String graduation_date;
	public String picture;
	public Long user_id;
	public Long fb_uid;
	public String created_at;
	public String updated_at;

	public GUser user;
	public GPhoneNumber[] phone_numbers;
	public GEmailAddress[] email_addresses;
	public GContactAssignment[] contact_assignments;
	public GFollowupComment[] followup_comments;
	public GFollowupComment[] comments_on_me;
	public GOrganizationalRole[] organizational_roles;
	public GOrganizationalRole[] all_organizational_roles;
	public GAddress current_address;

	public Boolean _assignToMe = false; // used by AddContactDialog
	public HashMultimap<Long, String> _answers; // used by AddContactDialog

	public static final Object lock = new Object();

	/**
	 * Saves the person to the SQLite database.
	 * 
	 * @param inTx
	 * @return
	 * @throws Exception
	 */
	public Person save(final boolean inTx) throws Exception {
		final Callable<Person> callable = new Callable<Person>() {
			@Override
			public Person call() throws Exception {
				synchronized (lock) {
					final PersonDao dao = Application.getDb().getPersonDao();

					boolean insert = false;
					Person person = dao.load(id);

					if (person == null) {
						person = new Person();
						insert = true;
					}
					person.setId(id);
					person.setFirst_name(first_name);
					person.setLast_name(last_name);
					person.setGender(gender);
					person.setCampus(campus);
					person.setYear_in_school(year_in_school);
					person.setMajor(major);
					person.setMinor(minor);
					person.setBirth_date(U.parseYMD(birth_date));
					person.setDate_became_christian(U.parseYMD(date_became_christian));
					person.setGraduation_date(U.parseYMD(graduation_date));
					person.setPicture(picture);
					person.setUser_id(user_id);
					person.setFb_uid(fb_uid);
					person.setCreated_at(U.parseISO8601(created_at));
					person.setUpdated_at(U.parseISO8601(updated_at));

					if (insert) {
						dao.insert(person);
					} else {
						dao.update(person);
					}

					if (user != null) {
						user.save(id, true);
					}

					if (phone_numbers != null) {
						for (final GPhoneNumber number : phone_numbers) {
							number.save(true);
						}
					}

					if (email_addresses != null) {
						for (final GEmailAddress address : email_addresses) {
							address.save(true);
						}
					}

					if (contact_assignments != null) {
						Application.getDb().getContactAssignmentDao().queryBuilder().where(ContactAssignmentDao.Properties.Person_id.eq(id), ContactAssignmentDao.Properties.Organization_id.eq(Session.getInstance().getOrganizationId())).buildDelete().executeDeleteWithoutDetachingEntities();
						for (final GContactAssignment assignment : contact_assignments) {
							assignment.save(true);
						}
					}

					if (followup_comments != null) {
						for (final GFollowupComment comment : followup_comments) {
							comment.save(true);
						}
					}

					if (comments_on_me != null) {
						for (final GFollowupComment comment : comments_on_me) {
							comment.save(true);
						}
					}

					if (organizational_roles != null) {
						Application.getDb().getOrganizationalRoleDao().queryBuilder()
								.where(OrganizationalRoleDao.Properties.Person_id.eq(id), OrganizationalRoleDao.Properties.Organization_id.eq(Session.getInstance().getOrganizationId())).buildDelete()
								.executeDeleteWithoutDetachingEntities();
						for (final GOrganizationalRole role : organizational_roles) {
							role.save(true);
						}
					}

					if (all_organizational_roles != null) {
						Application.getDb().getOrganizationalRoleDao().queryBuilder().where(OrganizationalRoleDao.Properties.Person_id.eq(id)).buildDelete().executeDeleteWithoutDetachingEntities();
						for (final GOrganizationalRole role : all_organizational_roles) {
							role.save(true);
						}
					}

					if (current_address != null) {
						current_address.save(id, true);
					}

					return person;
				}
			}
		};
		if (inTx) {
			return callable.call();
		} else {
			return Application.getDb().callInTx(callable);
		}
	}

	/**
	 * Adds the http params for this person
	 * 
	 * @param params
	 */
	public void toParams(final HttpParams params) {
		if (!U.isNullEmpty(first_name)) {
			params.add("person[first_name]", first_name);
		}
		if (!U.isNullEmpty(last_name)) {
			params.add("person[last_name]", last_name);
		}
		if (!U.isNullEmpty(gender)) {
			params.add("person[gender]", gender);
		}

		if (phone_numbers != null) {
			for (int i = 0; i < phone_numbers.length; i++) {
				final GPhoneNumber number = phone_numbers[i];
				if (number.id > 0) {
					params.add("person[phone_numbers_attributes][" + i + "][id]", number.id);
				}
				if (!U.isNullEmpty(number.number)) {
					params.add("person[phone_numbers_attributes][" + i + "][number]", number.number);
				}
				if (!U.isNullEmpty(number.location)) {
					params.add("person[phone_numbers_attributes][" + i + "][location]", number.location);
				}
				if (!U.isNullEmpty(number.primary)) {
					params.add("person[phone_numbers_attributes][" + i + "][primary]", number.primary);
				}
			}
		}

		if (email_addresses != null) {
			for (int i = 0; i < email_addresses.length; i++) {
				final GEmailAddress address = email_addresses[i];
				if (address.id > 0) {
					params.add("person[email_addresses_attributes][" + i + "][id]", address.id);
				}
				if (address.id > 0 && U.isNullEmpty(address.email)) {
					params.add("person[email_addresses_attributes][" + i + "][_destroy]", true);
				} else {
					if (!U.isNullEmpty(address.email)) {
						params.add("person[email_addresses_attributes][" + i + "][number]", address.email);
					}
					if (!U.isNullEmpty(address.primary)) {
						params.add("person[email_addresses_attributes][" + i + "][primary]", address.primary);
					}
				}
			}
		}

		if (current_address != null) {
			if (!U.isNullEmpty(current_address.address1)) {
				params.add("person[current_address_attributes][address1]", current_address.address1);
			}
			if (!U.isNullEmpty(current_address.address2)) {
				params.add("person[current_address_attributes][address2]", current_address.address2);
			}
			if (!U.isNullEmpty(current_address.city)) {
				params.add("person[current_address_attributes][city]", current_address.city);
			}
			if (!U.isNullEmpty(current_address.country)) {
				params.add("person[current_address_attributes][country]", current_address.country);
			}
			if (!U.isNullEmpty(current_address.state)) {
				params.add("person[current_address_attributes][state]", current_address.state);
			}
			if (!U.isNullEmpty(current_address.zip)) {
				params.add("person[current_address_attributes][zip]", current_address.zip);
			}
		}

		if (_assignToMe != null) {
			params.add("assign_to_me", _assignToMe);
		}

		if (_answers != null) {
			for (final Long key : _answers.keySet()) {
				final Set<String> values = _answers.get(key);
				if (values.size() <= 1) {
					for (final String value : values) {
						params.add("answers[" + key + "]", value);
					}
				} else {
					int count = 0;
					for (final String value : values) {
						params.add("answers[" + key + "][" + count + "]", value);
						count++;
					}
				}
			}
		}
	}

	public CharSequence getName() {
		if (first_name == null) first_name = "";
		if (last_name == null) last_name = "";
		return (first_name + " " + last_name).trim();
	}

	public void setName(final String string) {
		final String[] parts = string.split(" ");

		if (parts.length == 1) {
			first_name = parts[0];
		} else if (parts.length == 2) {
			first_name = parts[0];
			last_name = parts[1];
		} else if (parts.length > 2) {
			for (int i = 0; i < parts.length - 1; i++) {
				first_name += parts[i] + " ";
			}
			first_name = first_name.trim();
			last_name = parts[parts.length - 1];
		}
	}

	public boolean isValid() {
		return !U.isNullEmpty(first_name);
	}

}