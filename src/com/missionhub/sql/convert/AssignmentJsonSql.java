package com.missionhub.sql.convert;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.Bundle;

import com.missionhub.Application;
import com.missionhub.api.ApiNotifier;
import com.missionhub.model.json.GAssign;
import com.missionhub.model.json.GIdNameProvider;
import com.missionhub.model.json.GPerson;
import com.missionhub.model.sql.Assignment;
import com.missionhub.model.sql.AssignmentDao;
import com.missionhub.model.sql.AssignmentDao.Properties;


public class AssignmentJsonSql {
	
	public static void update(Context context, int personId, int organizationId, GAssign assign) {
		update(context, personId, organizationId, assign, null);
	}
	
	public static void update(Context context, int personId, int organizationId, GAssign assign, String tag) {
		if (assign == null) return;
		
		Application app = (Application) context.getApplicationContext();
		AssignmentDao ad = app.getDbSession().getAssignmentDao();
		
		// Delete all current stored assignments for this contact
		List<Assignment> currentAssignments = ad.queryBuilder().where(Properties.Organization_id.eq(organizationId), Properties.Assigned_to_id.eq(personId)).list();
		Iterator<Assignment> itr = currentAssignments.iterator();
		while(itr.hasNext()) {
			Assignment assignment = itr.next();
			ad.delete(assignment);
			
			Bundle b = new Bundle();
			b.putLong("id", assignment.get_id());
			b.putInt("personId", assignment.getPerson_id());
			b.putInt("assignedToPersonId", assignment.getAssigned_to_id());
			if (tag != null) b.putString("tag", tag);
			app.getApiNotifier().postMessage(ApiNotifier.Type.DELETE_ASSIGNMENT, b);
		}
		
		GIdNameProvider[] assignedToPeople = assign.getAssigned_to_person();
		for (GIdNameProvider assignedToPerson : assignedToPeople) {
			Assignment assignment = new Assignment();
			assignment.setAssigned_to_id(personId);
			assignment.setPerson_id(Integer.valueOf(assignedToPerson.getId()));
			assignment.setOrganization_id(organizationId);
			long id = ad.insert(assignment);
			
			Bundle b = new Bundle();
			b.putLong("id", id);
			b.putInt("personId", assignment.getPerson_id());
			b.putInt("assignedToPersonId", assignment.getAssigned_to_id());
			if (tag != null) b.putString("tag", tag);
			app.getApiNotifier().postMessage(ApiNotifier.Type.UPDATE_ASSIGNMENT, b);
			
			// Add person stubs
			GPerson person = new GPerson();
			person.setId(Integer.valueOf(assignedToPerson.getId()));
			person.setName(assignedToPerson.getName());
			PersonJsonSql.update(context, person, tag);
		}
	}
	
}