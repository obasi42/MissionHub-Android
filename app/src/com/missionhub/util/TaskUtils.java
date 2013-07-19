package com.missionhub.util;

import java.util.concurrent.FutureTask;

public class TaskUtils {

    public static boolean cancel(SafeAsyncTask... tasks) {
        boolean ok = true;
        for (SafeAsyncTask task : tasks) {
            try {
                if (task != null) {
                    ok = task.cancel(true) && ok;
                }
            } catch (Exception e) {
                ok = false;
            }
        }
        return ok;
    }

    public static boolean cancel(FutureTask... tasks) {
        boolean ok = true;
        for (FutureTask task : tasks) {
            try {
                if (task != null) {
                    ok = task.cancel(true) && ok;
                }
            } catch (Exception e) {
                ok = false;
            }
        }
        return ok;
    }
}
