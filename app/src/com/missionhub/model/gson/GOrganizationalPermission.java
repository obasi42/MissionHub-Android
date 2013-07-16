package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.OrganizationalPermission;
import com.missionhub.model.OrganizationalPermissionDao;

import java.util.List;
import java.util.concurrent.Callable;

public class GOrganizationalPermission {

    public GOrganizationalPermission organizational_permission;

    public long id;
    public long person_id;
    public long permission_id;
    public long organization_id;
    public String followup_status;
    public String start_date;
    public String created_at;
    public String updated_at;
    public String archive_date;

    public GPermission permission;

    public static final Object lock = new Object();

    /**
     * Saves an organizational role to the SQLite database.
     *
     * @param inTx
     * @return
     * @throws Exception
     */
    public OrganizationalPermission save(final boolean inTx) throws Exception {
        final Callable<OrganizationalPermission> callable = new Callable<OrganizationalPermission>() {
            @Override
            public OrganizationalPermission call() throws Exception {
                synchronized (lock) {

                    // wrapped permission
                    if (organizational_permission != null) {
                        organizational_permission.save(true);
                    }

                    final OrganizationalPermissionDao dao = Application.getDb().getOrganizationalPermissionDao();

                    if (archive_date != null) {
                        dao.deleteByKey(id);
                        return null;
                    }

                    final OrganizationalPermission orgperm = new OrganizationalPermission();
                    orgperm.setId(id);
                    orgperm.setPerson_id(person_id);
                    orgperm.setOrganization_id(organization_id);
                    orgperm.setFollowup_status(followup_status);
                    orgperm.setPermission_id(permission_id);
                    orgperm.setStart_date(start_date);
                    orgperm.setCreated_at(created_at);
                    orgperm.setUpdated_at(updated_at);

                    if (permission != null) {
                        permission.save(true);
                    }

                    dao.insertOrReplace(orgperm);

                    return orgperm;
                }
            }
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

    public static OrganizationalPermission replace(final GOrganizationalPermission permission, final boolean inTx) throws Exception {
        final Callable<OrganizationalPermission> callable = new Callable<OrganizationalPermission>() {
            @Override
            public OrganizationalPermission call() throws Exception {
                synchronized (lock) {
                    final OrganizationalPermissionDao dao = Application.getDb().getOrganizationalPermissionDao();

                    List<Long> oldIds = dao.queryBuilder().where(OrganizationalPermissionDao.Properties.Organization_id.eq(permission.organization_id), OrganizationalPermissionDao.Properties.Person_id.eq(permission.person_id)).listKeys();
                    for (Long id : oldIds) {
                        dao.deleteByKey(id);
                    }

                    return permission.save(true);
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