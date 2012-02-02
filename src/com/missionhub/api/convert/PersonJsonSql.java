package com.missionhub.api.convert;

import android.content.Context;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.model.GPerson;
import com.missionhub.api.model.sql.Person;
import com.missionhub.api.model.sql.PersonDao;
import com.missionhub.broadcast.PersonJsonSqlBroadcast;

public class PersonJsonSql {

	public static void update(final Context context, final GPerson person, final String... categories) {
		update(context, person, true, true, categories);
	}

	public static void update(final Context context, final GPerson person, final boolean threaded, final boolean notify, final String... categories) {
		try {
			privateUpdate(context, person, threaded, notify, categories);
		} catch (final Exception e) {
			int personId = -1;
			if (person != null) {
				personId = person.getId();
			}
			PersonJsonSqlBroadcast.broadcastError(context, personId, e, categories);
		}
	}

	private static void privateUpdate(final Context context, final GPerson person, final boolean threaded, final boolean notify, final String... categories) throws Exception {
		if (threaded) {
			new Thread(new Runnable() {
				@Override public void run() {
					// call update again, only this time we are in a thread, so
					// set threaded=false
					update(context, person, false, notify, categories);
				}
			}).start();
			return;
		}

		final MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		final PersonDao pd = app.getDbSession().getPersonDao();

		Person p = pd.load(person.getId());

		boolean createPerson = false;
		if (p == null) {
			p = new Person();
			createPerson = true;
		}

		if (person.getId() != null) {
			p.set_id(person.getId());
		}

		if (person.getName() != null) {
			p.setName(person.getName());
		}

		if (person.getGender() != null) {
			p.setGender(person.getGender());
		}

		if (person.getFb_id() != null) {
			p.setFb_id(person.getFb_id());
		}

		if (person.getPicture() != null) {
			p.setPicture(person.getPicture());
		}

		if (person.getStatus() != null) {
			p.setStatus(person.getStatus());
		}

		if (person.getFirst_name() != null) {
			p.setFirst_name(person.getFirst_name());
		}

		if (person.getLast_name() != null) {
			p.setLast_name(person.getLast_name());
		}

		if (person.getPhone_number() != null) {
			p.setPhone_number(person.getPhone_number());
		}

		if (person.getEmail_address() != null) {
			p.setEmail_address(person.getEmail_address());
		}

		if (person.getBirthday() != null) {
			p.setBirthday(person.getBirthday());
		}

		if (person.getLocale() != null) {
			p.setLocale(person.getLocale());
		}

		if (person.getNum_contacts() != null) {
			p.setNum_contacts(person.getNum_contacts());
		}

		// OrganizationRoleJsonSql.update(context, person.getId(),
		// person.getOrganizational_roles(), tag);
		//
		// if (person.getRequest_org_id() != null)
		// AssignmentJsonSql.update(context, person.getId(),
		// Integer.parseInt(person.getRequest_org_id()),
		// person.getAssignment(), tag);
		//
		// InterestJsonSql.update(context, person.getId(),
		// person.getInterests(), tag);
		//
		// EducationJsonSql.update(context, person.getId(),
		// person.getEducation(), tag);
		//
		// LocationJsonSql.update(context, person.getId(),
		// person.getLocation(), tag);

		pd.insertOrReplace(p);

		if (createPerson) {
			PersonJsonSqlBroadcast.broadcastCreate(context, p.get_id(), categories);
		} else {
			PersonJsonSqlBroadcast.broadcastUpdate(context, p.get_id(), categories);
		}
	}
	//
	// public static void update(Context context, GContact contact) {
	// update(context, contact, null);
	// }
	//
	// public static void update(Context context, GContact contact, String tag)
	// {
	// if (contact == null)
	// return;
	// update(context, contact.getPerson(), tag);
	//
	// if (contact.getPerson() != null)
	// AnswerJsonSql.update(context, contact.getPerson().getId(),
	// Integer.parseInt(contact.getPerson().getRequest_org_id()),
	// contact.getForm(), tag);
	// }
	//
	// public static void update(Context context, GMetaContact contact) {
	// update(context, contact, null);
	// }
	//
	// public static void update(Context context, GMetaContact contact, String
	// tag) {
	// if (contact == null)
	// return;
	// if (contact.getContacts() == null)
	// return;
	//
	// for (GContact c : contact.getContacts()) {
	// update(context, c, tag);
	// }
	// }
}