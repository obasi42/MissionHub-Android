package com.missionhub.application;

import android.util.Log;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UpgradeManager {

    public static final String TAG = UpgradeManager.class.getSimpleName();
    private static UpgradeManager sUpgradeManager;

    private UpgradeManager() {
    }

    /*=========================*/
    /* Dynamic Upgrade Methods */
    /*=========================*/

    public boolean toV45() {
        // wipe everything from MH v1
        clearApplicationData();
        return true;
    }

    /*========================*/
    /* UpgradeManager Methods */
    /*========================*/
    public static UpgradeManager getInstance() {
        if (sUpgradeManager == null) {
            sUpgradeManager = new UpgradeManager();
        }
        return sUpgradeManager;
    }

    public static void doUpgrade() {

        int from = SettingsManager.getApplicationLastVersionId();
        int to = Application.getVersionCode();

        while (from < to) {
            try {
                Method method = getInstance().getClass().getMethod("toV" + (from + 1), new Class[0]);
                if (!(Boolean) method.invoke(getInstance())) {
                    Log.i(TAG, "failed upgrade " + "toV" + (from + 1));
                    getInstance().onFailure(to);
                    break;
                } else {
                    Log.i(TAG, "completed upgrade " + "toV" + (from + 1));
                }
            } catch (InvocationTargetException e) {
                getInstance().onFailure(to, e);
                break;
            } catch (Exception e) {
                /* ignore */
            }
            from++;
        }

        getInstance().onSuccess(to);
    }

    private void onFailure(int to) {
        Log.w(TAG, "Upgrade to v" + to + " failed.");
        clearApplicationData();
    }

    private void onFailure(int to, Throwable t) {
        Log.w(TAG, "Upgrade to v" + to + " failed. Resetting app...", t);
        clearApplicationData();
    }

    private void onSuccess(int to) {
        Log.i(TAG, "Upgrade to v" + to + " completed.");
        SettingsManager.setApplicationLastVersionId(to);
    }

    private void clearApplicationData() {
        Log.e(TAG, "Clearing application data...");

        Application.closeDb();

        File cache = Application.getContext().getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.d(TAG, "Deleted " + appDir.getAbsolutePath() + "/" + s);
                }
            }
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

}