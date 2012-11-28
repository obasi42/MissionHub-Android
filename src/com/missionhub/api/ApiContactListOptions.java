package com.missionhub.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.missionhub.network.HttpParams;
import com.missionhub.util.U;

public class ApiContactListOptions implements Cloneable {

	public static enum Gender {
		male, female
	}

	public static enum Status {
		uncontacted, attempted_contact, contacted, completed, do_not_contact
	}

	public static enum OrderBy {
		first_name, last_name, phone, email, gender, status, date_created, date_surveyed, created_at
	}

	public static enum OrderDirection {
		asc, desc
	}

	/** the position to fetch from */
	private int mStart = 0;

	/** number of contact to fetch */
	private int mLimit = 10;

	/** the filters */
	private final Multimap<String, String> mFilters;

	/** the ordering */
	private final Map<OrderBy, OrderDirection> mOrderBy;

	public ApiContactListOptions() {
		mFilters = Multimaps.synchronizedSetMultimap(HashMultimap.<String, String> create());
		mOrderBy = Collections.synchronizedMap(new LinkedHashMap<OrderBy, OrderDirection>());
	}

	private ApiContactListOptions(final int start, final int limit, final Multimap<String, String> filters, final Map<OrderBy, OrderDirection> orderBy) {
		mStart = start;
		mLimit = limit;
		mFilters = Multimaps.synchronizedSetMultimap(HashMultimap.<String, String> create(filters));
		mOrderBy = Collections.synchronizedMap(new LinkedHashMap<OrderBy, OrderDirection>(orderBy));
	}

	private void setOrRemoveFilter(final String filter, final Object value) {
		if (U.isNullEmpty(String.valueOf(value))) {
			removeFilter(filter);
		} else {
			setFilter(filter, String.valueOf(value));
		}
	}

	/**
	 * @param personId
	 *            < 0 = none else id
	 */
	public void setFilterAssignedTo(final Long personId) {
		if (personId == null) {
			removeFilter("assigned_to");
		} else if (personId < 0) {
			setFilter("assigned_to", "none");
		} else {
			setFilter("assigned_to", String.valueOf(personId));
		}
	}

	public void setFilterName(final String name) {
		setOrRemoveFilter("name", name);
	}

	public void setFilterFirstName(final String firstName) {
		setOrRemoveFilter("first_name", firstName);
	}

	public void setFilterLastName(final String lastName) {
		setOrRemoveFilter("last_name", lastName);
	}

	public void setFilterEmail(final String email) {
		setOrRemoveFilter("email", email);
	}

	public void setFilterPhone(final String phone) {
		setOrRemoveFilter("phone", phone);
	}

	public void setFilterGender(final Gender gender) {
		setOrRemoveFilter("gender", gender);
	}

	public void setFilterStatus(final Status... statuses) {
		removeFilter("status");
		if (statuses != null && statuses.length > 0) {
			for (final Status status : statuses) {
				addFilter("status", status.name());
			}
		}
	}

	public void clearOrderBy() {
		mOrderBy.clear();
	}

	public void setOrderBy(final OrderBy order, final OrderDirection direction) {
		clearOrderBy();
		addOrderBy(order, direction);
	}

	public void addOrderBy(final OrderBy order, final OrderDirection direction) {
		mOrderBy.put(order, direction);
	}

	public void removeOrderBy(final OrderBy order) {
		mOrderBy.remove(order.name());
	}

	public int getStart() {
		return mStart;
	}

	public void setStart(final int start) {
		mStart = start;
	}

	public void incrementStart(final int num) {
		mStart += num;
	}

	public void advanceStart() {
		mStart += mLimit;
	}

	public void resetPosition() {
		mStart = 0;
	}

	public int getLimit() {
		return mLimit;
	}

	public void setmLimit(final int limit) {
		mLimit = limit;
	}

	private HttpParams appendLimits(final HttpParams params) {
		params.add("start", String.valueOf(mStart));
		params.add("limit", String.valueOf(mLimit));
		return params;
	}

	public Multimap<String, String> getFilters() {
		return HashMultimap.<String, String> create(mFilters);
	}

	private HttpParams appendFiltersParams(final HttpParams params) {
		final Iterator<String> itr = mFilters.keySet().iterator();
		while (itr.hasNext()) {
			final String filter = itr.next();
			final StringBuffer value = new StringBuffer();
			final Iterator<String> itr2 = mFilters.get(filter).iterator();
			while (itr2.hasNext()) {
				final String val = itr2.next();
				value.append(stripUnsafeChars(val));
				if (itr2.hasNext()) {
					value.append("|");
				}
			}
			params.add("filters[" + stripUnsafeChars(filter) + "]", value.toString());
		}
		return params;
	}

	public void addFilter(final String filter, final String value) {
		mFilters.put(filter, value);
	}

	public void setFilter(final String filter, final String value) {
		removeFilter(filter);
		addFilter(filter, value);
	}

	public void removeFilter(final String filter) {
		mFilters.removeAll(filter);
	}

	public void removeFilterValue(final String filter, final String value) {
		mFilters.remove(filter, value);
	}

	public boolean hasFilter(final String filter) {
		return mFilters.containsKey(filter);
	}

	public boolean hasFilter(final String filter, final String value) {
		return mFilters.containsEntry(filter, value);
	}

	public String getFilterValue(final String filter) {
		final Iterator<String> itr = mFilters.get(filter).iterator();
		while (itr.hasNext()) {
			return itr.next();
		}
		return null;
	}

	public Collection<String> getFilterValues(final String filter) {
		return mFilters.get(filter);
	}

	public void clearFilters() {
		mFilters.clear();
	}

	public Map<OrderBy, OrderDirection> getOrderBy() {
		return new HashMap<OrderBy, OrderDirection>(mOrderBy);
	}

	private HttpParams appendOrderByParam(final HttpParams params) {
		final StringBuffer sb = new StringBuffer();
		final Iterator<Entry<OrderBy, OrderDirection>> itr = mOrderBy.entrySet().iterator();
		while (itr.hasNext()) {
			final Entry<OrderBy, OrderDirection> entry = itr.next();
			sb.append(stripUnsafeChars(entry.getKey().name()) + "," + stripUnsafeChars(entry.getValue().name()));
			if (itr.hasNext()) {
				sb.append("|");
			}
		}

		if (!mOrderBy.isEmpty()) params.add("order_by", sb.toString());

		return params;
	}

	protected String getTag() {
		final HttpParams params = new HttpParams();
		this.appendFiltersParams(params);
		this.appendOrderByParam(params);
		return params.toString();
	}

	private String stripUnsafeChars(final String string) {
		return string.replaceAll("[\\]\\[|=?]", "");
	}

	@Override
	public String toString() {
		final HttpParams params = new HttpParams();
		this.appendFiltersParams(params);
		this.appendLimits(params);
		this.appendOrderByParam(params);
		return params.toString();
	}
	
	protected void appendParams(HttpParams params) {
		appendLimits(params);
		appendFiltersParams(params);
		appendOrderByParam(params);
	}

	@Override
	public Object clone() {
		return new ApiContactListOptions(mStart, mLimit, mFilters, mOrderBy);
	}
}