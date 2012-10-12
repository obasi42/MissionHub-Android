package com.missionhub.network;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.http.util.ByteArrayBuffer;

/**
 * HTTP Client Helper for MissionHub. A threadless derivative of croemmich's Android Asynchronous HttpURLConnection.
 * Requests are blocking and should be performed outside of the UI thread.
 * 
 * @author croemmich
 */
public class HttpClient {

	// TODO: find a more realistic value for the timesouts
	private static final int DEFAULT_READ_TIMEOUT = 100000;
	private static final int DEFAULT_CONNECT_TIMEOUT = 150000;
	private static final int DEFAULT_MAX_RETRIES = 3;
	private static final String DEFAULT_ENCODING = "UTF-8";

	private final RequestOptions requestOptions = new RequestOptions();

	private class RequestOptions {
		public int readTimeout = DEFAULT_READ_TIMEOUT;
		public int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
		public int maxRetries = DEFAULT_MAX_RETRIES;
		public String encoding = DEFAULT_ENCODING;
	}

	/**
	 * Supported HTTP request methods
	 */
	public static enum Method {
		GET, POST
	}

	/**
	 * Create a new HttpClient
	 */
	public HttpClient() {}

	/**
	 * Perform a HTTP GET request, without any parameters.
	 * 
	 * @param url
	 *            the URL
	 */
	public HttpResponse get(final String url) {
		return doRequest(Method.GET, url, null, null);
	}

	/**
	 * Perform a HTTP GET request with additional headers
	 * 
	 * @param url
	 *            the URL
	 * @param headers
	 *            additional headers for the request
	 */
	public HttpResponse get(final String url, final HttpHeaders headers) {
		return doRequest(Method.GET, url, headers, null);
	}

	/**
	 * Perform a HTTP GET request with parameters
	 * 
	 * @param url
	 *            the URL
	 * @param params
	 *            additional parameters for the request
	 */
	public HttpResponse get(final String url, final HttpParams params) {
		return doRequest(Method.GET, url, null, params);
	}

	/**
	 * Perform a HTTP GET request with additional headers and parameters
	 * 
	 * @param url
	 *            the URL
	 * @param headers
	 *            additional headers for the request
	 * @param params
	 *            additional parameters for the request
	 */
	public HttpResponse get(final String url, final HttpHeaders headers, final HttpParams params) {
		return doRequest(Method.GET, url, headers, params);
	}

	/**
	 * Perform a HTTP GET request, without any parameters.
	 * 
	 * @param url
	 *            the URL
	 */
	public HttpResponse post(final String url) {
		return doRequest(Method.POST, url, null, null);
	}

	/**
	 * Perform a HTTP POST request with additional headers
	 * 
	 * @param url
	 *            the URL
	 * @param headers
	 *            additional headers for the request
	 */
	public HttpResponse post(final String url, final HttpHeaders headers) {
		return doRequest(Method.POST, url, headers, null);
	}

	/**
	 * Perform a HTTP POST request with parameters
	 * 
	 * @param url
	 *            the URL
	 * @param params
	 *            additional parameters for the request
	 */
	public HttpResponse post(final String url, final HttpParams params) {
		return doRequest(Method.POST, url, null, params);
	}

	/**
	 * Perform a HTTP POST request with additional headers and parameters
	 * 
	 * @param url
	 *            the URL
	 * @param headers
	 *            additional headers for the request
	 * @param params
	 *            additional parameters for the request
	 */
	public HttpResponse post(final String url, final HttpHeaders headers, final HttpParams params) {
		return doRequest(Method.POST, url, headers, params);
	}

	/**
	 * Performs the first HTTP GET/POST request
	 * 
	 * @param method
	 * @param url
	 * @param headers
	 * @param params
	 * @return
	 */
	protected HttpResponse doRequest(final Method method, final String url, final HttpHeaders headers, final HttpParams params) {
		return doRequest(method, url, headers, params, 0);
	}

