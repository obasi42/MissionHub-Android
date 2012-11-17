package com.missionhub.network;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import ch.boye.httpclientandroidlib.Consts;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpDelete;
import ch.boye.httpclientandroidlib.client.methods.HttpEntityEnclosingRequestBase;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpHead;
import ch.boye.httpclientandroidlib.client.methods.HttpOptions;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpPut;
import ch.boye.httpclientandroidlib.client.methods.HttpRequestBase;
import ch.boye.httpclientandroidlib.client.methods.HttpTrace;
import ch.boye.httpclientandroidlib.client.utils.URIBuilder;
import ch.boye.httpclientandroidlib.impl.client.AutoRetryHttpClient;
import ch.boye.httpclientandroidlib.util.EntityUtils;

import com.missionhub.application.Application;

public class HttpClient {

	private ResponseType mResponseType = ResponseType.STRING;

	private Charset mCharset = Consts.UTF_8;

	public static enum ResponseType {
		STRING, RAW
	}

	public static enum HttpMethod {
		DELETE, GET, HEAD, POST, OPTIONS, PUT, TRACE
	}

	public HttpClientFuture doRequest(final HttpMethod method, final String url) throws IOException, URISyntaxException {
		return doRequest(method, url, null, null);
	}

	public HttpClientFuture doRequest(final HttpMethod method, final String url, final HttpHeaders headers) throws IOException, URISyntaxException {
		return doRequest(method, url, headers, null);
	}

	public HttpClientFuture doRequest(final HttpMethod method, final String url, final HttpParams params) throws IOException, URISyntaxException {
		return doRequest(method, url, null, params);
	}

	public HttpClientFuture doRequest(final HttpMethod method, final String url, final HttpHeaders headers, final HttpParams params) throws IOException, URISyntaxException {
		// check for a data connection
		if (!NetworkUtils.isNetworkAvailable(Application.getContext())) {
			throw new NetworkUnavailableException();
		}

		// create the request object based on the method
		HttpRequestBase request = null;
		switch (method) {
		case DELETE:
			request = new HttpDelete();
			break;
		case GET:
			request = new HttpGet();
			break;
		case HEAD:
			request = new HttpHead();
			break;
		case OPTIONS:
			request = new HttpOptions();
			break;
		case POST:
			request = new HttpPost();
			break;
		case PUT:
			request = new HttpPut();
			break;
		case TRACE:
			request = new HttpTrace();
			break;
		default:
			request = new HttpGet();
		}

		// create the uri builder from the url
		final URIBuilder builder = new URIBuilder(url);

		// set the headers
		if (headers != null) {
			final Map<String, List<String>> heads = headers.getHeaders();
			final Iterator<Entry<String, List<String>>> headerItr = heads.entrySet().iterator();
			while (headerItr.hasNext()) {
				final Entry<String, List<String>> entry = headerItr.next();
				final String key = entry.getKey();
				final List<String> values = entry.getValue();

				for (final String value : values) {
					request.addHeader(key, value);
				}
			}
		}

		// add the parameters to the builder
		if (params != null) {

			// add the uri parameters
			final Iterator<Entry<String, String>> uriItr = params.getUriParams().entrySet().iterator();
			while (uriItr.hasNext()) {
				final Entry<String, String> entry = uriItr.next();
				builder.addParameter(entry.getKey(), entry.getValue());
			}

			// add the normal params
			if (method != HttpMethod.POST && method != HttpMethod.PUT) {
				// if the method is not post or put, add the rest of the parameters to the url
				final Iterator<Entry<String, String>> pItr = params.getParams().entrySet().iterator();
				while (pItr.hasNext()) {
					final Entry<String, String> entry = pItr.next();
					builder.addParameter(entry.getKey(), entry.getValue());
				}
			} else {
				// set the entity from the params
				((HttpEntityEnclosingRequestBase) request).setEntity(params.getEntity());
			}
		};

		// set the uri
		request.setURI(builder.build());

		// handle the response in a separate thread
		final HttpClientFuture task = new HttpClientFuture(new HttpClientCallable(request, mResponseType, mCharset));
		Application.getExecutor().execute(task);

		return task;
	}

	public static class HttpClientFuture extends FutureTask<HttpResponse> {

		private final HttpClientCallable callable;

		protected HttpClientFuture(final HttpClientCallable callable) {
			super(callable);
			this.callable = callable;
		}

		@Override
		public boolean cancel(final boolean mayInterruptIfRunning) {
			try {
				callable.abort();
				callable.shutdown();
			} catch (final Exception ignore) { /* ignore */ }
			return super.cancel(mayInterruptIfRunning);
		}

		public void abort() {
			try {
				callable.abort();
			} catch (final Exception ignore) { /* ignore */ }
		}
	}

	public static class HttpClientCallable implements Callable<HttpResponse> {
		private final HttpRequestBase mRequest;
		private final ResponseType mType;
		private final Charset mCharset;

		private ch.boye.httpclientandroidlib.client.HttpClient mClient;
		private ch.boye.httpclientandroidlib.HttpResponse mResponse;

		protected HttpClientCallable(final HttpRequestBase request, final ResponseType type, final Charset charset) {
			mRequest = request;
			mType = type;
			mCharset = charset;
		}

		@Override
		public HttpResponse call() throws Exception {
			// the http response wrapper
			final HttpResponse response = new HttpResponse();

			// execute the response
			mClient = new AutoRetryHttpClient();
			try {
				mResponse = mClient.execute(mRequest);

				response.statusCode = mResponse.getStatusLine().getStatusCode();
				response.statusReason = mResponse.getStatusLine().getReasonPhrase();
				response.headers = mResponse.getAllHeaders();

				if (response.statusCode >= 400) {
					throw new HttpException(response.statusCode, response.statusReason);
				}

				final HttpEntity responseEntity = mResponse.getEntity();
				if (responseEntity != null) {
					if (mType == ResponseType.STRING) {

						response.responseBody = EntityUtils.toString(responseEntity, mCharset);
					} else {
						response.responseBodyRaw = EntityUtils.toByteArray(responseEntity);
					}
					EntityUtils.consumeQuietly(responseEntity);
				}
			} finally {
				shutdown();
			}
			return response;
		}

		public void abort() throws Exception {
			mRequest.abort();
		}

		public void shutdown() throws Exception {
			mClient.getConnectionManager().shutdown();
		}
	}

	public void setResponseType(final ResponseType type) {
		mResponseType = type;
	}

	public void setCharset(final Charset charset) {
		mCharset = charset;
	}
}