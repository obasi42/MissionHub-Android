package com.missionhub.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;

import com.missionhub.R;
import com.missionhub.application.Application;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.holoeverywhere.app.Activity;

public class DisplayUtils {

    /**
     * Converts density independent pixels to pixels.
     *
     * @param dip
     * @return
     */
    public static float dpToPixel(final float dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getContext().getResources().getDisplayMetrics());
    }

    /**
     * Converts pixels to density independent pixels.
     *
     * @param px
     * @return
     */
    public static float pixelToDp(final float px) {
        return px / (getContext().getResources().getDisplayMetrics().densityDpi / 160f);
    }

    /**
     * Attempts to get the accurate display metrics for the device
     *
     * @param activity
     * @return
     */
    public static DisplayMetrics getRealDisplayMetrics(Activity activity) {
        final DisplayMetrics metrics = new DisplayMetrics();
        Display display = activity.getWindowManager().getDefaultDisplay();
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                display.getRealMetrics(metrics);
            } else {
                display.getMetrics(metrics);
                metrics.widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                metrics.heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            }
        } catch (Exception e) {
            display.getMetrics(metrics);
        }
        return metrics;
    }

    public static DisplayImageOptions getContactImageDisplayOptions() {
        return new DisplayImageOptions.Builder()
                .cacheInMemory()
                .cacheOnDisc()
                .showImageForEmptyUri(R.drawable.default_contact)
                .showImageOnFail(R.drawable.default_contact)
                .showStubImage(R.drawable.default_contact)
                .build();
    }

    private static Context getContext() {
        return Application.getContext();
    }

}
