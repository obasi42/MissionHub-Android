package com.missionhub.android.model.gson;

import com.missionhub.android.application.Application;
import com.missionhub.android.model.Role;

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
                    final List<Role> retRoles = new ArrayList<Role>();
                    for (final GRole role : roles) {
                        final Role r = role.save(true);
                        if (r != null) {
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