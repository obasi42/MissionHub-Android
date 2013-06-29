package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Interaction;
import com.missionhub.model.InteractionDao;
import com.missionhub.model.InteractionInitiator;
import com.missionhub.model.InteractionInitiatorDao;
import com.missionhub.util.ObjectUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class GInteraction {

    public long id;
    public long interaction_type_id;
    public long receiver_id;
    public Long[] initiator_ids;
    public long organization_id;
    public Long created_by_id;
    public Long updated_by_id;
    public String comment;
    public String privacy_setting;
    public String timestamp;
    public String created_at;
    public String updated_at;
    public String deleted_at;

    public GPerson[] initiators;
    public GInteractionType interaction_type;
    public GPerson receiver;
    public GPerson creator;
    public GPerson last_updater;

    public static final Object lock = new Object();
    public static final Object allLock = new Object();

    /**
     * Saves the interaction to the SQLite database.
     *
     * @param inTx
     * @return
     * @throws Exception
     */
    public Interaction save(final boolean inTx) throws Exception {
        final Callable<Interaction> callable = new Callable<Interaction>() {
            @Override
            public Interaction call() throws Exception {
                synchronized (lock) {
                    final InteractionDao dao = Application.getDb().getInteractionDao();
                    final InteractionInitiatorDao idao = Application.getDb().getInteractionInitiatorDao();

                    Interaction i = dao.load(id);
                    if (deleted_at != null) {
                        if (i != null) {
                            i.deleteWithRelations();
                        }
                        return null;
                    }

                    boolean insert = false;
                    if (i == null) {
                        i = new Interaction();
                        insert = true;
                    }
                    i.setId(id);
                    i.setInteraction_type_id(interaction_type_id);
                    i.setReceiver_id(receiver_id);
                    if (ObjectUtils.isNotEmpty(initiator_ids)) {
                        idao.queryBuilder().where(InteractionInitiatorDao.Properties.Interaction_id.eq(id)).buildDelete().executeDeleteWithoutDetachingEntities();
                        for (long initiator : initiator_ids) {
                            InteractionInitiator ii = new InteractionInitiator();
                            ii.setInteraction_id(id);
                            ii.setPerson_id(initiator);
                            idao.insert(ii);
                        }
                    }
                    i.setOrganization_id(organization_id);
                    i.setCreated_by_id(created_by_id);
                    i.setUpdated_by_id(updated_by_id);
                    i.setComment(comment);
                    i.setPrivacy_setting(privacy_setting);
                    i.setTimestamp(timestamp);
                    i.setCreated_at(created_at);
                    i.setUpdated_at(updated_at);

                    if (ObjectUtils.isNotEmpty(initiators)) {
                        for (GPerson person : initiators) {
                            person.save(true);
                        }
                    }

                    if (ObjectUtils.isNotEmpty(interaction_type)) {
                        interaction_type.save(true);
                    }

                    if (ObjectUtils.isNotEmpty(receiver)) {
                        receiver.save(true);
                    }

                    if (ObjectUtils.isNotEmpty(creator)) {
                        creator.save(true);
                    }

                    if (ObjectUtils.isNotEmpty(last_updater)) {
                        last_updater.save(true);
                    }

                    if (insert) {
                        dao.insert(i);
                    } else {
                        dao.update(i);
                    }

                    return i;
                }
            }
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

    /**
     * Saves a list of interactions
     *
     * @param interactions
     * @param inTx
     * @return list of saved interactions
     * @throws Exception
     */
    public static List<Interaction> replaceAll(final GInteraction[] interactions, final long receiverId, final long organization_id, final boolean inTx) throws Exception {
        final Callable<List<Interaction>> callable = new Callable<List<Interaction>>() {
            @Override
            public List<Interaction> call() throws Exception {
                synchronized (allLock) {

                    InteractionDao dao = Application.getDb().getInteractionDao();

                    // delete old interactions
                    List<Long> oldIds = dao.queryBuilder().where(InteractionDao.Properties.Receiver_id.eq(receiverId), InteractionDao.Properties.Organization_id.eq(organization_id)).listKeys();
                    for (long id : oldIds) {
                        dao.deleteByKey(id);
                    }

                    final List<Interaction> c = new ArrayList<Interaction>();
                    for (final GInteraction interaction : interactions) {
                        final Interaction i = interaction.save(true);
                        if (i != null) {
                            c.add(i);
                        }
                    }
                    return c;
                }
            }
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

    public void toParams(final Map<String, String> params) {
        if (id > 0) {
            params.put("interaction[id]", String.valueOf(id));
        }
        if (interaction_type_id > 0) {
            params.put("interaction[interaction_type_id]", String.valueOf(interaction_type_id));
        }
        if (receiver_id > 0) {
            params.put("interaction[receiver_id]", String.valueOf(receiver_id));
        }
        if (ObjectUtils.isNotEmpty(initiator_ids)) {
            params.put("interaction[initiator_ids]", StringUtils.join(initiator_ids, ','));
        }
        if (organization_id > 0) {
            params.put("interaction[organization_id]", String.valueOf(organization_id));
        }
        if (comment != null) {
            params.put("interaction[comment]", comment);
        }
        if (privacy_setting != null) {
            params.put("interaction[privacy_setting]", privacy_setting);
        }
        if (timestamp != null) {
            params.put("interaction[timestamp]", timestamp);
        }
    }

}