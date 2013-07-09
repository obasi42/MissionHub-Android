package com.missionhub.util;

import java.lang.reflect.Array;
import java.util.Collection;

public class ArrayUtils {

    /**
     * Removes null or blank items
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> T[] clean(final T[] array) {
        int r, w;
        final int n = r = w = array.length;
        while (r > 0) {
            final T object = array[--r];
            if (object == null) continue;
            if (object instanceof Collection && ((Collection) object).size() <= 0) continue;
            if (object instanceof CharSequence && ((CharSequence) object).length() <= 0) continue;

            array[--w] = object;
        }
        return copyOfRange(array, w, n);
    }

    public static <T> T[] copyOfRange(T[] original, int start, int end) {
        int originalLength = original.length; // For exception priority compatibility.
        if (start > end) {
            throw new IllegalArgumentException();
        }
        if (start < 0 || start > originalLength) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int resultLength = end - start;
        int copyLength = Math.min(resultLength, originalLength - start);
        T[] result = (T[]) Array.newInstance(original.getClass().getComponentType(), resultLength);
        System.arraycopy(original, start, result, 0, copyLength);
        return result;
    }

}