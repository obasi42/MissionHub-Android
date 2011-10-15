package com.missionhub.api;

import java.util.Iterator;
import java.util.List;

import android.os.Build;

import com.loopj.android.http.RequestParams;
import com.missionhub.Application;
import com.missionhub.auth.Auth;
import com.missionhub.auth.User;
import com.missionhub.config.Config;
import com.missionhub.helpers.U;

public class ApiHelper {
	
	public static RequestParams appendLogging(RequestParams params) {
		params.put("platform", "android");
		params.put("platform_product", Build.PRODUCT);
		params.put("platform_release", android.os.Build.VERSION.RELEASE);
		params.put("app", Application.getVersion());
		return params;
	}
	
	public static RequestParams appendAccessToken(RequestParams params) {
		if (!U.nullOrEmpty(Auth.getAccessToken()))
			params.put("access_token", Auth.getAccessToken());
		return params;
	}
	
	public static RequestParams appendOrganizationId(RequestParams params) {
		if (User.getOrganizationID() >= 0)
			params.put("org_id", String.valueOf(User.getOrganizationID()));
		return params;
	}
	
	public static RequestParams getDefaultRequestParams() {
		RequestParams params = new RequestParams();
		appendAccessToken(params);
		appendOrganizationId(params);
		appendLogging(params);
		return params;
	}
	
	public static String getAbsoluteUrl(String... actions) {
		StringBuffer sb = new StringBuffer(Config.apiUrl);
		for (String action : actions) {
			sb.append("/" + action);
		}
		return sb.toString();
	}
	
	public static String getAbsoluteJsonUrl(String... actions) {
		return getAbsoluteUrl(actions) + ".json";
	}
	
	public static String toList(List<Integer> ids) {
		StringBuffer idList = new StringBuffer();
		Iterator<Integer> itr = ids.iterator();
		while (itr.hasNext()) {
			String element = String.valueOf(itr.next());
			idList.append(element);
			if (itr.hasNext()) {
				idList.append(",");
			}
		}
		return idList.toString();
	}
	
	public static String stripUnsafeChars(String string) {
		return string.replaceAll("[\\]\\[|=?]", "");
	}
}