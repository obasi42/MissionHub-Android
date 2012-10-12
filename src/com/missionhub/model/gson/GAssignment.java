package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Assignment;
import com.missionhub.model.AssignmentDao;
import com.missionhub.model.AssignmentDao.Properties;
import com.missionhub.model.Person;
import com.missionhub.util.U;

import de.greenrobot.dao.CloseableListIterator;
import de.greenrobot.dao.LazyList;

public class GAssignment {
	public GIdNameProvider[] assigned_to_person;
	public GIdNameProvider[] person_assigned_to;

	/**
	 * Saves the assignment to the database
	 * 
	 * @param p
	 */
	public void save(final Person p, final String request_org_id) {
		save(p, request_org_id, true);
	}

	/**
	 * Saves the assignment to the database
	 * 
	 * @param p
	 * @param request_org_id
	 * @param execute
	 */
	public void save(final Person p, final String request_org_id, final boolean execute) {
		if (U.isNullEmpty(p, request_org_id)) return;

		final Runnable runnable = new Runnable() {
			@Override
			public void run() {

				final Runnable txrunnable = new Runnable() {
					@Override
					public void run() {
						final AssignmentDao ad = Application.getDb().getAssignmentDao();

						// delete current assignment records for the current person
						final LazyList<Assignment> delAssignments = ad.queryBuilder()
								.whereOr(Properties.Person_id.eq(p.getId()), Properties.Assigned_to_id.eq(p.getId())).listLazyUncached();
						final CloseableListIterator<Assignment> itr = delAssignments.listIteratorAutoClose();
						while (itr.hasNext()) {
							ad.delete(itr.next());
						}

						// add people assigned to the person
						for (final GIdNameProvider ap : assigned_to_person) {
							if (ap == null) continue;

							final Assignment a = new Assignment();

							final GPerson person = new GPerson();
							person.id = Long.parseLong(ap.id);
							person.name = ap.name;
							person.save(false);

							a.setAssigned_to_id(p.getId());
							a.setPerson_id(person.id);
							a.setOrganization_id(Long.parseLong(request_org_id));

							ad.insert(a);
						}

						// add people the person is assigned to
						for (final GIdNameProvider pa : person_assigned_to) {
							if (pa == null) continue;

							final Assignment a = new Assignment();

							final GPerson person = new GPerson();
							person.id = Long.parseLong(pa.id);
							person.name = pa.name;
							person.save(false);

							a.setAssigned_to_id(person.id);
							a.setPerson_id(p.getId());
							a.setOrganization_id(Long.parseLong(request_org_id));

							ad.insert(a);
						}
					}
				};

				if (execute) {
					Application.getDb().runInTx(txrunnable);
				} else {
					txrunnable.run();
				}
			}
		};

		if (execute) {
			Application.getExecutor().execute(runnable);
		} else {
			runnable.run();
		}
	}
}
