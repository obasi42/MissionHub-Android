package com.missionhub.api;

import java.lang.reflect.Type;

import com.cr_wd.android.network.HttpHandler;
import com.cr_wd.android.network.HttpResponse;
import com.google.gson.Gson;
import com.missionhub.api.model.GError;
import com.missionhub.api.model.GMetaPeople;
import com.missionhub.error.ApiException;

public abstract class ApiHandler extends HttpHandler {

	private Type type;
	
	public ApiHandler() {}
	
	public ApiHandler(Class<GMetaPeople> type) {
		this.type = type;
	}

	@Override public void onSuccess(final HttpResponse response) {
		final Gson gson = new Gson();
		try {
			final GError error = gson.fromJson(response.responseBody, GError.class);
			response.throwable = new ApiException(error);
			onError(response);
			return;
		} catch (final Exception e) { /* not a mh error */}
		
		if (type != null) {
			try {
				final Object object = gson.fromJson(response.responseBody, type);
				onSuccess(object);
			} catch (final Exception e) {
				response.throwable = new ApiException(e);
				onError(response);
			}
		}
	}
	
	public void onSuccess(Object gsonObject) {
		
	}

	@Override public void onError(final HttpResponse response) {
		onError(response.throwable);
	}
	
	public void onError(final Throwable throwable) {
		
	}

	@Override public void onCancel(final HttpResponse response) {

	}

	@Override public void onRetry(final HttpResponse response) {

	}
}