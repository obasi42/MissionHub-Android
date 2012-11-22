package com.missionhub.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.Organization;
import com.missionhub.model.OrganizationDao;
import com.missionhub.model.OrganizationalRole;
import com.missionhub.model.OrganizationalRoleDao;
import com.missionhub.model.Person;
import com.missionhub.util.U;

import de.greenrobot.dao.CloseableListIterator;
import de.greenrobot.dao.LazyList;

public class GOrgGeneric {
	public Long org_id;
	public String name;
	public String primary;
	public String role;

	/**
	 * Saves the generic orgs block.
	 * 
	 * @param clss
	 * @param generics
	 * @param p
	 * @throws Exception
	 */
	public static void save(final Class<?> clss, final GOrgGeneric[] generics, final Person p, final boolean inTx) throws Exception {
		final Callable<Void> callable = new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				if (clss == OrganizationalRole.class) {
					saveOrganizationRales(generics, p);
				} else if (clss == Organization.class) {
					saveOrganizations(generics, p);
				}
				return null;
			}

		};
		if (!inTx) {
			Application.getDb().callInTx(callable);
		} else {
			callable.call();
		}
	}

	private static void saveOrganizations(final GOrgGeneric[] generics, final Person p) {
		if (generics == null || p == null) return;

		final OrganizationDao od = Application.getDb().getOrganizationDao();
		// insert or update the records
		for (final GOrgGeneric org : generics) {
			if (org == null) continue;

			Organization o = od.load(org.org_id);
			if (o == null) {
				o = new Organization();
			}

			if (!U.isNullEmptyNegative(org.org_id)) o.setId(org.org_id);
			if (!U.isNullEmpty(org.name)) o.setName(org.name);

			od.insertOrReplace(o);
		}
	}

	private static void saveOrganizationRales(final GOrgGeneric[] generics, final Person p) {
		if (generics == null || p == null) return;

		final OrganizationalRoleDao ord = Application.getDb().getOrganizationalRoleDao();

		// delete current records as we don't have stable id's currently
		final LazyList<OrganizationalRole> delRoles = ord.queryBuilder().where(com.missionhub.model.OrganizationalRoleDao.Properties.Person_id.eq(p.getId())).listLazyUncached();
		final CloseableListIterator<OrganizationalRole> itr = delRoles.listIteratorAutoClose();
		while (itr.hasNext()) {
			final OrganizationalRole role = itr.next();
			ord.delete(role);
		}

		// make sure we have a org to reference
		saveOrganizations(generics, p);

		// insert the new records
		for (final GOrgGeneric role : generics) {
			if (generics == null) continue;

			final OrganizationalRole or = new OrganizationalRole();
			if (!U.isNullEmptyNegative(p, p.getId())) or.setPerson_id(p.getId());
			or.setPrimary(Boolean.parseBoolean(role.primary));
			if (!U.isNullEmpty(role.role)) or.setRole(role.role);
			if (!U.isNullEmptyNegative(role.org_id)) or.setOrganization_id(role.org_id);
			ord.insert(or);
		}
	}
}
