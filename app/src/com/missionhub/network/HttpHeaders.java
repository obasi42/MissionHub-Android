package com.missionhub.network;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class HttpHeaders {

    /* Stores the request headers */
    private final ConcurrentMap<String, List<String>> headers = new ConcurrentHashMap<String, List<String>>();

    /**
     * Creates a HttpHeaders object
     */
    public HttpHeaders() {
    }

    /**
     * Adds a request header
     *
     * @param key
     * @param value
     */
    public void addHeader(final String key, final String value) {
        List<String> list = headers.get(key);
        if (list != null) {
            list.add(value);
        } else {
            list = new ArrayList<String>();
            list.add(value);
            headers.put(key, list);
        }
    }

    /**
     * Sets the request header to a value
     *
     * @param key
     * @param value
     */
    public void setHeader(final String key, final String value) {
        final List<String> list = new ArrayList<String>();
        list.add(value);
        headers.put(key, list);
    }

    /**
     * Sets the request header to a list of values
     *
     * @param key
     * @param value
     */
    public void setHeader(final String key, final ArrayList<String> values) {
        headers.put(key, values);
    }

    /**
     * Returns the first header for a key
     *
     * @return header value or null
     */
    public String getHeader(final String key) {
        final List<String> list = headers.get(key);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /**
     * Returns an unmodifiable copy of the request headers
     *
     * @return unmodifiable copy of the request headers
     */
    public Map<String, List<String>> getHeaders() {
        final Map<String, List<String>> tmp = new HashMap<String, List<String>>();

        final Iterator<Entry<String, List<String>>> itr = headers.entrySet().iterator();
        while (itr.hasNext()) {
            final Entry<String, List<String>> entry = itr.next();
            final List<String> tmpList = new ArrayList<String>();

            for (final String s : entry.getValue()) {
                tmpList.add(s);
            }

            tmp.put(entry.getKey(), Collections.unmodifiableList(tmpList));
        }

        return Collections.unmodifiableMap(tmp);
    }

    /**
     * Returns all headers for a key
     *
     * @param key
     * @return List of headers
     */
    public List<String> getHeaders(final String key) {
        final List<String> tmpList = new ArrayList<String>();
        final List<String> values = headers.get(key);
        if (values != null) {
            for (final String s : values) {
                tmpList.add(s);
            }
        }
        return Collections.unmodifiableList(tmpList);
    }

    /**
     * Removes all header values with the specified key
     *
     * @param key
     */
    public void removeHeader(final String key) {
        headers.remove(key);
    }

    /**
     * Removes a header with a specified value from the request headers
     *
     * @param key
     * @param value
     */
    public void removeHeader(final String key, final String value) {
        final List<String> list = headers.get(key);
        if (list != null) {
            list.remove(value);
            if (list.isEmpty()) {
                headers.remove(key);
            }
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();

        final Iterator<Entry<String, List<String>>> itr = getHeaders().entrySet().iterator();
        while (itr.hasNext()) {
            final Entry<String, List<String>> entry = itr.next();
            for (final String s : entry.getValue()) {
                sb.append(entry.getKey() + "=" + s + "\n");
            }
        }

        return sb.toString();
    }

    public void addAll(final HttpHeaders headers) {
        this.headers.putAll(headers.headers);
    }
}