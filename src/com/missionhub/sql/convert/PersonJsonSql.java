package com.missionhub.sql.convert;

import java.util.Date;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.json.GContact;
import com.missionhub.api.json.GContactAll;
import com.missionhub.api.json.GPerson;
import com.missionhub.auth.User;
import com.missionhub.helpers.U;
import com.missionhub.sql.Person;
import com.missionhub.sql.PersonDao;

import android.content.Context;

public class PersonJsonSql {
	
	public static void update(Context context, GPerson person) {
		if (person == null) return;
		
		MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
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
			p.setFirst_contact_date(new Date(person.getFirst_contact_date()));
		
		if (!U.nullOrEmpty(person.getDate_surveyed()))
			p.setDate_surveyed(new Date(person.getDate_surveyed()));
		
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
		
		OrganizationRoleJsonSql.update(context, person.getId(), person.getOrganizational_roles());
		AssignmentJsonSql.update(context, person.getId(), Integer.parseInt(person.getRequest_org_id()), person.getAssignment());
		InterestJsonSql.update(context, person.getId(), person.getInterests());
		EducationJsonSql.update(context, person.getId(), person.getEducation());
		LocationJsonSql.update(context, person.getId(), person.getLocation());
		
		pd.insertOrReplace(p);
	}
	
	public static void update(Context context, GContact contact) {
		if (contact == null) return;
		update(context, contact.getPerson());
		
		if (contact.getPerson() != null)
			AnswerJsonSql.update(context, contact.getPerson().getId(), Integer.parseInt(contact.getPerson().getRequest_org_id()), contact.getForm());
	}
	
	public static void update(Context context, GContactAll contact) {
		if (contact == null) return;
		if (contact.getPeople() == null) return;
		
		int organizationId = User.getOrganizationID();
		
		for (GContact c : contact.getPeople()) {
			update(context, c);
			organizationId = Integer.parseInt(c.getPerson().getRequest_org_id());
		}
		
		KeywordJsonSql.update(context, organizationId, contact.getKeywords());
		QuestionJsonSql.update(context, organizationId, contact.getQuestions());
	}
}