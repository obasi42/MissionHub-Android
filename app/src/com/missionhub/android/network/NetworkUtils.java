package com.missionhub.android.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * A collection of network related utilities
 */
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
            if (ni.getTypeName().equalsIgnoreCase("WIFI")) if (ni.isConnected()) haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) if (ni.isConnected()) haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}