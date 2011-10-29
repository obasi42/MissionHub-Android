package com.missionhub.api.convert;

import java.util.Date;

import com.missionhub.Application;
import com.missionhub.api.ApiNotifier;
import com.missionhub.api.model.json.GContact;
import com.missionhub.api.model.json.GContactAll;
import com.missionhub.api.model.json.GPerson;
import com.missionhub.api.model.sql.Person;
import com.missionhub.api.model.sql.PersonDao;
import com.missionhub.helper.Helper;
import com.missionhub.helper.U;

import android.content.Context;
import android.os.Bundle;

public class PersonJsonSql {
	
	public static void update(Context context, GPerson person) {
		update(context, person, null);
	}

	public static void update(Context context, GPerson person, String tag) {
		if (person == null) return;
		
		Application app = (Application) context.getApplicationContext();
		PersonDao pd = app.getDbSession().getPersonDao();
		
		Person p = pd.load(person.getId());
		
		if (p == null) {
			p = new Person();
		}
		
		if (person.getId() != null)
			p.set_id(person.getId());
		
		if (person.getName() != null)
			p.setName(person.getName());
		
		if (person.getGender() != null)
			p.setGender(person.getGender());
		
		if (person.getFb_id() != null)
			p.setFb_id(person.getFb_id());
		
		if (person.getPicture() != null)
			p.setPicture(person.getPicture());
		
		if (person.getStatus() != null)
			p.setStatus(person.getStatus());
		
		if (!U.nullOrEmpty(person.getFirst_contact_date()))
			p.setFirst_contact_date(Helper.getDateFromUTCString(person.getFirst_contact_date()));
		
		if (!U.nullOrEmpty(person.getDate_surveyed()))
			p.setDate_surveyed(Helper.getDateFromUTCString(person.getDate_surveyed()));
		
		if (person.getFirst_name() != null)
			p.setFirst_name(person.getFirst_name());
		
		if (person.getLast_name() != null)
			p.setLast_name(person.getLast_name());
		
		if (person.getPhone_number() != null)
			p.setPhone_number(person.getPhone_number());
		
		if (person.getEmail_address() != null)
			p.setEmail_address(person.getEmail_address());
		
		if (person.getBirthday() != null)
			p.setBirthday(person.getBirthday());
		
		if (person.getLocale() != null)
			p.setLocale(person.getLocale());
		
		p.setRetrieved(new Date());
		
		OrganizationRoleJsonSql.update(context, person.getId(), person.getOrganizational_roles(), tag);
		
		if (person.getRequest_org_id() != null)
			AssignmentJsonSql.update(context, person.getId(), Integer.parseInt(person.getRequest_org_id()), person.getAssignment(), tag);
		
		InterestJsonSql.update(context, person.getId(), person.getInterests(), tag);
		
		EducationJsonSql.update(context, person.getId(), person.getEducation(), tag);
		
		LocationJsonSql.update(context, person.getId(), person.getLocation(), tag);
		
		long id = pd.insertOrReplace(p);
		
		Bundle b = new Bundle();
		if (tag != null) b.putString("tag", tag);
		b.putLong("id", id);
		b.putInt("personId", p.get_id());
		app.getApiNotifier().postMessage(ApiNotifier.Type.UPDATE_PERSON, b);
	}
	
	public static void update(Context context, GContact contact) {
		update(context, contact, null);
	}
	
	public static void update(Context context, GContact contact, String tag) {
		if (contact == null) return;
		update(context, contact.getPerson(), tag);
		
		if (contact.getPerson() != null)
			AnswerJsonSql.update(context, contact.getPerson().getId(), Integer.parseInt(contact.getPerson().getRequest_org_id()), contact.getForm(), tag);
	}
	
	public static void update(Context context, GContactAll contact) {
		update(context, contact, null);
	}
	
	public static void update(Context context, GContactAll contact, String tag) {
		if (contact == null) return;
		if (contact.getPeople() == null) return;
		
		int organizationId = -1;
		
		for (GContact c : contact.getPeople()) {
			update(context, c, tag);
			organizationId = Integer.parseInt(c.getPerson().getRequest_org_id());
		}
		
		KeywordJsonSql.update(context, organizationId, contact.getKeywords(), tag);
		QuestionJsonSql.update(context, organizationId, contact.getQuestions(), tag);
	}
}