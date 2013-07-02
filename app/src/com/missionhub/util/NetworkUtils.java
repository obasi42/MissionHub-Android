package com.missionhub.util;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.missionhub.application.Configuration;

import java.io.File;

public class NetworkUtils {

    /**
     * Checks if a network connection is available.
     *
     * @param context
     * @return true if connect is available
     */
    public static boolean isNetworkAvailable(final Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (final NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()) haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    /**
     * Disables HTTP connection reuse in Donut and below, as it was buggy From:
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     */
    @SuppressWarnings("deprecation")
    public static void disableConnectionReuseIfNecessary() {
        if (Integer.parseInt(Build.VERSION.SDK) <= Build.VERSION_CODES.DONUT) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    /**
     * Enables built-in http cache beginning in ICS From:
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     *
     * @param application
     */
    public static void enableHttpResponseCache(final Application application) {
        if (Configuration.isCacheHttpEnabled()) {
            try {
                final long httpCacheSize = Configuration.getCacheHttpSize();
                final File httpCacheDir = new File(application.getCacheDir(), "http");
                Class.forName("android.net.http.HttpResponseCache").getMethod("install", File.class, long.class).invoke(null, httpCacheDir, httpCacheSize);
            } catch (final Exception httpResponseCacheNotAvailable) {
                /* ignore */
            }
        }
    }
}