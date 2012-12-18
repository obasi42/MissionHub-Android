package com.missionhub.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.ContactAssignment;
import com.missionhub.model.ContactAssignmentDao;
import com.missionhub.util.U;

public class GContactAssignment {

	public long id;
	public long person_id;
	public long assigned_to_id;
	public long organization_id;
	public String created_at;
	public String updated_at;
	public String deleted_at;

	public GPerson assigned_to;
	public GPerson person;

	public static final Object lock = new Object();
	public static final Object allLock = new Object();

	/**
	 * Saves the contact assignment to the SQLite database.
	 * 
	 * @param inTx
	 * @return
	 * @throws Exception
	 */
	public ContactAssignment save(final boolean inTx) throws Exception {
		final Callable<ContactAssignment> callable = new Callable<ContactAssignment>() {
			@Override
			public ContactAssignment call() throws Exception {
				synchronized (lock) {
					final ContactAssignmentDao dao = Application.getDb().getContactAssignmentDao();

					ContactAssignment assignment = dao.load(id);
					
					if (deleted_at != null) {
						if (assignment != null) {
							assignment.delete();
						}
						return null;
					}

					boolean insert = false;
					if (assignment == null) {
						assignment = new ContactAssignment();
						insert = true;
					}
					assignment.setId(id);
					assignment.setPerson_id(person_id);
					assignment.setAssigned_to_id(assigned_to_id);
					assignment.setOrganization_id(organization_id);
					assignment.setCreated_at(U.parseISO8601(created_at));
					assignment.setUpdated_at(U.parseISO8601(updated_at));

					if (assigned_to != null) {
						assigned_to.save(true);
					}
					if (person != null) {
						person.save(true);
					}

					if (insert) {
						dao.insert(assignment);
					} else {
						dao.update(assignment);
					}

					return assignment;
				}
			}
		};
		if (inTx) {
			return callable.call();
		} else {
			return Application.getDb().callInTx(callable);
		}
	}
}