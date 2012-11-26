package com.missionhub.util.facebook;

import java.net.URLEncoder;

import android.util.Log;

import com.google.gson.Gson;
import com.missionhub.network.HttpClient;
import com.missionhub.network.HttpClient.HttpClientFuture;
import com.missionhub.network.HttpClient.HttpMethod;
import com.missionhub.network.HttpResponse;

public class FQL {

	public static GFQLPicCrop getPicCrop(final int fbId) {
		try {
			final HttpClient client = new HttpClient();
			final HttpClientFuture future = client.doRequest(HttpMethod.GET, "https://graph.facebook.com/fql?q=" + URLEncoder.encode("select pic_crop from profile where id = " + fbId, "UTF-8"));
			final HttpResponse response = future.get();
			final Gson gson = new Gson();
			final GFQLData data = gson.fromJson(response.responseBody, GFQLData.class);
			return data.data[0].pic_crop;
		} catch (final Exception e) {
			Log.e("FQL", e.getMessage(), e);
		}
		return null;
	}

}