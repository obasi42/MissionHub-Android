package com.missionhub.application;

import com.missionhub.model.Setting;
import com.missionhub.model.SettingDao;
import com.missionhub.model.UserSetting;
import com.missionhub.model.UserSettingDao;

/**
 * Manages the user's settings
 */
public class SettingsManager {

    /**
     * the singleton instance
     */
    private static SettingsManager sSettingsManager;

    /**
     * the settings db session
     */
    private SettingDao sd;

    /**
     * the user settings db session
     */
    private UserSettingDao usd;

    private SettingsManager() {
    }

    /**
     * creates and returns the SettingsManager
     */
    public synchronized static SettingsManager getInstance() {
        if (sSettingsManager == null) {
            sSettingsManager = new SettingsManager();
            sSettingsManager.sd = Application.getDb().getSettingDao();
            sSettingsManager.usd = Application.getDb().getUserSettingDao();

        }
        return sSettingsManager;
    }

    /**
     * Parse object as string
     *
     * @param o
     * @return
     */
    private static String s(final Object o) {
        return String.valueOf(o);
    }

    // |**********************************|//
    // |******** SETTINGS METHODS ********|//
    // |**********************************|//

    /**
     * Finds a setting by the key
     *
     * @param key
     * @return the setting
     */
    public Setting findSettingByKey(final String key) {
        Setting setting = sd.queryBuilder().where(SettingDao.Properties.Key.eq(key)).unique();
        if (setting == null) {
            setting = new Setting();
            setting.setKey(key);
        }
        return setting;
    }

    /**
     * Gets the value of a setting
     *
     * @param key
     * @return the value or null
     */
    public String getSetting(final String key) {
        return getSetting(key, null);
    }

    /**
     * Gets the value of a setting or the default value
     *
     * @param key
     * @param def
     * @return the value or def
     */
    public String getSetting(final String key, final String def) {
        final Setting setting = findSettingByKey(key);
        if (setting.getValue() != null) {
            return setting.getValue();
        } else {
            return def;
        }
    }

    /**
     * Sets the value of a setting
     *
     * @param key
     * @param value
     */
    public void setSetting(final String key, final String value) {
        final Setting setting = findSettingByKey(key);
        setting.setValue(String.valueOf(value));
        sd.insertOrReplace(setting);
    }

    /**
     * Deletes a setting
     *
     * @param key
     */
    public void deleteSetting(final String key) {
        final Setting setting = findSettingByKey(key);
        if (setting != null) sd.delete(setting);
    }

    // |**********************************|//
    // |***** USER SETTINGS METHODS ******|//
    // |**********************************|//

    /**
     * Finds a user setting by the key
     *
     * @param key
     * @return
     */
    public UserSetting findUserSettingByKey(final long personId, final String key) {
        UserSetting setting = usd.queryBuilder().where(UserSettingDao.Properties.Key.eq(key), UserSettingDao.Properties.Person_id.eq(personId)).unique();
        if (setting == null) {
            setting = new UserSetting();
            setting.setPerson_id(personId);
            setting.setKey(key);
        }
        return setting;
    }

    /**
     * Gets a setting for a user
     *
     * @param personId
     * @param key
     * @return the value or null
     */
    public String getUserSetting(final long personId, final String key) {
        return getUserSetting(personId, key, null);
    }

    /**
     * Gets a setting for a user
     *
     * @param personId
     * @param key
     * @param def
     * @return the value or def
     */
    public String getUserSetting(final long personId, final String key, final String def) {
        final UserSetting setting = findUserSettingByKey(personId, key);
        if (setting.getValue() != null) {
            return setting.getValue();
        } else {
            return def;
        }
    }

    /**
     * Sets a user setting
     *
     * @param personId
     * @param key
     * @param value
     */
    public void setUserSetting(final long personId, final String key, final Object value) {
        final UserSetting setting = findUserSettingByKey(personId, key);
        setting.setValue(String.valueOf(value));
        usd.insertOrReplace(setting);
    }

    /**
     * Deletes a user setting
     *
     * @param personId
     * @param key
     */
    public void deleteUserSetting(final long personId, final String key) {
        final UserSetting setting = findUserSettingByKey(personId, key);
        if (setting != null) usd.delete(setting);
    }

    // |**********************************|//
    // |****** CONVENIENCE METHODS *******|//
    // |**********************************|//

    public static long getSessionLastUserId() {
        return Long.parseLong(SettingsManager.getInstance().getSetting("sessionLastUserId", "-1"));
    }

    public static void setSessionLastUserId(final long lastUserId) {
        SettingsManager.getInstance().setSetting("sessionLastUserId", s(lastUserId));
    }

    public static int getApplicationLastVersionId() {
        return Integer.parseInt(SettingsManager.getInstance().getSetting("applicationLastVersionId", "-1"));
    }

    public static void setApplicationLastVersionId(final int lastVersionId) {
        SettingsManager.getInstance().setSetting("applicationLastVersionId", s(lastVersionId));
    }

    public static long getSessionOrganizationId(final long personId) {
        return Long.parseLong(SettingsManager.getInstance().getUserSetting(personId, "sessionOrganizationId", "-1"));
    }

    public static void setSessionOrganizationId(final long personId, final long organizationId) {
        SettingsManager.getInstance().setUserSetting(personId, "sessionOrganizationId", s(organizationId));
    }
}