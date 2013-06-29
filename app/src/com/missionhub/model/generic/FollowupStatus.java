package com.missionhub.model.generic;

import com.missionhub.util.ResourceUtils;

public enum FollowupStatus {
    uncontacted, attempted_contact, contacted, completed, do_not_contact;

    private String mTranslatedName;

    public static FollowupStatus parse(final String string) {
        try {
            return valueOf(string.replace(' ', '_').toLowerCase());
        } catch (Exception e) {
            return uncontacted;
        }
    }

    private static String getString(int id) {
        return ResourceUtils.getString(id);
    }

    @Override
    public String toString() {
        if (mTranslatedName == null) {
            mTranslatedName = ResourceUtils.getTranslatedName("status", this.name(), this.name());
        }
        return mTranslatedName;
    }
}