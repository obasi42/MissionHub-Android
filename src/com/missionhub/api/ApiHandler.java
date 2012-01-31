package com.missionhub.api;

import com.cr_wd.android.network.HttpHandler;
import com.cr_wd.android.network.HttpResponse;
import com.google.gson.Gson;
import com.missionhub.api.model.GError;
import com.missionhub.error.ApiException;

abstract class ApiHandler extends HttpHandler {

	@Override public void onSuccess(final HttpResponse response) {
		final Gson gson = new Gson();
		try {
			final GError error = gson.fromJson(response.responseBody, GError.class);
			response.throwable = new ApiException(error);
			onError(response);
			return;
		} catch (final Exception e) { /* not a mh error */}
	}

	@Override public void onError(final HttpResponse response) {
		// TODO Auto-generated method stub

	}

	@Override public void onCancel(final HttpResponse response) {
		// TODO Auto-generated method stub

	}

	@Override public void onRetry(final HttpResponse response) {
		// TODO Auto-generated method stub

	}

}