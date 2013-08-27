package com.missionhub.util;

import android.content.Context;

import com.missionhub.application.Application;

import org.apache.commons.lang3.StringUtils;

public class ResourceUtils {

    public static String getString(int id) {
        return getContext().getString(id);
    }

    public static String getTranslatedName(String type, String i18n, String def) {
        if (StringUtils.isNotEmpty(i18n)) {
            final int id = getContext().getResources().getIdentifier(type + "_" + i18n, "string", getContext().getPackageName());
            if (id != 0) {
                return getString(id);
            }
        }
        return def;
    }

    public static int getDrawableId(String name, int def) {
        if (StringUtils.isNotEmpty(name)) {
            final int id = getContext().getResources().getIdentifier(name, "drawable", getContext().getPackageName());
            if (id != 0) {
                return id;
            }
        }
        return def;
    }

    private static Context getContext() {
        return Application.getContext();
    }


}