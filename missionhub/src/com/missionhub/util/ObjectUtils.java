package com.missionhub.util;

import java.util.Collection;

public class ObjectUtils {

    public static boolean isEmpty(Object object) {
        if (object == null) return true;
        if (object instanceof CharSequence && ((CharSequence) object).length() <= 0) return true;
        return object instanceof Collection && ((Collection) object).isEmpty();
    }

    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    public static boolean isNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) return true;
        }
        return false;
    }

    public static boolean isNotNull(Object... objects) {
        return !isNull(objects);
    }

}
