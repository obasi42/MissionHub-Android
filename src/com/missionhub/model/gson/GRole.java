package com.missionhub.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.Role;
import com.missionhub.model.RoleDao;
import com.missionhub.network.HttpParams;
import com.missionhub.util.U;

public class GRole {

	public long id;
	public long organization_id;
	public String name;
	public String i18n;
	public String created_at;
	public String updated_at;

	public static final Object lock = new Object();

	/**
	 * Saves the role to the SQLite database.
	 * 
	 * @param inTx
	 * @return
	 * @throws Exception
	 */
	public Role save(final boolean inTx) throws Exception {
		final Callable<Role> callable = new Callable<Role>() {
			@Override
			public Role call() throws Exception {
				synchronized (lock) {
					final RoleDao dao = Application.getDb().getRoleDao();

					final Role role = new Role();
					role.setId(id);
					role.setOrganization_id(organization_id);
					role.setName(name);
					role.setI18n(i18n);
					role.setCreated_at(U.parseISO8601(created_at));
					role.setUpdated_at(U.parseISO8601(updated_at));
					dao.insertOrReplace(role);

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

	public void toParams(final HttpParams params) {
		if (id > 0) {
			params.add("role[id]", id);
		}
		if (organization_id > 0) {
			params.add("role[organization_id]", organization_id);
		}
		if (!U.isNullEmpty(name)) {
			params.add("role[name]", name);
		}
		if (!U.isNullEmpty(i18n)) {
			params.add("role[i18n]", i18n);
		}
	}

}