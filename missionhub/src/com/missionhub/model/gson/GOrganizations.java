package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Organization;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GOrganizations {

    GOrganization[] organizations;
    GOrganization organization;

    /**
     * Saves the organizations to the SQLite database.
     *
     * @param inTx
     * @return
     * @throws Exception
     */
    public List<Organization> save(final boolean inTx) throws Exception {
        final Callable<List<Organization>> callable = new Callable<List<Organization>>() {
            @Override
            public List<Organization> call() throws Exception {
                final List<Organization> orgs = new ArrayList<Organization>();

                if (organization != null) {
                    orgs.add(organization.save(true));
                }

                if (organizations != null) {
                    for (final GOrganization organization : organizations) {
                        orgs.add(organization.save(true));
                    }
                }

                return orgs;
            }
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

}