package com.missionhub.api.convert;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.model.GPerson;
import com.missionhub.api.model.sql.DaoSession;
import com.missionhub.api.model.sql.Person;
import com.missionhub.api.model.sql.PersonDao;
import com.missionhub.broadcast.GenericCUDEBroadcast;

public class PersonJsonSql {

	public static void update(final Context context, final GPerson person, final String... categories) {
		update(context, new GPerson[] { person }, true, true, categories);
	}

	public static void update(final Context context, final GPerson[] people, final String... categories) {
		update(context, people, true, true, categories);
	}

	public static void update(final Context context, final GPerson person, final boolean threaded, final boolean notify, final String... categories) {
		try {
			privateUpdate(context, new GPerson[] { person }, threaded, notify, categories);
		} catch (final Exception e) {
			if (notify) {
				if (person != null) {
					GenericCUDEBroadcast.broadcastError(context, Person.class, person.getId(), e, categories);
				} else {
					GenericCUDEBroadcast.broadcastError(context, Person.class, -1, e, categories);
				}
			}
		}
	}

	public static void update(final Context context, final GPerson[] people, final boolean threaded, final boolean notify, final String... categories) {
		try {
			privateUpdate(context, people, threaded, notify, categories);
		} catch (final Exception e) {
			if (notify) {
				final ArrayList<Long> ids = new ArrayList<Long>();
				for (final GPerson person : people) {
					if (person != null) {
						ids.add(person.getId());
					}
				}
				GenericCUDEBroadcast.broadcastError(context, Person.class, ids, e, categories);
			}
		}
	}

	private static void privateUpdate(final Context context, final GPerson[] people, final boolean threaded, final boolean notify, final String... categories) throws Exception {
		if (threaded) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// call update again, only this time we are in a thread, so
					// set threaded=false
					update(context, people, false, notify, categories);
				}
			}).start();
			return;
		}

		final MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		final DaoSession session = app.getDbSession();
		final PersonDao pd = session.getPersonDao();

		final List<Person> peoples = new ArrayList<Person>();
		final List<Long> createdIds = new ArrayList<Long>();
		final List<Long> updatedIds = new ArrayList<Long>();

		for (final GPerson person : people) {
			Person p = pd.load(person.getId());

			if (p == null) {
				p = new Person();
				createdIds.add(person.getId());
			} else {
				p.refresh();
				updatedIds.add(person.getId());
			}

			p.setId(person.getId());

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

			if (person.getOrganizational_roles() != null) {
				OrganizationRoleJsonSql.update(context, person.getId(), person.getOrganizational_roles(), threaded, notify, categories);
			}

			if (person.getGroup_memberships() != null) {
				GroupMembershipJsonSql.update(context, person.getId(), person.getGroup_memberships(), threaded, notify, categories);
			}

			if (person.getInterests() != null) {
				InterestJsonSql.update(context, person.getId(), person.getInterests(), threaded, notify, categories);
			}

			if (person.getEducation() != null) {
				EducationJsonSql.update(context, person.getId(), person.getEducation(), threaded, notify, categories);
			}

			if (person.getLocation() != null) {
				LocationJsonSql.update(context, person.getId(), person.getLocation(), threaded, notify, categories);
			}

			peoples.add(p);
		}

		session.runInTx(new Runnable() {
			@Override
			public void run() {
				for (final Person person : peoples) {
					session.insertOrReplace(person);
				}
			}
		});

		if (notify) {
			GenericCUDEBroadcast.broadcastCreate(context, Person.class, createdIds, categories);
			GenericCUDEBroadcast.broadcastUpdate(context, Person.class, updatedIds, categories);
		}
	}
}