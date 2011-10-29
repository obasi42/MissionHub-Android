package com.missionhub.sql.convert;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.Bundle;

import com.google.common.primitives.Ints;
import com.missionhub.Application;
import com.missionhub.api.ApiNotifier;
import com.missionhub.model.json.GOrgGeneric;
import com.missionhub.model.sql.Organization;
import com.missionhub.model.sql.OrganizationDao;
import com.missionhub.model.sql.OrganizationalRole;
import com.missionhub.model.sql.OrganizationalRoleDao;
import com.missionhub.model.sql.OrganizationalRoleDao.Properties;

public class OrganizationRoleJsonSql {
	
	public static void update(Context context, int personId, GOrgGeneric[] roles) {
		update(context, personId, roles, null);
	}
	
	public static void update(Context context, int personId, GOrgGeneric[] roles, String tag) {
		if (roles == null) return;
		
		Application app = (Application) context.getApplicationContext();
		OrganizationalRoleDao ord = app.getDbSession().getOrganizationalRoleDao();
		OrganizationDao od = app.getDbSession().getOrganizationDao();
		
		// Delete all current stored roles
		List<OrganizationalRole> currentRoles = ord.queryBuilder().where(Properties.Person_id.eq(personId)).list();
		Iterator<OrganizationalRole> itr = currentRoles.iterator();
		while(itr.hasNext()) {
			OrganizationalRole or = itr.next();
			ord.delete(or);
			
			Bundle b = new Bundle();
			b.putLong("id", or.get_id());
			b.putInt("personId", or.getPerson_id());
			b.putInt("organizationId", or.getOrg_id());
			if (tag != null) b.putString("tag", tag);
			app.getApiNotifier().postMessage(ApiNotifier.Type.DELETE_ORGANIZATIONAL_ROLE, b);
		}
		
		// Insert new roles
		for (GOrgGeneric role : roles) {
			OrganizationalRole or = new OrganizationalRole();
			or.setName(role.getName());
			or.setOrg_id(role.getOrg_id());
			or.setPerson_id(personId);
			if (role.getPrimary() != null)
				or.setPrimary(Boolean.parseBoolean(role.getPrimary()));
			or.setRole(role.getRole());
			long id = ord.insert(or);
			
			Bundle b = new Bundle();
			b.putLong("id", id);
			b.putInt("personId", or.getPerson_id());
			b.putInt("organizationId", or.getOrg_id());
			if (tag != null) b.putString("tag", tag);
			app.getApiNotifier().postMessage(ApiNotifier.Type.UPDATE_ORGANIZATIONAL_ROLE, b);
			
			// insert organization stub
			Organization org = od.load(role.getOrg_id());
			if (org == null) org = new Organization();
			org.set_id(role.getOrg_id());
			org.setName(role.getName());
			long id2 = od.insertOrReplace(org);
			
			Bundle b2 = new Bundle();
			b.putLong("id", id2);
			b.putInt("organizationId", Ints.checkedCast(id2));
			if (tag != null) b.putString("tag", tag);
			app.getApiNotifier().postMessage(ApiNotifier.Type.UPDATE_ORGANIZATION, b2);
		}
	}	
}