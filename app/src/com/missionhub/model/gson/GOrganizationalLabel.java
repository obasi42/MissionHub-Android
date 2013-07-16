package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.model.OrganizationalLabel;
import com.missionhub.model.OrganizationalLabelDao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class GOrganizationalLabel {

    public long id;
    public long person_id;
    public long organization_id;
    public Long added_by_id;
    public long label_id;
    public String start_date;
    public String created_at;
    public String updated_at;
    public String removed_date;

    public GLabel label;

    public static final Object lock = new Object();

    /**
     * Saves an organizational role to the SQLite database.
     *
     * @param inTx
     * @return
     * @throws Exception
     */
    public OrganizationalLabel save(final boolean inTx) throws Exception {
        final Callable<OrganizationalLabel> callable = new Callable<OrganizationalLabel>() {
            @Override
            public OrganizationalLabel call() throws Exception {
                synchronized (lock) {
                    final OrganizationalLabelDao dao = Application.getDb().getOrganizationalLabelDao();

                    if (removed_date != null) {
                        dao.deleteByKey(id);
                        return null;
                    }

                    final OrganizationalLabel orglabel = new OrganizationalLabel();
                    orglabel.setId(id);
                    orglabel.setPerson_id(person_id);
                    orglabel.setOrganization_id(organization_id);
                    orglabel.setAdded_by_id(added_by_id);
                    orglabel.setLabel_id(label_id);
                    orglabel.setStart_date(start_date);
                    orglabel.setCreated_at(created_at);
                    orglabel.setUpdated_at(updated_at);

                    if (label != null) {
                        label.save(true);
                    }

                    dao.insertOrReplace(orglabel);

                    return orglabel;
                }
            }
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

    public static List<OrganizationalLabel> replaceAll(final GOrganizationalLabel[] labels, final long personId, final boolean inTx) throws Exception {
        final Callable<List<OrganizationalLabel>> callable = new Callable<List<OrganizationalLabel>>() {
            @Override
            public List<OrganizationalLabel> call() throws Exception {
                synchronized (lock) {
                    final OrganizationalLabelDao dao = Application.getDb().getOrganizationalLabelDao();


                    Set<Long> orgIds = new HashSet<Long>();
                    for (GOrganizationalLabel label : labels) {
                        orgIds.add(label.organization_id);
                    }
                    orgIds.add(Session.getInstance().getOrganizationId());
                    List<Long> oldIds = dao.queryBuilder().where(OrganizationalLabelDao.Properties.Organization_id.in(orgIds), OrganizationalLabelDao.Properties.Person_id.eq(personId)).listKeys();
                    for (Long id : oldIds) {
                        dao.deleteByKey(id);
                    }

                    final List<OrganizationalLabel> lbls = new ArrayList<OrganizationalLabel>();
                    for (final GOrganizationalLabel glabel : labels) {
                        final OrganizationalLabel label = glabel.save(true);
                        if (label != null) {
                            lbls.add(label);
                        }
                    }
                    return lbls;
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