package com.missionhub.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.OrganizationalRole;
import com.missionhub.model.OrganizationalRoleDao;
import com.missionhub.util.U;

public class GOrganizationalRole {

	public long id;
	public long person_id;
	public long organization_id;
	public String followup_status;
	public long role_id;
	public String start_date;
	public String created_at;
	public String updated_at;
	public String archive_date;

	public static final Object lock = new Object();

	/**
	 * Saves an organizational role to the SQLite database.
	 * 
	 * @param inTx
	 * @return
	 * @throws Exception
	 */
	public OrganizationalRole save(final boolean inTx) throws Exception {
		final Callable<OrganizationalRole> callable = new Callable<OrganizationalRole>() {
			@Override
			public OrganizationalRole call() throws Exception {
				synchronized (lock) {
					final OrganizationalRoleDao dao = Application.getDb().getOrganizationalRoleDao();

					if (archive_date != null) {
						dao.deleteByKey(id);
						return null;
					}

					boolean insert = false;
					OrganizationalRole role = dao.load(id);
					if (role == null) {
						role = new OrganizationalRole();
						insert = true;
					}

					role.setId(id);
					role.setPerson_id(person_id);
					role.setOrganization_id(organization_id);
					role.setFollowup_status(followup_status);
					role.setRole_id(role_id);
					role.setStart_date(U.parseYMD(start_date));
					role.setCreated_at(U.parseISO8601(created_at));
					role.setUpdated_at(U.parseISO8601(updated_at));

					if (insert) {
						dao.insert(role);
					} else {
						dao.update(role);
					}

					return role;
				}
			}
		};
		if (inTx) {
			return callable.call();
		} else {
			return Application.getDb().callInTx(callable);
		}
	}

}