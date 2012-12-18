package com.missionhub.api;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import com.missionhub.api.Api.ApiResponseParser;
import com.missionhub.application.Application;
import com.missionhub.network.HttpClient.HttpMethod;
import com.missionhub.network.HttpClient.ResponseType;
import com.missionhub.network.HttpHeaders;
import com.missionhub.network.HttpParams;
import com.missionhub.network.HttpResponse;
import com.missionhub.util.U;

/**
 * A generic api call
 * 
 * @param <T>
 *            the return type of the call
 */
public class ApiCall<T> {

	private final ApiOptions options;

	private FutureTask<T> mTask;

	private Future<T> mFuture;

	private boolean paramsBuilt = false;

	protected ApiCall(final ApiOptions options) {
		if (options == null) throw new RuntimeException("ApiOptions required.");

		this.options = options;

		if (options.id == null) {
			options.id = Api.sCallId.getAndIncrement();
		}

		createTask();

		Api.getInstance().registerCall(this);
	}

	/**
	 * Sets up the future task for the call
	 */
	private void createTask() {
		mTask = new FutureTask<T>(new Callable<T>() {
			@Override
			public T call() throws Exception {
				return ApiCall.this.call();
			}
		}) {

			@Override
			protected void done() {
				Api.getInstance().unregisterCall(ApiCall.this);
				super.done();
			}
		};
	}

	protected T call() throws Exception {
		final HttpResponse response = Api.getInstance().doRequest(getHttpMethod(), getUrl(), getHttpHeaders(), getHttpParams(), isAuthenticated(), getResponseType());
		try {
			return onParseReponse(response);
		} catch (final Exception e) {
			throw new ApiException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected T onParseReponse(final HttpResponse response) throws Exception {
		return (T) options.responseParser.parseResponse(response);
	}

	public Object getId() {
		return options.id;
	}

	public HttpMethod getHttpMethod() {
		return options.method;
	}

	public String getUrl() {
		return options.url;
	}

	public HttpHeaders getHttpHeaders() {
		return options.headers;
	}

	public HttpParams getHttpParams() {
		if (paramsBuilt) return options.params;

		if (options.params == null) {
			options.params = new HttpParams();
		}
		if (options.includes != null) {
			options.params.add("include", U.toCSV(options.includes));
		}
		if (options.since != null) {
			options.params.add("since", options.since);
		}
		if (options.limit != null) {
			options.params.add("limit", options.limit);
		}
		if (options.offset != null) {
			options.params.add("offset", options.offset);
		}

		paramsBuilt = true;

		return options.params;
	}

	public boolean isAuthenticated() {
		if (options.authenticated == null) {
			return true;
		}
		return options.authenticated;
	}

	public ResponseType getResponseType() {
		if (options.responseType == null) {
			return ResponseType.STRING;
		}
		return options.responseType;
	}

	@SuppressWarnings("unchecked")
	protected ApiResponseParser<T> getReponseParser() {
		return (ApiResponseParser<T>) options.responseParser;
	}

	/**
	 * Executes the task using the application wide executor
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized Future<T> execute() {
		if (mFuture != null) return mFuture;

		if (mTask == null) createTask();

		if (options.executor != null) {
			mFuture = (Future<T>) options.executor.submit(mTask);
		} else {
			mFuture = (Future<T>) Application.getExecutor().submit(mTask);
		}
		return mFuture;
	}

	/**
	 * @see FutureTask#cancel()
	 */
	public boolean cancel(final boolean mayInterruptIfRunning) {
		return mTask.cancel(mayInterruptIfRunning);
	}

	/**
	 * @see FutureTask#get()
	 */
	public T get() throws Exception {
		if (mFuture == null) {
			execute();
		}
		return mTask.get();
	}

	/**
	 * @see FutureTask#get(long, unit)
	 */
	public T get(final long timeout, final TimeUnit unit) throws Exception {
		if (mFuture == null) {
			execute();
		}
		return mTask.get(timeout, unit);
	}

	/**
	 * @see FutureTask#isCancelled()
	 */
	public boolean isCancelled() {
		return mTask.isCancelled();
	}

	/**
	 * @see FutureTask#isDone()
	 */
	public boolean isDone() {
		return mTask.isDone();
	}

	/**
	 * @see FutureTask#run()
	 */
	public void run() {
		mTask.run();
	}

	/**
	 * Resets the call so it can be run again. If the task is still running, this will have no effect.
	 */
	public synchronized void reset() {
		if (mTask != null && !mTask.isDone()) return;
		mFuture = null;
		createTask();
	}

}