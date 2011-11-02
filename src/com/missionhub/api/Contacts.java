package com.missionhub.api;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;

import com.google.common.collect.HashMultimap;
import com.loopj.android.http.RequestParams;
import com.missionhub.api.convert.PersonJsonSql;
import com.missionhub.api.model.json.GContact;
import com.missionhub.api.model.json.GMetaContact;

public class Contacts {

	/**
	 * Get a list of basic hash contacts
	 * 
	 * @param ctx
	 * @param options
	 * @param tag
	 */
	public static void list(Context ctx, Contacts.Options options, String tag) {
		list(ctx, options, new ContactsListNotifierResponseHandler(ctx, GMetaContact.class, tag, "CONTACTS"));
	}

	private static class ContactsListNotifierResponseHandler extends ApiNotifierResponseHandler {

		public ContactsListNotifierResponseHandler(Context ctx, Type t, String tag, String type) {
			super(ctx, t, tag, type);
		}

		@Override
		public void onSuccess(Object gMetaContact) {
			GMetaContact contacts = (GMetaContact) gMetaContact;
			for (GContact contact : contacts.getContacts()) {
				PersonJsonSql.update(ctx, contact, tag);
			}
			// TODO: handle questions and keywords
			super.onSuccess(gMetaContact);
		}
	}

	/**
	 * Get an individual full-hash contact
	 * 
	 * @param ctx
	 * @param personId
	 * @param tag
	 */
	public static void get(Context ctx, int personId, String tag) {
		get(ctx, personId, new ContactsNotifierResponseHandler(ctx, GMetaContact.class, tag, "CONTACTS"));
	}

	/**
	 * Get multiple full-hash contacts
	 * 
	 * @param ctx
	 * @param personIds
	 * @param tag
	 */
	public static void get(Context ctx, List<Integer> personIds, String tag) {
		get(ctx, personIds, new ContactsNotifierResponseHandler(ctx, GMetaContact.class, tag, "CONTACTS"));
	}

	private static class ContactsNotifierResponseHandler extends ApiNotifierResponseHandler {

		public ContactsNotifierResponseHandler(Context ctx, Type t, String tag, String type) {
			super(ctx, t, tag, type);
		}

		@Override
		public void onSuccess(Object gContactAll) {
			GMetaContact contacts = (GMetaContact) gContactAll;
			for (GContact contact : contacts.getContacts()) {
				PersonJsonSql.update(ctx, contact, tag);
			}
			// TODO: handle questions and keywords
			super.onSuccess(gContactAll);
		}
	}

	/**
	 * Get a list of basic hash contacts
	 * 
	 * @param ctx
	 * @param options
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient list(Context ctx, Contacts.Options options, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteUrl("contacts");
		RequestParams params = ApiHelper.getDefaultRequestParams(ctx);
		options.appendLimits(params);
		options.appendFiltersParams(params);
		options.appendOrderByParam(params);
		client.get(url, params, responseHandler);
		return client;
	}

	/**
	 * Get an individual full-hash contact
	 * 
	 * @param ctx
	 * @param personId
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient get(Context ctx, int personId, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteUrl("contacts", String.valueOf(personId));
		RequestParams params = ApiHelper.getDefaultRequestParams(ctx);
		client.get(url, params, responseHandler);
		return client;
	}

	/**
	 * Get multiple full-hash contacts
	 * 
	 * @param ctx
	 * @param personIds
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient get(Context ctx, List<Integer> personIds, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteUrl("contacts", ApiHelper.toList(personIds));
		RequestParams params = ApiHelper.getDefaultRequestParams(ctx);
		client.get(url, params, responseHandler);
		return client;
	}

	/**
	 * Options for Contacts.getList
	 */
	public static class Options {

		private int start = 0;
		private int limit = 20;

		private HashMultimap<String, String> filters = HashMultimap.<String, String> create();
		private HashMap<String, String> orderBy = new HashMap<String, String>();

		public Options() {
		}

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
					if (itr2.hasNext()) {
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

		public void setFilter(String filter, String value) {
			removeFilter(filter);
			addFilter(filter, value);
		}

		public void removeFilter(String filter) {
			filters.removeAll(filter);
		}

		public void removeFilterValue(String filter, String value) {
			filters.remove(filter, value);
		}

		public boolean hasFilter(String filter) {
			return filters.containsKey(filter);
		}

		public boolean hasFilter(String filter, String value) {
			return filters.containsEntry(filter, value);
		}

		public String getFilterValue(String filter) {
			Iterator<String> itr = filters.get(filter).iterator();
			while (itr.hasNext()) {
				return itr.next();
			}
			return null;
		}

		public Set<String> getFilterValues(String filter) {
			return filters.get(filter);
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

		public String getTag() {
			RequestParams params = new RequestParams();
			this.appendFiltersParams(params);
			this.appendOrderByParam(params);
			return params.toString();
		}

		@Override
		public String toString() {
			RequestParams params = new RequestParams();
			this.appendFiltersParams(params);
			this.appendLimits(params);
			this.appendOrderByParam(params);
			return params.toString();
		}
	}
}