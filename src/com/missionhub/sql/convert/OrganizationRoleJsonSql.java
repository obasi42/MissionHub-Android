package com.missionhub.sql.convert;

import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.json.GOrgGeneric;
import com.missionhub.sql.Organization;
import com.missionhub.sql.OrganizationDao;
import com.missionhub.sql.OrganizationalRole;
import com.missionhub.sql.OrganizationalRoleDao;
import com.missionhub.sql.OrganizationalRoleDao.Properties;

public class OrganizationRoleJsonSql {
	
	public static void update(Context context, int personId, GOrgGeneric[] roles) {
		if (roles == null) return;
		
		MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		OrganizationalRoleDao ord = app.getDbSession().getOrganizationalRoleDao();
		OrganizationDao od = app.getDbSession().getOrganizationDao();
		
		// Delete all current stored roles
		List<OrganizationalRole> currentRoles = ord.queryBuilder().where(Properties.Person_id.eq(personId)).list();
		Iterator<OrganizationalRole> itr = currentRoles.iterator();
		while(itr.hasNext()) {
			ord.delete(itr.next());
		}
		
		// Insert new roles
		for (GOrgGeneric role : roles) {
			OrganizationalRole or = new OrganizationalRole();
			or.setName(role.getName());
			or.setOrg_id(role.getOrg_id());
			or.setPerson_id(personId);
			or.setPrimary(Boolean.getBoolean(role.getPrimary()));
			or.setRole(role.getRole());
			ord.insert(or);
			
			// insert organization stub
			Organization org = od.load(role.getOrg_id());
			if (org == null) org = new Organization();
			org.set_id(role.getOrg_id());
			org.setName(role.getName());
			od.insertOrReplace(org);
		}
	}	
}