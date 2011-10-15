package com.missionhub.api.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.content.Context;

import com.google.common.collect.HashMultimap;
import com.loopj.android.http.RequestParams;
import com.missionhub.api.ApiClient;
import com.missionhub.api.ApiHelper;
import com.missionhub.api.ApiResponseHandler;

public class Contacts {
	
	/**
	 * Get a list of basic hash contacts
	 * @param ctx
	 * @param options
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient list(Context ctx, Contacts.Options options, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteJsonUrl("contacts");
		RequestParams params = ApiHelper.getDefaultRequestParams();
		options.appendLimits(params);
		options.appendFiltersParams(params);
		options.appendOrderByParam(params);
		client.get(url, params, responseHandler);
		return client;
	}
	
	/**
	 * Get an individual full-hash contact
	 * @param ctx
	 * @param personId
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient get(Context ctx, int personId, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteJsonUrl("contacts", String.valueOf(personId));
		RequestParams params = ApiHelper.getDefaultRequestParams();
		client.get(url, params, responseHandler);
		return client;
	}
	
	/**
	 * Get multiple full-hash contacts
	 * @param ctx
	 * @param personIds
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient get(Context ctx, List<Integer> personIds, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteJsonUrl("contacts", ApiHelper.toList(personIds));
		RequestParams params = ApiHelper.getDefaultRequestParams();
		client.get(url, params, responseHandler);
		return client;
	}
	
	/**
	 * Options for Contacts.getList
	 */
	public static class Options {
		
		private int start = 0;
		private int limit = 15;
		
		private HashMultimap<String, String> filters = HashMultimap.<String, String> create();
		private HashMap<String, String> orderBy = new HashMap<String, String>();
		
		public Options() {}
		
		public int getStart() {
			return start;
		}
		
		public void setStart(int start) {
			this.start = start;
		}
		
		public void incrementStart(int num) {
			this.start += num;
		}
		
		public int getLimit() {
			return limit;
		}
		
		public void setLimit(int limit) {
			this.limit = limit;
		}
		
		public RequestParams appendLimits(RequestParams params) {
			params.put("start", String.valueOf(start));
			params.put("limit", String.valueOf(limit));
			return params;
		}
		
		public HashMultimap<String, String> getFilters() {
			return filters;
		}
		
		public RequestParams appendFiltersParams(RequestParams params) {
			Iterator<String> itr = filters.keySet().iterator();
			while (itr.hasNext()) {
				String filter = itr.next();
				StringBuffer value = new StringBuffer();
				Iterator<String> itr2 = filters.get(filter).iterator();
				while (itr2.hasNext()) {
					String val = itr2.next();
					value.append(ApiHelper.stripUnsafeChars(val));
					if (itr.hasNext()) {
						value.append("|");
					}
				}
				params.put("filters[" + ApiHelper.stripUnsafeChars(filter) + "]", value.toString());
			}
			return params;
		}
		
		public void addFilter(String filter, String value) {
			filters.put(filter, value);
		}
		
		public void removeFilter(String filter) {
			filters.removeAll(filter);
		}
		
		public void removeFilterValue(String filter, String value) {
			filters.remove(filter, value);
		}
		
		public void clearFilters() {
			filters.clear();
		}
		
		public HashMap<String, String> getOrderBy() {
			return orderBy;
		}
		
		public RequestParams appendOrderByParam(RequestParams params) {
			StringBuffer sb = new StringBuffer();
			Iterator<Entry<String, String>> itr = orderBy.entrySet().iterator();
			while (itr.hasNext()) {
				Entry<String, String> entry = itr.next();
				sb.append(ApiHelper.stripUnsafeChars(entry.getKey()) + "," + ApiHelper.stripUnsafeChars(entry.getValue()));
				if (itr.hasNext()) {
					sb.append("|");
				}
			}
			
			if (!orderBy.isEmpty())
				params.put("order_by", sb.toString());
			
			return params;
		}
		
		public void addOrderBy(String value) {
			orderBy.put(value, "asc");
		}
		
		public void addOrderBy(String value, String direction) {
			orderBy.put(value, direction);
		}
		
		public void removeOrderBy(String value) {
			orderBy.remove(value);
		}
		
		public void clearOrderBy() {
			orderBy.clear();
		}
	}
}