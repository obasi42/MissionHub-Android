package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Permission;
import com.missionhub.model.PermissionDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GPermission {

    public GPermission permission;

    public long id;
    public String name;
    public String i18n;
    public String created_at;
    public String updated_at;

    public static final Object lock = new Object();

    public Permission save(final boolean inTx) throws Exception {
        final Callable<Permission> callable = new Callable<Permission>() {
            @Override
            public Permission call() throws Exception {
                synchronized (lock) {
                    // wrapped permission
                    if (permission != null) {
                        return permission.save(true);
                    }

                    final PermissionDao dao = Application.getDb().getPermissionDao();

                    final Permission permission = new Permission();
                    permission.setId(id);
                    permission.setName(name);
                    permission.setI18n(i18n);
                    permission.setCreated_at(created_at);
                    permission.setUpdated_at(updated_at);
                    dao.insertOrReplace(permission);

                    return permission;
                }
            }
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

    public static List<Permission> replaceAll(final GPermission[] permissions, final boolean inTx) throws Exception {
        final Callable<List<Permission>> callable = new Callable<List<Permission>>() {
            @Override
            public List<Permission> call() throws Exception {
                synchronized (lock) {
                    final PermissionDao dao = Application.getDb().getPermissionDao();
                    dao.deleteAll();

                    // save the new number
                    final List<Permission> perms = new ArrayList<Permission>();
                    for (final GPermission permission : permissions) {
                        final Permission perm = permission.save(true);
                        if (perm != null) {
                            perms.add(perm);
                        }
                    }
                    return perms;
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
