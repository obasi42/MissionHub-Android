package com.missionhub.model.gson;

import com.google.common.collect.HashMultimap;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.model.AnswerSheet;
import com.missionhub.model.AnswerSheetDao;
import com.missionhub.model.ContactAssignmentDao;
import com.missionhub.model.DaoSession;
import com.missionhub.model.OrganizationalPermissionDao;
import com.missionhub.model.Person;
import com.missionhub.model.PersonDao;
import com.missionhub.util.ObjectUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class GPerson {

    public GPerson person;

    public long id;
    public String first_name;
    public String last_name;
    public String gender;
    public String campus;
    public String year_in_school;
    public String major;
    public String minor;
    public String birth_date;
    public String date_became_christian;
    public String graduation_date;
    public String picture;
    public Long user_id;
    public Long fb_uid;
    public String created_at;
    public String updated_at;

    public GPhoneNumber[] phone_numbers;
    public GEmailAddress[] email_addresses;
    public GContactAssignment[] contact_assignments;
    public GContactAssignment[] assigned_tos;
    public GAnswerSheet[] answer_sheets;
    public GOrganizationalPermission[] all_organizational_permissions;
    public GOrganization[] all_organization_and_children;
    public GInteraction[] interactions;
    public GOrganizationalLabel[] organizational_labels;
    public GAddress[] addresses;

    public GUser user;
    public GOrganizationalPermission organizational_permission;

    public HashMultimap<Long, String> _answers; // used by AddContactDialog

    /**
     * Saves the person to the SQLite database.
     *
     * @param inTx
     * @return
     * @throws Exception
     */
    public Person save(final boolean inTx) throws Exception {
        final Callable<Person> callable = new Callable<Person>() {
            @Override
            public Person call() throws Exception {
                // wrapped person
                if (person != null) {
                    return person.save(true);
                }

                final DaoSession session = Application.getDb();
                final PersonDao dao = session.getPersonDao();
                //long orgId = Session.getInstance().getOrganizationId();

                boolean insert = false;
                Person person = dao.load(id);

                if (person == null) {
                    person = new Person();
                    insert = true;
                }
                person.setId(id);

                if (first_name != null) {
                    person.setFirst_name(first_name);
                }
                if (last_name != null) {
                    person.setLast_name(last_name);
                }
                if (gender != null) {
                    person.setGender(gender);
                }
                if (campus != null) {
                    person.setCampus(campus);
                }
                if (year_in_school != null) {
                    person.setYear_in_school(year_in_school);
                }
                if (major != null) {
                    person.setMajor(major);
                }
                if (minor != null) {
                    person.setMinor(minor);
                }
                if (birth_date != null) {
                    person.setBirth_date(birth_date);
                }
                if (date_became_christian != null) {
                    person.setDate_became_christian(date_became_christian);
                }
                if (graduation_date != null) {
                    person.setGraduation_date(graduation_date);
                }
                if (picture != null) {
                    person.setPicture(picture);
                }
                if (user_id != null) {
                    person.setUser_id(user_id);
                }
                if (fb_uid != null) {
                    person.setFb_uid(fb_uid);
                }
                if (created_at != null) {
                    person.setCreated_at(created_at);
                }
                if (updated_at != null) {
                    person.setUpdated_at(updated_at);
                }

                if (insert) {
                    dao.insert(person);
                } else {
                    dao.update(person);
                }

                if (phone_numbers != null) {
                    GPhoneNumber.replaceAll(phone_numbers, id, true);
                }

                if (email_addresses != null) {
                    GEmailAddress.replaceAll(email_addresses, id, true);
                }

                if (contact_assignments != null) {
                    Set<Long> orgIds = new HashSet<Long>();
                    for (GContactAssignment assignment : contact_assignments) {
                        orgIds.add(assignment.organization_id);
                    }
                    orgIds.add(Session.getInstance().getOrganizationId());

                    List<Long> keys = session.getContactAssignmentDao().queryBuilder().where(ContactAssignmentDao.Properties.Assigned_to_id.eq(id), ContactAssignmentDao.Properties.Organization_id.in(orgIds)).listKeys();
                    for (Long key : keys) {
                        session.getContactAssignmentDao().deleteByKey(key);
                    }

                    for (final GContactAssignment assignment : contact_assignments) {
                        assignment.save(true);
                    }
                }

                if (assigned_tos != null) {
                    Set<Long> orgIds = new HashSet<Long>();
                    for (GContactAssignment assignment : assigned_tos) {
                        orgIds.add(assignment.organization_id);
                    }
                    orgIds.add(Session.getInstance().getOrganizationId());

                    List<Long> keys = session.getContactAssignmentDao().queryBuilder().where(ContactAssignmentDao.Properties.Person_id.eq(id), ContactAssignmentDao.Properties.Organization_id.in(orgIds)).listKeys();
                    for (Long key : keys) {
                        session.getContactAssignmentDao().deleteByKey(key);
                    }

                    for (final GContactAssignment assignment : assigned_tos) {
                        assignment.save(true);
                    }
                }

                if (answer_sheets != null) {
                    final List<AnswerSheet> oldSheets = Application.getDb().getAnswerSheetDao().queryBuilder().where(AnswerSheetDao.Properties.Person_id.eq(id)).list();
                    for (final AnswerSheet oldSheet : oldSheets) {
                        oldSheet.deleteWithRelations();
                    }
                    for (final GAnswerSheet sheet : answer_sheets) {
                        sheet.save(id, true);
                    }
                }

                if (all_organizational_permissions != null) {
                    final List<Long> oldKeys = Application.getDb().getOrganizationalPermissionDao().queryBuilder().where(OrganizationalPermissionDao.Properties.Person_id.eq(id)).listKeys();
                    for (long key : oldKeys) {
                        Application.getDb().getOrganizationalPermissionDao().deleteByKey(key);
                    }
                    for (GOrganizationalPermission perm : all_organizational_permissions) {
                        perm.save(true);
                    }
                }

                if (all_organization_and_children != null) {
                    for (GOrganization organization : all_organization_and_children) {
                        organization.save(true);
                    }
                }

                if (interactions != null) {
                    GInteraction.replaceAll(interactions, id, true);
                }

                if (organizational_labels != null) {
                    GOrganizationalLabel.replaceAll(organizational_labels, id, true);
                }

                if (addresses != null) {
                    GAddress.replaceAll(addresses, id, true);
                }

                if (user != null) {
                    user.save(id, true);
                }

                if (organizational_permission != null) {
                    GOrganizationalPermission.replace(organizational_permission, true);
                }

                if (person.getId() == Session.getInstance().getPersonId()) {
                    person.getOrganizationHierarchy();
                }

                person.refreshAll();
                person.getViewCache();

                return person;
            }

        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

    /**
     * Adds the http params for this person
     *
     * @param params
     */
    public void toParams(final Map<String, String> params) {
        if (ObjectUtils.isNotEmpty(first_name)) {
            params.put("person[first_name]", first_name);
        }
        if (ObjectUtils.isNotEmpty(last_name)) {
            params.put("person[last_name]", last_name);
        }
        if (ObjectUtils.isNotEmpty(gender)) {
            params.put("person[gender]", gender);
        }

        if (phone_numbers != null) {
            for (int i = 0; i < phone_numbers.length; i++) {
                final GPhoneNumber number = phone_numbers[i];
                if (number.id > 0) {
                    params.put("person[phone_numbers][" + i + "][id]", String.valueOf(number.id));
                }
                if (ObjectUtils.isNotEmpty(number.number)) {
                    params.put("person[phone_numbers][" + i + "][number]", number.number);
                }
                if (ObjectUtils.isNotEmpty(number.location)) {
                    params.put("person[phone_numbers][" + i + "][location]", number.location);
                }
                if (ObjectUtils.isNotEmpty(number.primary)) {
                    params.put("person[phone_numbers][" + i + "][primary]", String.valueOf(number.primary));
                }
            }
        }

        if (email_addresses != null) {
            for (int i = 0; i < email_addresses.length; i++) {
                final GEmailAddress address = email_addresses[i];
                if (address.id > 0) {
                    params.put("person[email_addresses][" + i + "][id]", String.valueOf(address.id));
                }
                if (address.id > 0 && StringUtils.isEmpty(address.email)) {
                    params.put("person[email_addresses][" + i + "][_destroy]", String.valueOf(true));
                } else {
                    if (ObjectUtils.isNotEmpty(address.email)) {
                        params.put("person[email_addresses][" + i + "][email]", address.email);
                    }
                    if (ObjectUtils.isNotEmpty(address.primary)) {
                        params.put("person[email_addresses][" + i + "][primary]", String.valueOf(address.primary));
                    }
                }
            }
        }

        if (addresses != null) {
            for (int i = 0; i < addresses.length; i++) {
                final GAddress address = addresses[i];
                if (address.id > 0) {
                    params.put("person[addresses][" + i + "][id]", String.valueOf(address.id));
                }
                if (address.id > 0 && StringUtils.isEmpty(address.address1)) {
                    params.put("person[addresses][" + i + "][_destroy]", String.valueOf(true));
                } else {
                    if (ObjectUtils.isNotEmpty(address.address1)) {
                        params.put("person[addresses][" + i + "][address1]", address.address1);
                    }
                    if (ObjectUtils.isNotEmpty(address.address2)) {
                        params.put("person[addresses][" + i + "][address2]", address.address2);
                    }
                    if (ObjectUtils.isNotEmpty(address.city)) {
                        params.put("person[addresses][" + i + "][city]", address.city);
                    }
                    if (ObjectUtils.isNotEmpty(address.country)) {
                        params.put("person[addresses][" + i + "][country]", address.country);
                    }
                    if (ObjectUtils.isNotEmpty(address.state)) {
                        params.put("person[addresses][" + i + "][state]", address.state);
                    }
                    if (ObjectUtils.isNotEmpty(address.zip)) {
                        params.put("person[addresses][" + i + "][zip]", address.zip);
                    }
                    if (ObjectUtils.isNotEmpty(address.address_type)) {
                        params.put("person[addresses][" + i + "][address_type]", address.address_type);
                    }
                }
            }
        }

        if (_answers != null) {
            for (final Long key : _answers.keySet()) {
                final Set<String> values = _answers.get(key);
                if (values.size() <= 1) {
                    for (final String value : values) {
                        params.put("answers[" + key + "]", value);
                    }
                } else {
                    int count = 0;
                    for (final String value : values) {
                        params.put("answers[" + key + "][" + count + "]", value);
                        count++;
                    }
                }
            }
        }
    }

    public CharSequence getName() {
        if (first_name == null) first_name = "";
        if (last_name == null) last_name = "";
        return (first_name + " " + last_name).trim();
    }

    public void setName(final String string) {
        final String[] parts = string.split(" ");

        if (parts.length == 1) {
            first_name = parts[0];
        } else if (parts.length == 2) {
            first_name = parts[0];
            last_name = parts[1];
        } else if (parts.length > 2) {
            for (int i = 0; i < parts.length - 1; i++) {
                first_name += parts[i] + " ";
            }
            first_name = first_name.trim();
            last_name = parts[parts.length - 1];
        }
    }

    public boolean isValid() {
        return ObjectUtils.isNotEmpty(first_name);
    }

}