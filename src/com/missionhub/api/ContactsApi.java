package com.missionhub.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.cr_wd.android.network.HttpHeaders;
import com.cr_wd.android.network.HttpParams;
import com.google.common.collect.HashMultimap;
import com.missionhub.api.model.GPerson;

/**
 * Contacts API Helper
 * 
 * @see missionhub/app/controllers/api/contacts_controller.rb
 * 
 */
public class ContactsApi {

    /**
     * Creates a new Contact
     * 
     * @param context
     * @param person
     * @param assignToMe
     * @param apiHandler
     * @return
     */
    public static ApiRequest create(final Context context,
            final GPerson person, final boolean assignToMe,
            final ApiHandler apiHandler) {
        final ApiClient client = new ApiClient();
        final String url = ApiHelper.getAbsoluteUrl("contacts");
        final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
        final HttpParams params = ApiHelper.getDefaultParams(context);
        addPersonToParams(params, person);
        params.put("assign_to_me", String.valueOf(assignToMe));
        return new ApiRequest(client, client.post(url, headers, params,
                apiHandler));
    }

    private static void addPersonToParams(final HttpParams params,
            final GPerson person) {
        maybeAddToParams(params, "person[firstName]", person.getFirst_name());
        maybeAddToParams(params, "person[lastName]", person.getLast_name());
        maybeAddToParams(params, "person[email_address][email]",
                person.getEmail_address());
        maybeAddToParams(params, "person[phone_number][number]",
                person.getPhone_number());
        maybeAddToParams(params, "person[gender]", person.getGender());
    }

    private static void maybeAddToParams(final HttpParams params,
            final String key, final Object value) {
        if (value != null && !value.toString().trim().equals("")) {
            params.put(key, value);
        }
    }

    /**
     * Get a list of basic hash contacts
     * 
     * @param context
     * @param options
     * @param apiHandler
     * @return
     */
    public static ApiRequest list(final Context context,
            final ContactsApi.Options options, final ApiHandler apiHandler) {
        final ApiClient client = new ApiClient();
        final String url = ApiHelper.getAbsoluteUrl("contacts");
        final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
        final HttpParams params = ApiHelper.getDefaultParams(context);
        options.appendLimits(params);
        options.appendFiltersParams(params);
        options.appendOrderByParam(params);
        return new ApiRequest(client, client.get(url, headers, params,
                apiHandler));
    }

    /**
     * Get an individual full-hash contact
     * 
     * @param context
     * @param personId
     * @param apiHandler
     * @return
     */
    public static ApiRequest get(final Context context, final long personId,
            final ApiHandler apiHandler) {
        final ApiClient client = new ApiClient();
        final String url = ApiHelper.getAbsoluteUrl("contacts",
                String.valueOf(personId));
        final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
        final HttpParams params = ApiHelper.getDefaultParams(context);
        return new ApiRequest(client, client.get(url, headers, params,
                apiHandler));
    }

    /**
     * Get multiple full-hash contacts
     * 
     * @param context
     * @param personIds
     * @param apiHandler
     * @return
     */
    public static ApiRequest get(final Context context,
            final List<Long> personIds, final ApiHandler apiHandler) {
        final ApiClient client = new ApiClient();
        final String url = ApiHelper.getAbsoluteUrl("contacts",
                ApiHelper.toList(personIds));
        final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
        final HttpParams params = ApiHelper.getDefaultParams(context);
        return new ApiRequest(client, client.get(url, headers, params,
                apiHandler));
    }

    /**
     * Options for Contacts.getList
     */
    public static class Options implements Parcelable {

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeInt(start);
            dest.writeInt(limit);
            dest.writeSerializable(filters);
            dest.writeSerializable(orderBy);
        }

        @SuppressWarnings("unchecked")
        private void readFromParcel(final Parcel in) {
            start = in.readInt();
            limit = in.readInt();
            filters = (HashMultimap<String, String>) in.readSerializable();
            orderBy = (HashMap<String, String>) in.readSerializable();
        }

