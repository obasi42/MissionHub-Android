package com.missionhub.util;

import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import com.missionhub.model.InteractionType;
import com.missionhub.model.Label;
import com.missionhub.model.Person;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SortUtils<T> {
    public List<T> sort(final Collection<T> objects, final boolean asc, final SortNameCallback callback) {
        final TreeMultimap<String, T> sorted = TreeMultimap.create(Ordering.natural(), Ordering.usingToString());
        for (final T object : objects) {
            String name;

            SortNameCallback cb = callback;
            if (object instanceof SortNameCallback) {
                cb = (SortNameCallback) object;
            }

            if (cb == null) {
                name = object.toString();
            } else {
                name = cb.getSortName(object);
            }

            if (StringUtils.isEmpty(name)) {
                name = "";
            }
            sorted.put(name, object);
        }

        List<T> sortedList = new ArrayList<T>(sorted.values());
        if (!asc) {
            Collections.reverse(sortedList);
        }
        return sortedList;
    }

    public interface SortNameCallback {
        public String getSortName(Object object);
    }

    public static List<Person> sortPeople(final Collection<Person> people, final boolean asc) {
        SortUtils<Person> su = new SortUtils<Person>();
        return su.sort(people, asc, new SortNameCallback() {
            @Override
            public String getSortName(Object object) {
                return ((Person) object).getName();
            }
        });
    }

    public static List<InteractionType> sortInteractionTypes(final Collection<InteractionType> types, final boolean asc) {
        SortUtils<InteractionType> su = new SortUtils<InteractionType>();
        return su.sort(types, asc, new SortNameCallback() {
            @Override
            public String getSortName(Object object) {
                return ((InteractionType) object).getTranslatedName();
            }
        });
    }

    public static List<Label> sortLabels(final Collection<Label> labels, final boolean asc) {
        SortUtils<Label> su = new SortUtils<Label>();
        return su.sort(labels, asc, new SortNameCallback() {
            @Override
            public String getSortName(Object object) {
                return ((Label) object).getTranslatedName();
            }
        });
    }
}
