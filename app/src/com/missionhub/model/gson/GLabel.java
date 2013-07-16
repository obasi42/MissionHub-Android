package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Label;
import com.missionhub.model.LabelDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class GLabel {

    public GLabel label;

    public long id;
    public long organization_id;
    public String name;
    public String i18n;
    public String created_at;
    public String updated_at;

    public static final Object lock = new Object();

    public Label save(final boolean inTx) throws Exception {
        final Callable<Label> callable = new Callable<Label>() {
            @Override
            public Label call() throws Exception {
                synchronized (lock) {

                    // wrapped label
                    if (label != null) {
                        label.save(true);
                    }

                    final LabelDao dao = Application.getDb().getLabelDao();

                    final Label label = new Label();
                    label.setId(id);
                    label.setOrganization_id(organization_id);
                    label.setName(name);
                    label.setI18n(i18n);
                    label.setCreated_at(created_at);
                    label.setUpdated_at(updated_at);
                    dao.insertOrReplace(label);

                    return label;
                }
            }
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

    public static List<Label> replaceAll(final GLabel[] labels, final long organization_id, final boolean inTx) throws Exception {
        final Callable<List<Label>> callable = new Callable<List<Label>>() {
            @Override
            public List<Label> call() throws Exception {
                synchronized (lock) {
                    final LabelDao dao = Application.getDb().getLabelDao();
                    List<Long> oldIds = dao.queryBuilder().where(LabelDao.Properties.Organization_id.eq(organization_id)).listKeys();
                    for (Long id : oldIds) {
                        dao.deleteByKey(id);
                    }

                    final List<Label> lbls = new ArrayList<Label>();
                    for (final GLabel Label : labels) {
                        final Label label = Label.save(true);
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

    public void toParams(Map<String, String> params) {
        if (id > 0) {
            params.put("label[id]", String.valueOf(id));
        }
        if (organization_id > 0) {
            params.put("label[organization_id]", String.valueOf(organization_id));
        }
        if (name != null) {
            params.put("label[name]", name);
        }
        if (i18n != null) {
            params.put("label[i18n]", i18n);
        }
    }
}
