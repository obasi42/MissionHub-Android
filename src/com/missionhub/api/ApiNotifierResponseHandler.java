package com.missionhub.api;

import android.content.Context;
import android.os.Bundle;

import com.missionhub.Application;
import com.missionhub.api.ApiNotifier.Type;

public class ApiNotifierResponseHandler extends ApiResponseHandler {

	Application app;
	Context ctx;
	String tag;
	String type;

	public ApiNotifierResponseHandler(Context ctx, java.lang.reflect.Type t, String tag, String type) {
		super(t);
		this.ctx = ctx;
		app = (Application) ctx.getApplicationContext();
		this.tag = tag;
		this.type = type;
	}

	@Override
	public void onStart() {
		super.onStart();
		Bundle b = new Bundle();
		if (tag != null)
			b.putString("tag", tag);
		app.getApiNotifier().postMessage(Type.valueOf("JSON_" + type + "_ON_START"), b);
	}

	@Override
	public void onSuccess(Object gMetaPerson) {
		Bundle b = new Bundle();
		if (tag != null)
			b.putString("tag", tag);
		app.getApiNotifier().postMessage(Type.valueOf("JSON_" + type + "_ON_SUCCESS"), b);
	}

	@Override
	public void onFailure(Throwable e) {
		Bundle b = new Bundle();
		if (tag != null)
			b.putString("tag", tag);
		b.putSerializable("throwable", e);
		app.getApiNotifier().postMessage(Type.valueOf("JSON_" + type + "_ON_FAILURE"), b);
	}

	@Override
	public void onFinish() {
		Bundle b = new Bundle();
		if (tag != null)
			b.putString("tag", tag);
		app.getApiNotifier().postMessage(Type.valueOf("JSON_" + type + "_ON_FINISH"), b);
	}
}