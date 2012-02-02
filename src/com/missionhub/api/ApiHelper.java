package com.missionhub.api;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.Build;

import com.cr_wd.android.network.HttpHeaders;
import com.cr_wd.android.network.HttpParams;
import com.missionhub.MissionHubApplication;
import com.missionhub.config.Config;
import com.missionhub.config.Preferences;

public class ApiHelper {

	public static HttpParams getDefaultParams(final Context context) {
		final HttpParams params = new HttpParams();

		// Logging
		params.put("platform", "android");
		params.put("platform_product", Build.PRODUCT);
		params.put("platform_release", android.os.Build.VERSION.RELEASE);
		params.put("app", ((MissionHubApplication) context.getApplicationContext()).getVersion());

		// Access Token
		params.put("access_token", ((MissionHubApplication) context.getApplicationContext()).getSession().getAccessToken());
		
		// Organization Id
		final int organizationId = ((MissionHubApplication) context.getApplicationContext()).getSession().getOrganizationId();
		if (organizationId >= 0)
			params.put("org_id", organizationId);
		
		return params;
	}

	public static HttpHeaders getDefaultHeaders(final Context context) {
		final HttpHeaders headers = new HttpHeaders();
		headers.addHeader("Accept", "application/vnd.missionhub-v" + Config.apiVersion + "+json");

		// Access Token
		headers.addHeader("Authorization", "OAuth: " + ((MissionHubApplication) context.getApplicationContext()).getSession().getAccessToken());

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

	public static void configAutoLogin(Context context) {
		if (Config.autoLoginToken != null && !Config.autoLoginToken.trim().equals(""))
			Preferences.setAccessToken(context, Config.autoLoginToken);
	}
}