package com.missionhub.model.gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.ContactAssignment;
import com.missionhub.model.ContactAssignmentDao;

public class GContactAssignments {

	public GContactAssignment[] contact_assignments;
	public GContactAssignment contact_assignment;

	/**
	 * Saves the contact assignments to the SQLite database.
	 * 
	 * @param inTx
	 * @return
	 * @throws Exception
	 */
	public List<ContactAssignment> save(final boolean inTx) throws Exception {
		final Callable<List<ContactAssignment>> callable = new Callable<List<ContactAssignment>>() {
			@Override
			public List<ContactAssignment> call() throws Exception {
				synchronized (GContactAssignment.allLock) {
					final List<ContactAssignment> assignments = new ArrayList<ContactAssignment>();
					if (contact_assignments != null) {
						for (final GContactAssignment assignment : contact_assignments) {
							final ContactAssignment assign = assignment.save(true);
							if (assign != null) {
								assignments.add(assign);
							}
						}
					}
					if (contact_assignment != null) {
						final ContactAssignment assign = contact_assignment.save(true);
						if (assign != null) {
							assignments.add(contact_assignment.save(true));
						}
					}
					return assignments;
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
	 * Replaces a person's current assignments with the passed ones
	 * 
	 * @param assignments
	 * @param personId
	 * @param organizationId
	 * @param inTx
	 * @return list of saved contact assignments
	 * @throws Exception
	 */
	public static List<ContactAssignment> replaceAll(final GContactAssignment[] assignments, final long personId, final long organizationId, final boolean inTx) throws Exception {
		final Callable<List<ContactAssignment>> callable = new Callable<List<ContactAssignment>>() {
			@Override
			public List<ContactAssignment> call() throws Exception {
				synchronized (GContactAssignment.allLock) {
					final ContactAssignmentDao dao = Application.getDb().getContactAssignmentDao();

					// delete current assignments
					dao.queryBuilder().where(ContactAssignmentDao.Properties.Person_id.eq(personId), ContactAssignmentDao.Properties.Organization_id.eq(organizationId)).buildDelete().executeDelete();

					// save the new assignments
					final List<ContactAssignment> assign = new ArrayList<ContactAssignment>();
					for (final GContactAssignment assignment : assignments) {
						final ContactAssignment a = assignment.save(true);
						if (a != null) {
							assign.add(a);
						}
					}

					return assign;
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