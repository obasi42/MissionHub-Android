package com.missionhub.util;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.missionhub.application.Configuration;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetworkUtils {

    public static final String TAG = NetworkUtils.class.getSimpleName();

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

    public static String buildUrl(String scheme, String host, String path, Map<String, String> params) {
        return buildUrl(scheme, host, path, params, true);
    }

    public static String buildUrl(String scheme, String host, String path, Map<String, String> params, boolean ignoreEmptyParams) {
        List<NameValuePair> qparams = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (ignoreEmptyParams && StringUtils.isEmpty(entry.getValue())) continue;
            qparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        try {
            return URIUtils.createURI(scheme, host, -1, path, URLEncodedUtils.format(qparams, "UTF-8"), null).toString();
        } catch (URISyntaxException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }
}