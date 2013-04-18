package com.missionhub.application;

import android.util.Log;
import com.google.gson.Gson;
import com.missionhub.model.Setting;
import com.missionhub.model.SettingDao;
import com.missionhub.model.UserSetting;
import com.missionhub.model.UserSettingDao;
import org.apache.commons.lang3.ClassUtils;

/**
 * Manages the user's settings
 */
public class SettingsManager {

    private static SettingsManager sSettingsManager;
    private static Gson sGson;

    private SettingsManager() {
    }

    public synchronized static SettingsManager getInstance() {
        if (sSettingsManager == null) {
            sSettingsManager = new SettingsManager();
        }
        return sSettingsManager;
    }

    public synchronized static Gson getGsonInstance() {
        if (sGson == null) {
            sGson = new Gson();
        }
        return sGson;
    }

    // |**********************************|//
    // |******** SETTINGS METHODS ********|//
    // |**********************************|//

    public Setting findSettingByKey(final String key) {
        Setting setting = Application.getDb().getSettingDao().queryBuilder().where(SettingDao.Properties.Key.eq(key)).unique();
        if (setting == null) {
            setting = new Setting();
            setting.setKey(key);
        }
        return setting;
    }

    public <T> T getSetting(final String key) {
        return getSetting(key, null);
    }

    public <T> T getSetting(final String key, final T def) {
        return getSetting(key, def, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getSetting(final String key, final T def, Class<T> type) {
        final Setting setting = findSettingByKey(key);
        if (setting.getValue() != null) {
            try {
                if (type == null) {
                    if (def == null) {
                        return def;
                    }
                    type = (Class<T>) def.getClass();
                }
                return getGsonInstance().fromJson(setting.getValue(), type);
            } catch (Exception e) {
                Log.e("SettingManager", e.getMessage(), e);
            /* ignore */
            }
        }
        return def;
    }

    public void setSetting(final String key, final Object value) {
        final Setting setting = findSettingByKey(key);
        setting.setValue(getGsonInstance().toJson(value));
        Application.getDb().getSettingDao().insertOrReplace(setting);
    }

    public void deleteSetting(final String key) {
        final Setting setting = findSettingByKey(key);
        if (setting != null) Application.getDb().getSettingDao().delete(setting);
    }

    // |**********************************|//
    // |***** USER SETTINGS METHODS ******|//
    // |**********************************|//

    public UserSetting findUserSettingByKey(final long personId, final String key) {
        UserSetting setting = Application.getDb().getUserSettingDao().queryBuilder().where(UserSettingDao.Properties.Key.eq(key), UserSettingDao.Properties.Person_id.eq(personId)).unique();
        if (setting == null) {
            setting = new UserSetting();
            setting.setPerson_id(personId);
            setting.setKey(key);
        }
        return setting;
    }

    public <T> T getUserSetting(final long personId, final String key) {
        return getUserSetting(personId, key, null);
    }

    public <T> T getUserSetting(final long personId, final String key, final T def) {
        return getUserSetting(personId, key, def, null);
    }

    public <T> T getUserSetting(final long personId, final String key, final T def, Class<T> type) {
        final UserSetting setting = findUserSettingByKey(personId, key);
        if (setting.getValue() != null) {
            try {
                if (type == null) {
                    if (def == null) {
                        return def;
                    }
                    type = (Class<T>) def.getClass();
                }
                return getGsonInstance().fromJson(setting.getValue(), type);
            } catch (Exception e) {
                Log.e("SettingManager", e.getMessage(), e);
                /* ignore */
            }
        }
        return def;
    }

    public <T> void setUserSetting(final long personId, final String key, final T value) {
        final UserSetting setting = findUserSettingByKey(personId, key);
        setting.setValue(getGsonInstance().toJson(value));
        Application.getDb().getUserSettingDao().insertOrReplace(setting);
    }

    public void deleteUserSetting(final long personId, final String key) {
        final UserSetting setting = findUserSettingByKey(personId, key);
        if (setting != null) Application.getDb().getUserSettingDao().delete(setting);
    }

    // |**********************************|//
    // |****** CONVENIENCE METHODS *******|//
    // |**********************************|//

    public static long getSessionLastUserId() {
        return SettingsManager.getInstance().getSetting("sessionLastUserId", -1l, long.class);
    }

    public static void setSessionLastUserId(final long lastUserId) {
        SettingsManager.getInstance().setSetting("sessionLastUserId", lastUserId);
    }

    public static int getApplicationLastVersionId() {
        return SettingsManager.getInstance().getSetting("applicationLastVersionId", -1, int.class);
    }

    public static void setApplicationLastVersionId(final int lastVersionId) {
        SettingsManager.getInstance().setSetting("applicationLastVersionId", lastVersionId);
    }

    public static long getSessionOrganizationId(final long personId) {
        return SettingsManager.getInstance().getUserSetting(personId, "sessionOrganizationId", -1l, long.class);
    }

    public static void setSessionOrganizationId(final long personId, final long organizationId) {
        SettingsManager.getInstance().setUserSetting(personId, "sessionOrganizationId", organizationId);
    }
}