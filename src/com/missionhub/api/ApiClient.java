package com.missionhub.api;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.missionhub.config.Config;

public class ApiClient extends AsyncHttpClient {

	public static final String TAG = "ApiClient";
	
	private Context ctx;	
	
	public ApiClient (Context ctx) {
		super();
		this.ctx = new ContextWrapper(ctx);
		
	}

	/**
	 * Perform a HTTP GET request, without any parameters.
	 * 
	 * @param url
	 *            the URL to send the request to.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 */
	@Override
	public void get(String url, AsyncHttpResponseHandler responseHandler) {
		Log.d(TAG, "GET: " + url);
		addHeader("Accept", "application/vnd.missionhub-v"+Config.apiVersion+"+json");
		get(ctx, url, null, responseHandler);
	}

	/**
	 * Perform a HTTP GET request with parameters.
	 * 
	 *            the URL to send the request to.
	 * @param params
	 *            additional GET parameters to send with the request.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 */
	@Override
	public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		Log.d(TAG, "GET: " + url + '?' + params.toString());
		addHeader("Accept", "application/vnd.missionhub-v"+Config.apiVersion+"+json");
		get(ctx, url, params, responseHandler);
	}

	/**
	 * Perform a HTTP POST request, without any parameters.
	 * 
	 * @param url
	 *            the URL to send the request to.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 */
	@Override
	public void post(String url, AsyncHttpResponseHandler responseHandler) {
		Log.d(TAG, "POST: " + url);
		addHeader("Accept", "application/vnd.missionhub-v"+Config.apiVersion+"+json");
		post(ctx, url, null, responseHandler);
	}

	/**
	 * Perform a HTTP POST request with parameters.
	 * 
	 * @param url
	 *            the URL to send the request to.
	 * @param params
	 *            additional POST parameters or files to send with the request.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 */
	@Override
	public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		Log.d(TAG, "POST: " + url + "    PARAMS: " + params.toString());
		addHeader("Accept", "application/vnd.missionhub-v"+Config.apiVersion+"+json");
		post(ctx, url, params, responseHandler);
	}
	
	public void cancel(boolean mayInterruptIfRunning) {
		cancelRequests(ctx, mayInterruptIfRunning);
	}
}