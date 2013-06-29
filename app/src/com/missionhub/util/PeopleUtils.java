package com.missionhub.util;

import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import com.missionhub.model.Person;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PeopleUtils {

    public static List<Person> sortPeople(final Collection<Person> people, final boolean asc) {
        final TreeMultimap<String, Person> sorted = TreeMultimap.create(Ordering.natural(), Ordering.usingToString());
        for (final Person person : people) {
            String name = "";
            if (StringUtils.isNotEmpty(person.getName())) {
                name = person.getName();
            }
            sorted.put(name, person);
        }

        List<Person> sortedList = new ArrayList<Person>(sorted.values());
        if (!asc) {
            Collections.reverse(sortedList);
        }
        return sortedList;
    }

}
