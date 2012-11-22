package com.missionhub.util.facebook;

import java.net.URLEncoder;

import android.util.Log;

import com.google.gson.Gson;
import com.missionhub.network.HttpClient;
import com.missionhub.network.HttpResponse;
import com.missionhub.network.HttpClient.HttpClientFuture;
import com.missionhub.network.HttpClient.HttpMethod;

public class FQL {
	
	public static GFQLPicCrop getPicCrop(int fbId) {
		try {
			HttpClient client = new HttpClient();
			HttpClientFuture future = client.doRequest(HttpMethod.GET, "https://graph.facebook.com/fql?q=" + URLEncoder.encode("select pic_crop from profile where id = " + fbId, "UTF-8"));
			HttpResponse response = future.get();
			Gson gson = new Gson();
			GFQLData data = gson.fromJson(response.responseBody, GFQLData.class);
			return data.data.pic_crop;
		} catch (Exception e) {
			Log.e("FQL", e.getMessage(), e);
		}
		return null;
	}
	
}