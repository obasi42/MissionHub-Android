package com.missionhub.sql.convert;

import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.json.GAssign;
import com.missionhub.api.json.GIdNameProvider;
import com.missionhub.api.json.GPerson;
import com.missionhub.sql.Assignment;
import com.missionhub.sql.AssignmentDao;
import com.missionhub.sql.AssignmentDao.Properties;


public class AssignmentJsonSql {
	
	public static void update(Context context, int personId, int organizationId, GAssign assign) {
		if (assign == null) return;
		
		MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		AssignmentDao ad = app.getDbSession().getAssignmentDao();
		
		// Delete all current stored assignments for this contact
		List<Assignment> currentAssignments = ad.queryBuilder().where(Properties.Organization_id.eq(organizationId), Properties.Assigned_to_id.eq(personId)).list();
		Iterator<Assignment> itr = currentAssignments.iterator();
		while(itr.hasNext()) {
			ad.delete(itr.next());
		}
		
		GIdNameProvider[] assignedToPeople = assign.getAssigned_to_person();
		for (GIdNameProvider assignedToPerson : assignedToPeople) {
			Assignment assignment = new Assignment();
			assignment.setAssigned_to_id(personId);
			assignment.setPerson_id(Integer.valueOf(assignedToPerson.getId()));
			assignment.setOrganization_id(organizationId);
			ad.insert(assignment);
			
			// Add person stubs
			GPerson person = new GPerson();
			person.setId(Integer.valueOf(assignedToPerson.getId()));
			person.setName(assignedToPerson.getName());
			PersonJsonSql.update(context, person);
		}
	}
	
}