package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GPermissions {

    public GPermission[] permissions;

    public List<Permission> save(final boolean inTx) throws Exception {
        final Callable<List<Permission>> callable = new Callable<List<Permission>>() {
            @Override
            public List<Permission> call() throws Exception {
                final List<Permission> perms = new ArrayList<Permission>();

                if (permissions != null) {
                    for (final GPermission permission : permissions) {
                        perms.add(permission.save(true));
                    }
                }

                return perms;
            }
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

}
