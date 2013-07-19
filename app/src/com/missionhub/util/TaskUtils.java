package com.missionhub.util;

import com.missionhub.ui.drilldown.DrillDownItem;

import java.util.List;

public class TaskUtils {

    public static boolean cancel(SafeAsyncTask ... tasks) {
        boolean ok = true;
        for(SafeAsyncTask task : tasks) {
            try {
                if (task != null) {
                    ok = task.cancel(true) && ok;
                }
            } catch (Exception e) { ok = false; }
        }
        return ok;
    }
}
