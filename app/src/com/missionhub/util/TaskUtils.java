package com.missionhub.util;

public class TaskUtils {

    public static boolean cancel(SafeAsyncTask task) {
        try {
            if (task != null) {
                return task.cancel(true);
            }
        } catch (Exception e) { /* ignore */ }
        return false;
    }

}
