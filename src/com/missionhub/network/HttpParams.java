package com.missionhub.network;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ch.boye.httpclientandroidlib.Consts;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.ByteArrayBody;
import ch.boye.httpclientandroidlib.entity.mime.content.ContentBody;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;
import ch.boye.httpclientandroidlib.entity.mime.content.InputStreamBody;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

public class HttpParams {

	private final Charset mEncoding = Consts.UTF_8;

	private final ConcurrentMap<String, String> mParams = new ConcurrentHashMap<String, String>();
	private final ConcurrentMap<String, String> mUriParams = new ConcurrentHashMap<String, String>();
	private final ConcurrentMap<String, ContentBody> mMultipartParams = new ConcurrentHashMap<String, ContentBody>();

	/**
	 * Creates a new HttpParams object
	 */
	public HttpParams() {}

	/**
	 * Returns an unmodifiable map of the basic params
	 * 
	 * @return
	 */
	public Map<String, String> getParams() {
		return Collections.unmodifiableMap(mParams);
	}

	/**
	 * Returns an unmodifiable map of the uri params
	 */
	public Map<String, String> getUriParams() {
		return Collections.unmodifiableMap(mUriParams);
	}

	/**
	 * Adds a parameter that is always part of the uri
	 * 
	 * @param key
	 * @param value
	 */
	public void addUri(final String key, final String value) {
		if (key != null && value != null) {
			mUriParams.put(key, value);
		}
	}

	/**
	 * Adds a key/value string pair to the request.
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param value
	 *            the value string for the new param.
	 */
	public void addUri(final String key, final Object value) {
		addUri(key, value.toString());
	}

	/**
	 * Adds a key/value string pair to the request.
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param value
	 *            the value string for the new param.
	 */
	public void add(final String key, final String value) {
		if (key != null && value != null) {
			mParams.put(key, value);
		}
	}

	/**
	 * Adds a key/value pair to the request. Calls Object.toString() to determine value.
	 * 
	 * @param key
	 * @param value
	 */
	public void add(final String key, final Object value) {
		add(key, value.toString());
	}

	/**
	 * Adds a byte array to the request.
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param bytes
	 *            the byte array
	 * @param filename
	 *            the filename the data represents
	 */
	public void add(final String key, final byte[] bytes, final String filename) {
		mMultipartParams.put(key, new ByteArrayBody(bytes, filename));
	}

	/**
	 * Adds a file to the request.
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param file
	 *            the file to add.
	 */
	public void add(final String key, final File file) {
		mMultipartParams.put(key, new FileBody(file));
	}

	/**
	 * Adds an input stream to the request.
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param stream
	 *            the input stream to add.
	 * @param filename
	 *            the name of the file.
	 */
	public void add(final String key, final InputStream stream, final String filename) {
		mMultipartParams.put(key, new InputStreamBody(stream, filename));
	}

	/**
	 * Removes a parameter from the request.
	 * 
	 * @param key
	 *            the key name for the parameter to remove.
	 */
	public void remove(final String key) {
		mParams.remove(key);
		mUriParams.remove(key);
		mMultipartParams.remove(key);
	}

	/**
	 * Returns the HttpEntity for POST requests.
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public HttpEntity getEntity() throws UnsupportedEncodingException {
		HttpEntity entity = null;

		if (mMultipartParams.isEmpty()) {
			// URL Encoded

			final List<NameValuePair> params = new ArrayList<NameValuePair>();
			final Iterator<Entry<String, String>> itr = mParams.entrySet().iterator();
			while (itr.hasNext()) {
				final Entry<String, String> entry = itr.next();
				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			entity = new UrlEncodedFormEntity(params, mEncoding);
		} else {
			// Multipart

			final MultipartEntity mpe = new MultipartEntity();

			// add string values
			final Iterator<Entry<String, String>> itr = mParams.entrySet().iterator();
			while (itr.hasNext()) {
				final Entry<String, String> entry = itr.next();
				mpe.addPart(entry.getKey(), new StringBody(entry.getValue(), mEncoding));
			}

			// add files
			final Iterator<Entry<String, ContentBody>> itr2 = mMultipartParams.entrySet().iterator();
			while (itr2.hasNext()) {
				final Entry<String, ContentBody> entry = itr2.next();
				mpe.addPart(entry.getKey(), entry.getValue());
			}

			entity = mpe;
		}

		return entity;
	}

	/**
	 * Returns true if there are multipart contents
	 * 
	 * @return
	 */
	public boolean isMultipart() {
		return !mMultipartParams.isEmpty();
	}

	public void addAll(final HttpParams params) {
		mParams.putAll(params.mParams);
		mUriParams.putAll(params.mUriParams);
		mMultipartParams.putAll(params.mMultipartParams);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		if (!mParams.isEmpty()) {
			sb.append("PARAMS:\n");
			for(Entry<String, String> entry : mParams.entrySet()) {
				sb.append(entry.getKey() + " => " + entry.getValue() + "\n");
			}
		}
		if(!mUriParams.isEmpty()) {
			sb.append("URI PARAMS:\n");
			for(Entry<String, String> entry : mUriParams.entrySet()) {
				sb.append(entry.getKey() + " => " + entry.getValue() + "\n");
			}
		}
		if(!mMultipartParams.isEmpty()) {
			sb.append("MULTIPART PARAMS:\n");
			for(Entry<String, ContentBody> entry : mMultipartParams.entrySet()) {
				sb.append(entry.getKey() + " => " + entry.getValue() + "\n");
			}
		}
		return sb.toString();
	}
}