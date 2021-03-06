package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Organization;
import com.missionhub.model.OrganizationDao;

import java.util.concurrent.Callable;

public class GOrganization {

    public GOrganization organization;

    public long id;
    public String name;
    public String terminology;
    public String ancestry;
    public Boolean show_sub_orgs;
    public String status;
    public String created_at;
    public String updated_at;

    public GPerson[] contacts;
    public GPerson[] admins;
    public GPerson[] users;
    public GPerson[] people;
    public GSurvey[] surveys;
    public GSmsKeyword[] keywords;
    public GLabel[] labels;
    public GInteractionType[] interaction_types;

    /**
     * Saves the organization to the SQLite database.
     *
     * @param inTx
     * @return
     * @throws Exception
     */
    public Organization save(final boolean inTx) throws Exception {
        final Callable<Organization> callable = new Callable<Organization>() {
            @Override
            public Organization call() throws Exception {
                // wrapped organization
                if (organization != null) {
                    return organization.save(true);
                }

                boolean update = true;
                final OrganizationDao dao = Application.getDb().getOrganizationDao();

                Organization org = dao.load(id);
                if (org == null) {
                    org = new Organization();
                    update = false;
                }
                org.setId(id);
                org.setName(name);
                org.setTerminology(terminology);
                org.setAncestry(ancestry);
                org.setShow_sub_orgs(show_sub_orgs);
                org.setStatus(status);
                org.setCreated_at(created_at);
                org.setUpdated_at(updated_at);

                if (update) {
                    dao.update(org);
                } else {
                    dao.insert(org);
                }

                if (contacts != null) {
                    for (final GPerson person : contacts) {
                        person.save(true);
                    }
                }

                if (admins != null) {
                    for (final GPerson person : admins) {
                        person.save(true);
                    }
                }

                if (users != null) {
                    for (final GPerson person : users) {
                        person.save(true);
                    }
                }

                if (people != null) {
                    for (final GPerson person : people) {
                        person.save(true);
                    }
                }

                if (surveys != null) {
                    GSurvey.replaceAll(surveys, id, true);
                }

                if (keywords != null) {
                    GSmsKeyword.replaceAll(keywords, id, true);
                }

                if (labels != null) {
                    GLabel.replaceAll(labels, id, true);
                }

                if (interaction_types != null) {
                    GInteractionType.replaceAll(interaction_types, true);
                }

                org.refreshAll();

                return org;
            }

        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

}