package com.missionhub.api;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.missionhub.api.json.GError;
import com.missionhub.error.MHException;

public class ApiResponseHandler extends AsyncHttpResponseHandler {
	
	private Gson gson = new Gson();
	
	private Type gsonClass = null;
	
	public ApiResponseHandler () {
		super();
	}
	
	public ApiResponseHandler (Type gsonClass) {
		super();
		this.gsonClass = gsonClass;
	}
	
	public void setGsonClass(Type gsonClass) {
		this.gsonClass = gsonClass;
	}
	
	public Type getGsonClass() {
		return gsonClass;
	}
	
	@Override
	public void onSuccess(String response) {
		try {
			GError error = gson.fromJson(response, GError.class);
			onFailure(new MHException(error));
			return;
		} catch (Exception e) { /* not a mh error */ }
		
		onSuccessPlain(response);
		
		if (gsonClass != null) {
			try {
				Object gsonObject = gson.fromJson(response, gsonClass);
				onSuccess();
				onSuccess(gsonObject);
			} catch (Exception e) {
				onFailure(e);
			}
			return;
		}
		
		onSuccess();
	}
	
	public void onSuccess(Object gsonObject) {}

	public void onSuccess() {}
	
	public void onSuccessPlain(String response) {
		
	}
}