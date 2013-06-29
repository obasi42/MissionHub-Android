package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GPeople {

    public GPerson[] people;
    public GPerson person;

    public GPerson[] organizational_roles; // returned by organizational_roles/bulk.. yes it's people
    public GPerson[] organizational_labels;
    public GPerson[] organizational_permissions;

    /**
     * Saves the people to the SQLite database.
     *
     * @param inTx
     * @return
     * @throws Exception
     */
    public List<Person> save(final boolean inTx) throws Exception {
        final Callable<List<Person>> callable = new Callable<List<Person>>() {
            @Override
            public List<Person> call() throws Exception {
                final List<Person> ps = new ArrayList<Person>();

                if (person != null) {
                    ps.add(person.save(true));
                }

                if (people != null) {
                    for (final GPerson person : people) {
                        ps.add(person.save(true));
                    }
                }

                if (organizational_roles != null) {
                    for (final GPerson person : organizational_roles) {
                        ps.add(person.save(true));
                    }
                }

                if (organizational_labels != null) {
                    for (final GPerson person : organizational_labels) {
                        ps.add(person.save(true));
                    }
                }

                if (organizational_permissions != null) {
                    for (final GPerson person : organizational_permissions) {
                        ps.add(person.save(true));
                    }
                }

                return ps;
            }
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

}