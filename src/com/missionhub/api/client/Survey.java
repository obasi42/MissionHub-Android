package com.missionhub.api.client;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.missionhub.api.ApiHelper;
import com.missionhub.config.Config;

public class Survey {
	
	public static String getUrl(Context ctx) {
		String url = Config.baseUrl + "/surveys";
		RequestParams params = ApiHelper.getDefaultRequestParams(ctx);
		return url + '?' + params.toString();
	}
	
}