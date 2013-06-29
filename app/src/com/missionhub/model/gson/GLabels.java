package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GLabels {

    public GLabel[] labels;

    public List<Label> save(final boolean inTx) throws Exception {
        final Callable<List<Label>> callable = new Callable<List<Label>>() {
            @Override
            public List<Label> call() throws Exception {
                final List<Label> lbls = new ArrayList<Label>();

                if (labels != null) {
                    for (final GLabel label : labels) {
                        lbls.add(label.save(true));
                    }
                }

                return lbls;
            }
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

}
