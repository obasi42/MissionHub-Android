package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.InteractionType;
import com.missionhub.model.InteractionTypeDao;

import java.util.concurrent.Callable;

public class GInteractionType {

    public static final Object lock = new Object();

    public long id;
    public long organization_id;
    public String name;
    public String i18n;
    public String icon;
    public String created_at;
    public String updated_at;

    /**
     * Saves an interaction type to the SQLite database.
     *
     * @param inTx
     * @return
     * @throws Exception
     */
    public InteractionType save(final boolean inTx) throws Exception {
        final Callable<InteractionType> callable = new Callable<InteractionType>() {
            @Override
            public InteractionType call() throws Exception {
                synchronized (lock) {
                    final InteractionTypeDao dao = Application.getDb().getInteractionTypeDao();

                    final InteractionType type = new InteractionType();
                    type.setId(id);
                    type.setOrganization_id(organization_id);
                    type.setName(name);
                    type.setI18n(i18n);
                    type.setIcon(icon);
                    type.setCreated_at(created_at);
                    type.setUpdated_at(updated_at);

                    dao.insertOrReplace(type);

                    return type;
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