        @SuppressWarnings("rawtypes")
        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            @Override
            public Options createFromParcel(final Parcel in) {
                return new Options(in);
            }

            @Override
            public Options[] newArray(final int size) {
                return new Options[size];
            }
        };

        private int start = 0;
        private int limit = 20;

        private HashMultimap<String, String> filters = HashMultimap
                .<String, String> create();
        private HashMap<String, String> orderBy = new HashMap<String, String>();

        public Options() {
        }

        public Options(final Parcel in) {
            readFromParcel(in);
        }

        public int getStart() {
            return start;
        }

        public void setStart(final int start) {
            this.start = start;
        }

        public void incrementStart(final int num) {
            this.start += num;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(final int limit) {
            this.limit = limit;
        }

        public HttpParams appendLimits(final HttpParams params) {
            params.put("start", String.valueOf(start));
            params.put("limit", String.valueOf(limit));
            return params;
        }

        public HashMultimap<String, String> getFilters() {
            return filters;
        }

        public HttpParams appendFiltersParams(final HttpParams params) {
            final Iterator<String> itr = filters.keySet().iterator();
            while (itr.hasNext()) {
                final String filter = itr.next();
                final StringBuffer value = new StringBuffer();
                final Iterator<String> itr2 = filters.get(filter).iterator();
                while (itr2.hasNext()) {
                    final String val = itr2.next();
                    value.append(ApiHelper.stripUnsafeChars(val));
                    if (itr2.hasNext()) {
                        value.append("|");
                    }
                }
                params.put("filters[" + ApiHelper.stripUnsafeChars(filter)
                        + "]", value.toString());
            }
            return params;
        }

        public void addFilter(final String filter, final String value) {
            filters.put(filter, value);
        }

        public void setFilter(final String filter, final String value) {
            removeFilter(filter);
            addFilter(filter, value);
        }

        public void removeFilter(final String filter) {
            filters.removeAll(filter);
        }

        public void removeFilterValue(final String filter, final String value) {
            filters.remove(filter, value);
        }

        public boolean hasFilter(final String filter) {
            return filters.containsKey(filter);
        }

        public boolean hasFilter(final String filter, final String value) {
            return filters.containsEntry(filter, value);
        }

        public String getFilterValue(final String filter) {
            final Iterator<String> itr = filters.get(filter).iterator();
            while (itr.hasNext()) {
                return itr.next();
            }
            return null;
        }

        public Set<String> getFilterValues(final String filter) {
            return filters.get(filter);
        }

        public void clearFilters() {
            filters.clear();
        }

        public HashMap<String, String> getOrderBy() {
            return orderBy;
        }

        public HttpParams appendOrderByParam(final HttpParams params) {
            final StringBuffer sb = new StringBuffer();
            final Iterator<Entry<String, String>> itr = orderBy.entrySet()
                    .iterator();
            while (itr.hasNext()) {
                final Entry<String, String> entry = itr.next();
                sb.append(ApiHelper.stripUnsafeChars(entry.getKey()) + ","
                        + ApiHelper.stripUnsafeChars(entry.getValue()));
                if (itr.hasNext()) {
                    sb.append("|");
                }
            }

            if (!orderBy.isEmpty()) {
                params.put("order_by", sb.toString());
            }

            return params;
        }

        public void addOrderBy(final String value) {
            orderBy.put(value, "asc");
        }

        public void addOrderBy(final String value, final String direction) {
            orderBy.put(value, direction);
        }

        public void removeOrderBy(final String value) {
            orderBy.remove(value);
        }

        public void clearOrderBy() {
            orderBy.clear();
        }

        public String getTag() {
            final HttpParams params = new HttpParams();
            this.appendFiltersParams(params);
            this.appendOrderByParam(params);
            return params.toString();
        }

        @Override
        public String toString() {
            final HttpParams params = new HttpParams();
            this.appendFiltersParams(params);
            this.appendLimits(params);
            this.appendOrderByParam(params);
            return params.toString();
        }
    }
}
