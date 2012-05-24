package com.missionhub.android.api.old;

import java.io.File;
import java.io.FileNotFoundException;

import android.content.Context;

import com.cr_wd.android.network.HttpHeaders;
import com.cr_wd.android.network.HttpParams;

/**
 * Photos API Helper
 * 
 * @see missionhub/app/controllers/api/photos_controller.rb
 */
public class PhotosApi {

	/**
	 * Uploads a photo to the server for the specified contact
	 * 
	 * @param context
	 * @param contactId
	 * @param file
	 * @param apiHandler
	 * @return
	 * @throws FileNotFoundException
	 */
	public static ApiRequest create(final Context context, final int contactId, final File file, final ApiHandler apiHandler) throws FileNotFoundException {
		final ApiClient client = new ApiClient();
		final String url = ApiHelper.getAbsoluteUrl("photos");
		final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
		final HttpParams params = ApiHelper.getDefaultParams(context);
		params.put("contact_id", contactId);
		params.put("image", file);
		return new ApiRequest(client, client.post(url, headers, params, apiHandler));
	}

}