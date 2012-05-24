package com.missionhub.android.api.old;

import android.content.Context;

import com.cr_wd.android.network.HttpParams;
import com.missionhub.android.config.Config;

/**
 * Survey API Helper
 */
public class SurveyApi {

	/**
	 * Returns the url to open in a web view for surveys
	 * 
	 * @param context
	 * @return
	 */
	public static String getUrl(final Context context) {
		final String url = Config.baseUrl + "/surveys";
		final HttpParams params = ApiHelper.getDefaultParams(context);
		return url + '?' + params.getParamString();
	}

}