	/**
	 * Performs a HTTP GET/POST Request
	 * 
	 * @return id of the request
	 */
	protected HttpResponse doRequest(final Method method, String url, HttpHeaders headers, HttpParams params, final int retries) {
		if (headers == null) {
			headers = new HttpHeaders();
		}
		if (params == null) {
			params = new HttpParams();
		}

		final HttpResponse response = new HttpResponse(method, url);
		HttpURLConnection conn = null;
		try {
			/* append query string for GET requests */
			if (method == Method.GET) {
				if (!params.urlParams.isEmpty()) {
					url += ('?' + params.getParamString());
				}
			}

			/* setup headers for POST requests */
			if (method == Method.POST) {
				headers.addHeader("Accept-Charset", requestOptions.encoding);
				if (params.hasMultipartParams()) {
					final SimpleMultipart multipart = params.getMultipart();
					headers.addHeader("Content-Type", multipart.getContentType());
				} else {
					headers.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + requestOptions.encoding);
				}
			}

			/* open and configure the connection */
			conn = (HttpURLConnection) new URL(url).openConnection();

			if (method == Method.GET) {
				conn = (HttpURLConnection) new URL(url).openConnection();
				conn.setRequestMethod("GET");
			} else if (method == Method.POST) {
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
			}
			conn.setAllowUserInteraction(false);
			conn.setReadTimeout(requestOptions.readTimeout);
			conn.setConnectTimeout(requestOptions.connectTimeout);

			/* add headers to the connection */
			for (final Map.Entry<String, List<String>> entry : headers.getHeaders().entrySet()) {
				for (final String value : entry.getValue()) {
					conn.addRequestProperty(entry.getKey(), value);
				}
			}

			response.requestProperties = conn.getRequestProperties();

			/* do post */
			if (method == Method.POST) {
				InputStream is;

				if (params.hasMultipartParams()) {
					is = params.getMultipart().getContent();
				} else {
					is = new ByteArrayInputStream(params.getParamString().getBytes());
				}

				final OutputStream os = conn.getOutputStream();

				writeStream(os, is);
			} else {
				conn.connect();
			}

			response.contentEncoding = conn.getContentEncoding();
			response.contentLength = conn.getContentLength();
			response.contentType = conn.getContentType();
			response.date = conn.getDate();
			response.expiration = conn.getExpiration();
			response.headerFields = conn.getHeaderFields();
			response.ifModifiedSince = conn.getIfModifiedSince();
			response.lastModified = conn.getLastModified();
			response.responseCode = conn.getResponseCode();
			response.responseMessage = conn.getResponseMessage();

			/* do get */
			if (conn.getResponseCode() < 400) {
				response.responseBody = readStream(conn.getInputStream());
			} else {
				response.responseBody = readStream(conn.getErrorStream());
				response.throwable = new HttpException(response.responseMessage);
			}

		} catch (final Exception e) {
			if (retries < requestOptions.maxRetries) {
				return doRequest(method, url, headers, params, retries + 1);
			} else {
				response.responseBody = e.getMessage();
				response.throwable = e;
			}
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return response;
	}

	private String readStream(final InputStream is) throws IOException {
		final BufferedInputStream bis = new BufferedInputStream(is);
		final ByteArrayBuffer baf = new ByteArrayBuffer(50);
		int read = 0;
		final byte[] buffer = new byte[8192];
		while (true) {
			read = bis.read(buffer);
			if (read == -1) {
				break;
			}
			baf.append(buffer, 0, read);
		}

		try {
			bis.close();
		} catch (final IOException e) {}

		try {
			is.close();
		} catch (final IOException e) {}

		return new String(baf.toByteArray());
	}

	private void writeStream(final OutputStream os, final InputStream is) throws IOException {
		final BufferedInputStream bis = new BufferedInputStream(is);
		int read = 0;
		final byte[] buffer = new byte[8192];
		while (true) {
			read = bis.read(buffer);
			if (read == -1) {
				break;
			}
			os.write(buffer, 0, read);
		}
		os.flush();
		try {
			os.close();
		} catch (final IOException e) {}

		try {
			bis.close();
		} catch (final IOException e) {}

		try {
			is.close();
		} catch (final IOException e) {}
	}

	/**
	 * Gets the HttpURLConnection readTimeout
	 * 
	 * @return readTimeout in milliseconds
	 */
	public int getReadTimeout() {
		return requestOptions.readTimeout;
	}

	/**
	 * Set the HttpURLConnection readTimeout
	 * 
	 * @param readTimeout
	 *            in milliseconds
	 */
	public void setReadTimeout(final int readTimeout) {
		requestOptions.readTimeout = readTimeout;
	}

	/**
	 * Gets the HttpURLConnection connectTimeout
	 * 
	 * @return connectTimeout in milliseconds
	 */
	public int getConnectTimeout() {
		return requestOptions.connectTimeout;
	}

	/**
	 * Set the HttpURLConnection connectTimeout
	 * 
	 * @param connectTimeout
	 *            in milliseconds
	 */
	public void setConnectTimeout(final int connectTimeout) {
		requestOptions.connectTimeout = connectTimeout;
	}

	/**
	 * Get the max number of retries
	 * 
	 * @return number of retries
	 */
	public int getMaxRetries() {
		return requestOptions.maxRetries;
	}

	/**
	 * Sets the max number of retries
	 * 
	 * @param maxRetries
	 */
	public void setMaxRetries(final int maxRetries) {
		requestOptions.maxRetries = maxRetries;
	}
}