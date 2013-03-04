package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Role;
import com.missionhub.model.RoleDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GRoles {

    public GRole[] roles;

    public static final Object lock = new Object();

    /**
     * Saves the contact roles to the SQLite database.
     *
     * @param inTx
     * @return
     * @throws Exception
     */
    public List<Role> save(final boolean inTx) throws Exception {
        final Callable<List<Role>> callable = new Callable<List<Role>>() {
            @Override
            public List<Role> call() throws Exception {
                synchronized (lock) {
                    final List<Long> orgIds = new ArrayList<Long>();
                    boolean deletedSystem = false;

                    final List<Role> retRoles = new ArrayList<Role>();
                    for (final GRole role : roles) {
                        if (role != null) {
                            if (role.organization_id == 0 && !deletedSystem) {
                                final List<Long> keys = Application.getDb().getRoleDao().queryBuilder().where(RoleDao.Properties.Organization_id.eq(0)).listKeys();
                                for(Long key : keys) {
                                    Application.getDb().getRoleDao().deleteByKey(key);
                                }
                                deletedSystem = true;
                            }

                            if (role.organization_id != 0 && !orgIds.contains(role.organization_id)) {
                                final List<Long> keys = Application.getDb().getRoleDao().queryBuilder().where(RoleDao.Properties.Organization_id.eq(role.organization_id)).listKeys();
                                for(Long key : keys) {
                                    Application.getDb().getRoleDao().deleteByKey(key);
                                }
                                orgIds.add(role.organization_id);
                            }

                            final Role r = role.save(true);
                            retRoles.add(r);
                        }
                    }
                    return retRoles;
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