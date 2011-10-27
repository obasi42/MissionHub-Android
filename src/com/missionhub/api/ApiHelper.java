package com.missionhub.api;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.Build;

import com.loopj.android.http.RequestParams;
import com.missionhub.Application;
import com.missionhub.config.Config;
import com.missionhub.helpers.U;

public class ApiHelper {
	
	public static RequestParams appendLogging(Context ctx, RequestParams params) {
		params.put("platform", "android");
		params.put("platform_product", Build.PRODUCT);
		params.put("platform_release", android.os.Build.VERSION.RELEASE);
		params.put("app", ((Application) ctx.getApplicationContext()).getVersion());
		return params;
	}
	
	public static RequestParams appendAccessToken(Context ctx, RequestParams params) {
		if (!U.nullOrEmpty(((Application) ctx.getApplicationContext()).getUser().getAccessToken()))
			params.put("access_token", ((Application) ctx.getApplicationContext()).getUser().getAccessToken());
		return params;
	}
	
	public static RequestParams appendOrganizationId(Context ctx, RequestParams params) {
		if (((Application) ctx.getApplicationContext()).getUser().getOrganizationID() >= 0)
			params.put("org_id", String.valueOf(((Application) ctx.getApplicationContext()).getUser().getOrganizationID()));
		return params;
	}
	
	public static RequestParams getDefaultRequestParams(Context ctx) {
		RequestParams params = new RequestParams();
		appendAccessToken(ctx, params);
		appendOrganizationId(ctx, params);
		appendLogging(ctx, params);
		return params;
	}
	
	public static String getAbsoluteUrl(String... actions) {
		StringBuffer sb = new StringBuffer(Config.apiUrl);
		for (String action : actions) {
			sb.append("/" + action);
		}
		return sb.toString();
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