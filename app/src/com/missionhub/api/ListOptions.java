package com.missionhub.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.missionhub.api.ListOptions.ListFilterOrderJson.Filter;
import com.missionhub.api.ListOptions.ListFilterOrderJson.Order;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ListOptions implements Serializable {

    /**
     * the filters
     */
    private final Multimap<String, String> mFilters;

    /**
     * the order directions
     */
    public static enum Direction {
        ASC, DESC
    }

    /**
     * the ordering
     */
    private final Map<String, Direction> mOrderBy;

    /**
     * the limit
     */
    private Integer mLimit = 15;

    /**
     * the offset
     */
    private Integer mOffset = 0;

    /**
     * creates a new list filter object
     */
    public ListOptions() {
        mFilters = Multimaps.synchronizedSetMultimap(HashMultimap.<String, String>create());
        mOrderBy = Collections.synchronizedMap(new LinkedHashMap<String, Direction>());
    }

    /**
     * used by clone to create a new list filter object
     */
    private ListOptions(final HashMultimap<String, String> filters, final Map<String, Direction> orderBy, final Integer limit, final Integer offset) {
        mFilters = Multimaps.synchronizedSetMultimap(filters);
        mOrderBy = Collections.synchronizedMap(orderBy);
        mLimit = limit;
        mOffset = offset;
    }

    protected ListOptions(final ListOptions options) {
        mFilters = options.mFilters;
        mOrderBy = options.mOrderBy;
        mLimit = options.mLimit;
        mOffset = options.mOffset;
    }

    /**
     * returns a copy of the list filters
     */
    public Multimap<String, String> getFilters() {
        return HashMultimap.create(mFilters);
    }

    /**
     * adds the filters to the http params
     */
    protected void toParams(final Map<String, String> params) {
        for (String filter : mFilters.keySet()) {
            final StringBuilder value = new StringBuilder();
            final Iterator<String> itr2 = mFilters.get(filter).iterator();
            while (itr2.hasNext()) {
                final String val = itr2.next();
                value.append(stripUnsafeChars(val));
                if (itr2.hasNext()) {
                    value.append(",");
                }
            }
            params.put("filters[" + stripUnsafeChars(filter) + "]", value.toString());
        }

        final StringBuilder sb = new StringBuilder();
        final Iterator<Entry<String, Direction>> itr2 = mOrderBy.entrySet().iterator();
        while (itr2.hasNext()) {
            final Entry<String, Direction> entry = itr2.next();
            sb.append(stripUnsafeChars(entry.getKey()) + " " + stripUnsafeChars(entry.getValue().name()));
            if (itr2.hasNext()) {
                sb.append(",");
            }
        }
        if (!mOrderBy.isEmpty()) params.put("order", sb.toString());

        if (mLimit != null) {
            params.put("limit", String.valueOf(mLimit));
        }

        if (mOffset != null) {
            params.put("offset", String.valueOf(mOffset));
        }
    }

    private String stripUnsafeChars(final String string) {
        return string.replaceAll("[\\]\\[|=?]", "");
    }

    protected void setOrRemoveFilter(final String filter, final Collection<Object> values) {
        if (values == null || values.isEmpty()) {
            removeFilter(filter);
        } else {
            setFilter(filter, values);
        }
    }

    protected void setOrRemoveFilter(final String filter, final Object value) {
        if (StringUtils.isEmpty(String.valueOf(value))) {
            removeFilter(filter);
        } else {
            setFilter(filter, objectToString(value));
        }
    }

    private String objectToString(final Object object) {
        if (object == null) return null;

        if (object instanceof Enum) {
            return ((Enum<?>) object).name();
        } else {
            return String.valueOf(object);
        }
    }

    private Collection<String> objectsToStrings(final Collection<Object> objects) {
        final Set<String> strings = new HashSet<String>();
        for (final Object object : objects) {
            strings.add(objectToString(object));
        }
        return strings;
    }

    public void addFilter(final String filter, final Object value) {
        mFilters.put(filter, objectToString(value));
    }

    public void addFilter(final String filter, final Collection<Object> values) {
        mFilters.putAll(filter, objectsToStrings(values));
    }

    public void setFilter(final String filter, final Object value) {
        removeFilter(filter);
        addFilter(filter, objectToString(value));
    }

    public void setFilter(final String filter, final Collection<Object> values) {
        removeFilter(filter);
        addFilter(filter, objectsToStrings(values));
    }

    public void removeFilter(final String filter) {
        mFilters.removeAll(filter);
    }

    public void removeFilterValue(final String filter, final Object value) {
        mFilters.remove(filter, objectToString(value));
    }

    public boolean hasFilter(final String filter) {
        return mFilters.containsKey(filter);
    }

    public boolean hasFilter(final String filter, final Object value) {
        return mFilters.containsEntry(filter, objectToString(value));
    }

    public String getFilterValue(final String filter) {
        for (String s : mFilters.get(filter)) {
            return s;
        }
        return null;
    }

    public Collection<String> getFilterValues(final String filter) {
        return mFilters.get(filter);
    }

    public void clearFilters() {
        mFilters.clear();
    }

    public void toggle(final String filter, final Object value) {
        String val = objectToString(value);
        if (val.equalsIgnoreCase("true")) {
            setFilter(filter, "false");
        } else if (val.equalsIgnoreCase("false")) {
            setFilter(filter, "true");
        } else {
            if (hasFilter(filter, value)) {
                removeFilterValue(filter, value);
            } else {
                addFilter(filter, value);
            }
        }
    }

    public Map<String, Direction> getOrders() {
        return mOrderBy;
    }

    /**
     * Clears all order by values
     */
    public void clearOrders() {
        mOrderBy.clear();
    }

    /**
     * Clears and sets the order by to the new single value
     *
     * @param order
     * @param direction
     */
    public void setOrder(final Object order, final Direction direction) {
        clearOrders();
        addOrder(order.toString(), direction);
    }

    /**
     * Adds an order by value
     *
     * @param order
     * @param direction
     */
    public void addOrder(final Object order, final Direction direction) {
        mOrderBy.put(order.toString(), direction);
    }

    /**
     * Removes an order by value
     *
     * @param order
     */
    public void removeOrder(final Object order) {
        mOrderBy.remove(order.toString());
    }

    public Integer getLimit() {
        return mLimit;
    }

    public void setLimit(final Integer limit) {
        mLimit = limit;
    }

    public void removeLimit() {
        setLimit(null);
    }

    public Integer getOffset() {
        return mOffset;
    }

    public void setOffset(final Integer offset) {
        mOffset = offset;
    }

    public void removeOffset() {
        mOffset = null;
    }

    public void advanceOffset() {
        mOffset += mLimit;
    }

    public String toJson() {
        final List<Filter> filters = new ArrayList<Filter>();
        final List<Order> orders = new ArrayList<Order>();

        final Iterator<Entry<String, Collection<String>>> itr = mFilters.asMap().entrySet().iterator();
        while (itr.hasNext()) {
            final Entry<String, Collection<String>> entry = itr.next();
            final Filter filter = new Filter();
            filter.key = entry.getKey();
            filter.values = entry.getValue().toArray(new String[]{});
            filters.add(filter);
        }

        final Iterator<Entry<String, Direction>> itr2 = mOrderBy.entrySet().iterator();
        while (itr.hasNext()) {
            final Entry<String, Direction> entry = itr2.next();
            final Order order = new Order();
            order.key = entry.getKey();
            order.direction = entry.getValue().name();
            orders.add(order);
        }

        final ListFilterOrderJson jsonFilter = new ListFilterOrderJson();
        jsonFilter.filters = filters.toArray(new Filter[]{});
        jsonFilter.orders = orders.toArray(new Order[]{});
        jsonFilter.offset = mOffset;
        jsonFilter.limit = mLimit;

        return Api.sGson.toJson(jsonFilter);
    }

    public static ListOptions fromJson(final String jsonString) {
        final ListFilterOrderJson json = Api.sGson.fromJson(jsonString, ListFilterOrderJson.class);
        final HashMultimap<String, String> filters = HashMultimap.<String, String>create();
        for (final Filter filter : json.filters) {
            filters.putAll(filter.key, Arrays.asList(filter.values));
        }
        final LinkedHashMap<String, Direction> orders = new LinkedHashMap<String, Direction>();
        for (final Order order : json.orders) {
            orders.put(order.key, Direction.valueOf(order.direction));
        }

        return new ListOptions(filters, orders, json.limit, json.offset);
    }

    @Override
    public Object clone() {
        return new ListOptions(HashMultimap.<String, String>create(mFilters), new LinkedHashMap<String, Direction>(mOrderBy), mLimit, mOffset);
    }

    protected static class ListFilterOrderJson {

        public Filter[] filters;
        public Order[] orders;
        public Integer offset;
        public Integer limit;

        public static class Filter {
            public String key;
            public String[] values;
        }

        public static class Order {
            public String key;
            public String direction;
        }

    }

}