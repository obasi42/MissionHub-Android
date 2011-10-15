package com.missionhub.api;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class ApiResponseHandler extends AsyncHttpResponseHandler {
	
	private Class<?> gsonClass;
	
	public void setGsonClass(Class<?> gsonClass) {
		this.gsonClass = gsonClass;
	}
	
	public Class<?> getGsonClass() {
		return gsonClass;
	}
	
}