package com.missionhub.api;

import com.loopj.android.http.RequestParams;
import com.missionhub.config.Config;

public class Login {
	
	public static String getUrl() {
		String url = Config.oauthUrl + "/authorize";
		RequestParams params = new RequestParams();
		params.put("display", "touch");
		params.put("simple", "true");
		params.put("response_type", "code");
		params.put("redirect_uri", Config.oauthUrl + "/done.json");
		params.put("client_id", Config.oauthClientId);
		params.put("scope", Config.oauthScope);
		return url + '?' + params.toString();
	}
	
}