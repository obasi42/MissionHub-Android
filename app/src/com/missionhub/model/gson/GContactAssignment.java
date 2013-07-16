package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.ContactAssignment;
import com.missionhub.model.ContactAssignmentDao;

import java.util.concurrent.Callable;

public class GContactAssignment {

    GContactAssignment contact_assignment;

    public long id;
    public long person_id;
    public long assigned_to_id;
    public long organization_id;
    public String created_at;
    public String updated_at;
    public String deleted_at;

    public GPerson assigned_to;
    public GPerson person;

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
                // wrapped assignment
                if (contact_assignment != null) {
                    return contact_assignment.save(true);
                }

                final ContactAssignmentDao dao = Application.getDb().getContactAssignmentDao();

                if (deleted_at != null) {
                    dao.deleteByKey(id);
                    return null;
                }

                final ContactAssignment assignment = new ContactAssignment();
                assignment.setId(id);
                assignment.setPerson_id(person_id);
                assignment.setAssigned_to_id(assigned_to_id);
                assignment.setOrganization_id(organization_id);
                assignment.setCreated_at(created_at);
                assignment.setUpdated_at(updated_at);
                dao.insertOrReplace(assignment);

                if (assigned_to != null) {
                    assigned_to.save(true);
                }
                if (person != null) {
                    person.save(true);
                }

                return assignment;
            }
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }
}