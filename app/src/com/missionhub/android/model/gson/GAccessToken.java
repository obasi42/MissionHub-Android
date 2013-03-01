package com.missionhub.android.model.gson;

import com.missionhub.android.application.Application;
import com.missionhub.android.model.Person;
import com.missionhub.android.model.PersonDao;

public class GAccessToken {

    public String access_token;

    public GAccessTokenPerson person;

    public static class GAccessTokenPerson {
        public long id;
        public String first_name;
        public String last_name;

        public Person save() {
            synchronized (GPerson.lock) {
                final PersonDao dao = Application.getDb().getPersonDao();
                Person person = dao.load(id);
                boolean insert = false;
                if (person == null) {
                    person = new Person();
                    insert = true;
                }

                person.setId(id);
                person.setFirst_name(first_name);
                person.setLast_name(last_name);

                if (insert) {
                    dao.insert(person);
                } else {
                    dao.update(person);
                }

                return person;
            }
        }
    }

}