package com.missionhub.api.client;

import com.loopj.android.http.RequestParams;
import com.missionhub.api.ApiHelper;
import com.missionhub.config.Config;

public class Survey {
	
	public static String getUrl() {
		String url = Config.baseUrl + "/surveys";
		RequestParams params = ApiHelper.getDefaultRequestParams();
		return url + '?' + params.toString();
	}
	
}