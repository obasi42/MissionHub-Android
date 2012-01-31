package com.missionhub.api;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.Build;

import com.cr_wd.android.network.HttpHeaders;
import com.cr_wd.android.network.HttpParams;
import com.missionhub.MissionHubApplication;
import com.missionhub.config.Config;

public class ApiHelper {

	public static HttpParams getDefaultParams(final Context context) {
		final HttpParams params = new HttpParams();

		// Logging
		params.put("platform", "android");
		params.put("platform_product", Build.PRODUCT);
		params.put("platform_release", android.os.Build.VERSION.RELEASE);
		params.put("app", ((MissionHubApplication) context.getApplicationContext()).getVersion());

		// Access Token
		// TODO:

		// Organization Id
		// TODO:

		return params;
	}

	public static HttpHeaders getDefaultHeaders(final Context context) {
		final HttpHeaders headers = new HttpHeaders();
		headers.addHeader("Accept", "application/vnd.missionhub-v" + Config.apiVersion + "+json");

		// Access Token
		// TODO:
		// headers.addHeader("Authorization", "OAuth: " + accessToken);

		return headers;
	}

	public static String getAbsoluteUrl(final String... actions) {
		final StringBuffer sb = new StringBuffer(Config.apiUrl);
		for (final String action : actions) {
			sb.append("/" + action);
		}
		return sb.toString();
	}

	public static String toList(final List<Integer> ids) {
		final StringBuffer idList = new StringBuffer();
		final Iterator<Integer> itr = ids.iterator();
		while (itr.hasNext()) {
			final String element = String.valueOf(itr.next());
			idList.append(element);
			if (itr.hasNext()) {
				idList.append(",");
			}
		}
		return idList.toString();
	}

	public static String stripUnsafeChars(final String string) {
		return string.replaceAll("[\\]\\[|=?]", "");
	}
}