package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.InteractionType;
import com.missionhub.model.InteractionTypeDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GInteractionType {

    public GInteractionType interaction_type;

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
                // wrapped type
                if (interaction_type != null) {
                    interaction_type.save(true);
                }

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
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

    public static List<InteractionType> replaceAll(final GInteractionType[] types, final boolean inTx) throws Exception {
        final ArrayList<Long> orgIds = new ArrayList<Long>();
        for (GInteractionType type : types) {
            orgIds.add(type.id);
        }
        final Callable<List<InteractionType>> callable = new Callable<List<InteractionType>>() {
            @Override
            public List<InteractionType> call() throws Exception {
                final InteractionTypeDao dao = Application.getDb().getInteractionTypeDao();

                // delete current types
                List<Long> keys = dao.queryBuilder().where(InteractionTypeDao.Properties.Organization_id.in(orgIds)).listKeys();
                for (Long key : keys) {
                    dao.deleteByKey(key);
                }

                // save the new types
                final List<InteractionType> interactionTypes = new ArrayList<InteractionType>();
                for (final GInteractionType gType : types) {
                    final InteractionType type = gType.save(true);
                    if (type != null) {
                        interactionTypes.add(type);
                    }
                }

                return interactionTypes;
            }
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }

    }
}
