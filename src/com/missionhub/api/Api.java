package com.missionhub.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Build;
import android.util.Log;

import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import com.missionhub.application.Application;
import com.missionhub.application.Configuration;
import com.missionhub.application.Session;
import com.missionhub.application.Session.NoAccountException;
import com.missionhub.exception.ApiException;
import com.missionhub.exception.OfflineException;
import com.missionhub.model.FollowupComment;
import com.missionhub.model.Organization;
import com.missionhub.model.Person;
import com.missionhub.model.Rejoicable;
import com.missionhub.model.gson.GAuthTokenDone;
import com.missionhub.model.gson.GContact;
import com.missionhub.model.gson.GMetaCommentTop;
import com.missionhub.model.gson.GMetaContact;
import com.missionhub.model.gson.GMetaOrganizations;
import com.missionhub.model.gson.GMetaPeople;
import com.missionhub.model.gson.GPerson;
import com.missionhub.network.HttpClient;
import com.missionhub.network.HttpClient.Method;
import com.missionhub.network.HttpException;
import com.missionhub.network.HttpHeaders;
import com.missionhub.network.HttpParams;
import com.missionhub.network.NetworkUtils;
import com.missionhub.util.U;

/**
 * The Android MissionHub API Client.
 */
public class Api {

	/** the singleton api object */
	private static Api sApi;

	/** the logging tag */
	private static final String TAG = Api.class.getSimpleName();

	/** the gson parser */
	private final Gson gson = new Gson();

	private Api() {}

	/**
	 * Returns the instance of the API.
	 * 
	 * @return instance of the api
	 */
	public static Api getInstance() {
		if (sApi == null) {
			sApi = new Api();
			Application.registerEventSubscriber(sApi);
		}
		return sApi;
	}

	/**
	 * Performs a http request automatically appending the organization id and access token
	 * 
	 * @param method
	 * @param url
	 *            the url for the request. Can be built buildUrlPath().
	 * @param headers
	 * @param params
	 * @return the response of the request
	 * @throws ApiException
	 *             all exceptions will be wrapped in an ApiException
	 * @throws OfflineException
	 */
	private ApiResponse doRequest(final Method method, final String url, final HttpHeaders headers, final HttpParams params) throws ApiException,
			OfflineException {
		return doRequest(method, url, headers, params, true, Session.getInstance().getOrganizationId());
	}

	/**
	 * Performs a http request
	 * 
	 * @param method
	 * @param url
	 *            the url for the request. Can be built buildUrlPath().
	 * @param headers
	 * @param params
	 * @param authenticated
	 *            true if the access token from the session should be used
	 * @param useOrg
	 * @return the response of the request
	 * @throws ApiException
	 *             all exceptions will be wrapped in an ApiException
	 * @throws OfflineException
	 */
	private ApiResponse doRequest(final Method method, final String url, final HttpHeaders headers, final HttpParams params, final boolean authenticated,
			final long organizationId) throws ApiException, OfflineException {
		return doRequest(null, method, url, headers, params, authenticated, organizationId, 3);
	}

	/**
	 * Performs a http request.
	 * 
	 * @param request
	 *            the request to retry
	 * @param method
	 * @param url
	 *            the url for the request. Can be built buildUrlPath().
	 * @param headers
	 * @param params
	 * @param authenticated
	 *            true if the access token from the session should be used
	 * @param maxRetries
	 *            the number of times the request will be retried automatically
	 * @return the response of the request
	 * @throws ApiException
	 *             all exceptions will be wrapped in an ApiException
	 * @throws OfflineException
	 */
	private ApiResponse doRequest(ApiRequest request, final Method method, final String url, HttpHeaders headers, final HttpParams params,
			final boolean authenticated, final long organizationId, final int maxRetries) throws ApiException, OfflineException {

		/* check for a data connection */
		if (!NetworkUtils.isNetworkAvailable(Application.getContext())) {
			throw new OfflineException();
		}

		/* create the headers object if needed and add the api version header */
		if (headers == null) {
			headers = new HttpHeaders();
		}
		headers.setHeader("Accept", "application/vnd.missionhub-v" + Configuration.getApiVersion() + "+json");

		/* create the request and response objects for holding state data */
		if (request == null) {
			request = new ApiRequest(method, url, headers, params, authenticated, organizationId);
		}

		final ApiResponse response = new ApiResponse(request);

		try {
			/* add oauth token to the request if needed */
			if (request.authenticated) {
				request.headers.setHeader("Authorization", "OAuth: " + Session.getInstance().getAccessToken());
			}

			if (request.params == null) request.params = new HttpParams();
			if (!U.isNullEmptyNegative(request.organizationId)) request.params.put("org_id", request.organizationId);
			appendLoggingParams(request.params);

			final HttpClient client = new HttpClient();
			Log.d(TAG, request.url);
			if (request.params != null) {
				Log.d(TAG, "params: " + request.params);
			}
			if (request.headers != null) {
				Log.d(TAG, "headers: " + request.headers);
			}
			if (request.method == Method.GET) {
				response.httpResponse = client.get(request.url, request.headers, request.params);
			} else if (request.method == Method.POST) {
				response.httpResponse = client.post(request.url, request.headers, request.params);
			}

			final Throwable throwable = response.httpResponse.throwable;

			if (throwable != null) {
				if (throwable instanceof HttpException || throwable instanceof IOException) {
					if (response.httpResponse.responseCode == 401 || throwable.getMessage().contains("authentication")) {
						Session.getInstance().reportInvalidAccessToken();
						throw new AccessTokenException(throwable);
					}
				}
			} else {
				final String body = response.httpResponse.responseBody;
				if (!U.isNullEmpty(body) && body.contains("error")) {
					try {
						final ApiErrorGson error = gson.fromJson(body, ApiErrorGson.class);
						if (error != null) {
							if (error.error.code.equalsIgnoreCase("56")) {
								Session.getInstance().reportInvalidAccessToken();
								throw new AccessTokenException(error);
							} else {
								throw new ApiException(error);
							}
						}
					} catch (final Exception e) {
						// if the exception is not an ApiException, the response is not an JSON api error.
						if (e instanceof ApiException) {
							throw (ApiException) e;
						}
					}
				}
			}
		} catch (final Exception e) {
			if (e instanceof ApiException) {
				throw (ApiException) e;
			} else {
				request.retries++;
				if (request.retries > maxRetries) {
					throw new ApiException(e);
				} else {
					return doRequest(request, method, url, headers, params, authenticated, organizationId, maxRetries);
				}
			}
		}

		return response;
	}

	/**
	 * Error thrown when the access token is invalid or missing
	 */
	public static class AccessTokenException extends ApiException {

		private static final long serialVersionUID = 1L;

		public AccessTokenException(final Throwable cause) {
			super(cause);
		}

		public AccessTokenException(final ApiErrorGson error) {
			super(error);
		}

	}

	/**
	 * Appends parameters for logging
	 * 
	 * @param params
	 */
	private void appendLoggingParams(final HttpParams params) {
		try {
			params.put("platform", "android");
			params.put("platform_product", Build.PRODUCT);
			params.put("platform_release", android.os.Build.VERSION.RELEASE);
			params.put("app", Application.getVersionCode());
		} catch (final Exception e) { /* this is really not that important */}
	}

	/**
	 * Builds an api url
	 * 
	 * @param parts
	 * @return
	 */
	private String buildUrlPath(final Object... parts) {
		final StringBuffer sb = new StringBuffer();
		sb.append(Configuration.getApiUrl() + '/');
		for (int i = 0; i < parts.length; i++) {
			final Object part = parts[i];
			if (part != null) {
				if (part instanceof List) {
					sb.append(U.toCSV((List<?>) part));
				} else {
					sb.append(part);
				}
				if (i + 1 < parts.length) {
					sb.append('/');
				}
			}
		}
		return sb.toString();
	}

	// **********************************//
	// *********** PEOPLE API ***********//
	// **********************************//

	/**
	 * Gets a single person by their id
	 * 
	 * @param personId
	 * @return the person identified by the given personId
	 */
	public static FutureTask<Person> getPerson(final int personId) {
		final Callable<Person> callable = new Callable<Person>() {
			@Override
			public Person call() throws Exception {
				final String url = Api.getInstance().buildUrlPath("people", personId + ".json");
				final ApiResponse response = Api.getInstance().doRequest(Method.GET, url, null, null);
				try {
					final GMetaPeople gmp = Api.getInstance().gson.fromJson(response.httpResponse.responseBody, GMetaPeople.class);
					final Person p = gmp.people[0].save().get();
					return p;
				} catch (final Exception e) {
					throw new ApiException(e);
				}
			}
		};

		final FutureTask<Person> task = new FutureTask<Person>(callable);
		Application.getExecutor().execute(task);
		return task;
	}

	/**
	 * Gets the person associated with the access token
	 * 
	 * @return the person associated with the access token
	 */
	public static FutureTask<Person> getPersonMe() {
		final Callable<Person> callable = new Callable<Person>() {
			@Override
			public Person call() throws Exception {
				final String url = Api.getInstance().buildUrlPath("people", "me.json");
				final ApiResponse response = Api.getInstance().doRequest(Method.GET, url, null, null);
				try {
					final GMetaPeople gmp = Api.getInstance().gson.fromJson(response.httpResponse.responseBody, GMetaPeople.class);
					final Person p = gmp.people[0].save().get();
					return p;
				} catch (final Exception e) {
					throw new ApiException(e);
				}
			}
		};
		final FutureTask<Person> task = new FutureTask<Person>(callable);
		Application.getExecutor().execute(task);
		return task;
	}

	/**
	 * Gets a list of people by their ids
	 * 
	 * @param personIds
	 * @return list of people
	 */
	public static FutureTask<List<Person>> getPeople(final List<Long> personIds) {
		final Callable<List<Person>> callable = new Callable<List<Person>>() {
			@Override
			public List<Person> call() throws Exception {
				final String url = Api.getInstance().buildUrlPath("people", U.toCSV(personIds) + ".json");
				final ApiResponse response = Api.getInstance().doRequest(Method.GET, url, null, null);
				try {
					final GMetaPeople gmp = Api.getInstance().gson.fromJson(response.httpResponse.responseBody, GMetaPeople.class);
					final List<Person> persons = new ArrayList<Person>();
					for (final GPerson gperson : gmp.people) {
						persons.add(gperson.save().get());
					}
					return persons;
				} catch (final Exception e) {
					Log.e("Exception", e.getMessage(), e);
					throw new ApiException(e);
				}
			}
		};
		final FutureTask<List<Person>> task = new FutureTask<List<Person>>(callable);
		Application.getExecutor().execute(task);
		return task;
	}

	// **********************************//
	// ***** CONTACT ASSIGNMENT API *****//
	// **********************************//

	public static enum ContactAssignmentType {
		leader, organization
	}

	/**
	 * Creates a contact assignment
	 * 
	 * @param personIds
	 *            from ids
	 * @param type
	 * @param toIds
	 *            to ids
	 * @return
	 */
	public static FutureTask<Boolean> createContactAssignment(final List<Long> personIds, final ContactAssignmentType type, final List<Long> toIds) {
		final Callable<Boolean> callable = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				final String url = Api.getInstance().buildUrlPath("contact_assignments.json");

				final HttpParams params = new HttpParams();
				params.put("id", U.toCSV(personIds));
				params.put("type", type.name());
				params.put("assign_to_id", U.toCSV(toIds));

				Api.getInstance().doRequest(Method.POST, url, null, params);
				return true;
			}
		};
		final FutureTask<Boolean> task = new FutureTask<Boolean>(callable);
		Application.getExecutor().execute(task);
		return task;
	}

	/**
	 * Deletes a contact assignment
	 * 
	 * @param personIds
	 *            the persons to remove assignments for
	 * @return
	 */
	public static FutureTask<Boolean> deleteContactAssigment(final List<Long> personIds) {
		final Callable<Boolean> callable = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				final String url = Api.getInstance().buildUrlPath("contact_assignments", U.toCSV(personIds) + ".json");

				final HttpParams params = new HttpParams();
				params.put("ids", U.toCSV(personIds));
				params.put("_method", "delete");

				Api.getInstance().doRequest(Method.POST, url, null, params);
				return true;
			}
		};
		final FutureTask<Boolean> task = new FutureTask<Boolean>(callable);
		Application.getExecutor().execute(task);
		return task;
	}

	// **********************************//
	// ********** CONTACTS API **********//
	// **********************************//

	/**
	 * Gets a single contact from the api. This is basically the same as getPerson, however questions answers are also
	 * returned.
	 * 
	 * @param personId
	 * @return
	 */
	public static FutureTask<Person> getContact(final long personId) {
		final Callable<Person> callable = new Callable<Person>() {
			@Override
			public Person call() throws Exception {
				final String url = Api.getInstance().buildUrlPath("contacts", personId + ".json");
				final ApiResponse response = Api.getInstance().doRequest(Method.GET, url, null, null);
				try {
					final GMetaContact contacts = Api.getInstance().gson.fromJson(response.httpResponse.responseBody, GMetaContact.class);
					// we should only have one result, so just return the first one
					return contacts.contacts[0].save().get();
				} catch (final Exception e) {
					throw new ApiException(e);
				}
			}
		};
		final FutureTask<Person> task = new FutureTask<Person>(callable);
		Application.getExecutor().execute(task);
		return task;
	}

	/**
	 * Gets a list of contacts from the api. This is basically the same as getPerson, however questions answers are also
	 * returned.
	 * 
	 * @param personId
	 * @return
	 */
	public static FutureTask<List<Person>> getContacts(final List<Long> personIds) {
		final Callable<List<Person>> callable = new Callable<List<Person>>() {
			@Override
			public List<Person> call() throws Exception {
				final String url = Api.getInstance().buildUrlPath("contacts", U.toCSV(personIds) + ".json");
				final ApiResponse response = Api.getInstance().doRequest(Method.GET, url, null, null);
				try {
					final GMetaContact contacts = Api.getInstance().gson.fromJson(response.httpResponse.responseBody, GMetaContact.class);
					final List<Person> persons = new ArrayList<Person>();
					for (final GContact contact : contacts.contacts) {
						Log.e("HERE", "HERE" + contact.person.name);
						persons.add(contact.save().get());
					}
					return persons;
				} catch (final Exception e) {
					Log.e("Exception", e.getMessage(), e);
					throw new ApiException(e);
				}
			}
		};
		final FutureTask<List<Person>> task = new FutureTask<List<Person>>(callable);
		Application.getExecutor().execute(task);
		return task;
	}

	/**
	 * Gets a single contact from the api based on a query
	 * 
	 * @param options
	 *            the options used to create the search query
	 * @return
	 */
	public static FutureTask<List<Person>> getContactList(final ContactListOptions options) {
		final Callable<List<Person>> callable = new Callable<List<Person>>() {
			@Override
			public List<Person> call() throws Exception {
				final String url = Api.getInstance().buildUrlPath("contacts.json");

				final HttpParams params = new HttpParams();
				options.appendLimits(params);
				options.appendFiltersParams(params);
				options.appendOrderByParam(params);

				final ApiResponse response = Api.getInstance().doRequest(Method.GET, url, null, params);
				try {
					final GMetaContact contacts = Api.getInstance().gson.fromJson(response.httpResponse.responseBody, GMetaContact.class);
					return contacts.save().get();
				} catch (final Exception e) {
					Log.e("Exception", e.getMessage(), e);
					throw new ApiException(e);
				}
			}
		};
		final FutureTask<List<Person>> task = new FutureTask<List<Person>>(callable);
		Application.getExecutor().execute(task);
		return task;
	}

	/**
	 * Options for getContactList
	 */
	public static class ContactListOptions {

		private int start = 0;
		private int limit = 20;
		private boolean atEnd = false;

		private final HashMultimap<String, String> filters = HashMultimap.<String, String> create();
		private final HashMap<String, String> orderBy = new HashMap<String, String>();

		public ContactListOptions() {}

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

		protected HttpParams appendLimits(final HttpParams params) {
			params.put("start", String.valueOf(start));
			params.put("limit", String.valueOf(limit));
			return params;
		}

		public HashMultimap<String, String> getFilters() {
			return filters;
		}

		protected HttpParams appendFiltersParams(final HttpParams params) {
			final Iterator<String> itr = filters.keySet().iterator();
			while (itr.hasNext()) {
				final String filter = itr.next();
				final StringBuffer value = new StringBuffer();
				final Iterator<String> itr2 = filters.get(filter).iterator();
				while (itr2.hasNext()) {
					final String val = itr2.next();
					value.append(stripUnsafeChars(val));
					if (itr2.hasNext()) {
						value.append("|");
					}
				}
				params.put("filters[" + stripUnsafeChars(filter) + "]", value.toString());
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

		protected HttpParams appendOrderByParam(final HttpParams params) {
			final StringBuffer sb = new StringBuffer();
			final Iterator<Entry<String, String>> itr = orderBy.entrySet().iterator();
			while (itr.hasNext()) {
				final Entry<String, String> entry = itr.next();
				sb.append(stripUnsafeChars(entry.getKey()) + "," + stripUnsafeChars(entry.getValue()));
				if (itr.hasNext()) {
					sb.append("|");
				}
			}

			if (!orderBy.isEmpty()) params.put("order_by", sb.toString());

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

		public boolean isAtEnd() {
			return atEnd;
		}

		public void setIsAtEnd(final boolean atEnd) {
			this.atEnd = atEnd;
		}

		public void advanceStart() {
			this.start += this.limit;
		}
	}

	// **********************************//
	// ********** COMMENTS API **********//
	// **********************************//

	/**
	 * Gets the followup comments for a contact
	 * 
	 * @param options
	 *            the options used to create the search query
	 * @return
	 */
	public static FutureTask<List<FollowupComment>> getComments(final long personId) {
		final Callable<List<FollowupComment>> callable = new Callable<List<FollowupComment>>() {
			@Override
			public List<FollowupComment> call() throws Exception {
				final String url = Api.getInstance().buildUrlPath("followup_comments", personId + ".json");
				final ApiResponse response = Api.getInstance().doRequest(Method.GET, url, null, null);
				try {
					final GMetaCommentTop comments = Api.getInstance().gson.fromJson(response.httpResponse.responseBody, GMetaCommentTop.class);

					return comments.save().get();
				} catch (final Exception e) {
					Log.e("Exception", e.getMessage(), e);
					throw new ApiException(e);
				}
			}
		};
		final FutureTask<List<FollowupComment>> task = new FutureTask<List<FollowupComment>>(callable);
		Application.getExecutor().execute(task);
		return task;
	}

	/**
	 * Creates a followup comment for a contact
	 * 
	 * @param personId
	 * @param comment
	 * @param rejoicables
	 * @return
	 */
	public static FutureTask<Boolean> addComment(final long personId, final FollowupComment comment, final List<Rejoicable> rejoicables) {
		final Callable<Boolean> callable = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				final String url = Api.getInstance().buildUrlPath("followup_comments");

				final JSONObject jsonComment = new JSONObject();

				if (comment.getOrganization_id() <= 0) {
					comment.setOrganization_id(Session.getInstance().getOrganizationId());
				}

				if (comment.getCommenter_id() <= 0) {
					comment.setCommenter_id(Session.getInstance().getPerson().getId());
				}

				if (U.isNullEmpty(comment.getStatus())) {
					comment.setStatus("");
				}

				if (U.isNullEmpty(comment.getComment())) {
					comment.setComment("");
				}

				jsonComment.put("organization_id", comment.getOrganization_id());
				jsonComment.put("contact_id", personId);
				jsonComment.put("commenter_id", comment.getCommenter_id());
				jsonComment.put("comment", comment.getComment());
				jsonComment.put("status", comment.getStatus());

				final JSONArray jsonRejoicables = new JSONArray();
				final Iterator<Rejoicable> itr = rejoicables.iterator();
				while (itr.hasNext()) {
					final String what = itr.next().getWhat();
					if (U.isNullEmpty(what)) continue;
					jsonRejoicables.put(what);
				}

				final JSONObject json = new JSONObject();
				json.put("followup_comment", jsonComment);
				json.put("rejoicables", jsonRejoicables);

				final HttpParams params = new HttpParams();
				params.put("json", json.toString());

				Api.getInstance().doRequest(Method.POST, url, null, params);
				return true;
			}
		};
		final FutureTask<Boolean> task = new FutureTask<Boolean>(callable);
		Application.getExecutor().execute(task);
		return task;
	}

	/**
	 * Deletes a followup comment
	 * 
	 * @param commentId
	 * @return
	 */
	public static FutureTask<Boolean> deleteComment(final long commentId) {
		final List<Long> comments = new ArrayList<Long>();
		comments.add(commentId);
		return deleteComments(comments);
	}

	/**
	 * Deletes multiple follow up comments
	 * 
	 * @param commentIds
	 *            a list of comment ids
	 * @return
	 */
	public static FutureTask<Boolean> deleteComments(final List<Long> commentIds) {
		final Callable<Boolean> callable = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				final String url = Api.getInstance().buildUrlPath("followup_comments", U.toCSV(commentIds));

				final HttpParams params = new HttpParams();
				params.put("_method", "delete");

				Api.getInstance().doRequest(Method.POST, url, null, params);
				return true;
			}
		};
		final FutureTask<Boolean> task = new FutureTask<Boolean>(callable);
		Application.getExecutor().execute(task);
		return task;
	}

	// **********************************//
	// *********** GROUPS API ***********//
	// **********************************//

	// TODO: implement groups api calls

	// **********************************//
	// ******** ORGANIZATIONS API *******//
	// **********************************//

	/**
	 * Gets the person associated with the access token
	 * 
	 * @return the requested organization
	 */
	public static FutureTask<Organization> getOrganization(final long organizationId) {
		final Callable<Organization> callable = new Callable<Organization>() {
			@Override
			public Organization call() throws Exception {
				final String url = Api.getInstance().buildUrlPath("organizations", organizationId);
				final ApiResponse response = Api.getInstance().doRequest(Method.GET, url, null, null);
				try {
					final GMetaOrganizations gmo = Api.getInstance().gson.fromJson(response.httpResponse.responseBody, GMetaOrganizations.class);
					return gmo.organizations[0].save().get();
				} catch (final Exception e) {
					throw new ApiException(e);
				}
			}
		};
		final FutureTask<Organization> task = new FutureTask<Organization>(callable);
		Application.getExecutor().execute(task);
		return task;
	}

	/**
	 * Gets a list of people by their ids
	 * 
	 * @param organizationIds
	 *            list of organizations to retrieve. If null, will return all organizations the logged in user has
	 *            access to.
	 * @return list of organizations
	 */
	public static FutureTask<List<Organization>> getOrganizations(final List<Long> organizationIds) {
		final Callable<List<Organization>> callable = new Callable<List<Organization>>() {
			@Override
			public List<Organization> call() throws Exception {
				String url = "";
				if (organizationIds == null) {
					url = Api.getInstance().buildUrlPath("organizations");
				} else {
					url = Api.getInstance().buildUrlPath("organizations", U.toCSV(organizationIds));
				}

				final ApiResponse response = Api.getInstance().doRequest(Method.GET, url, null, null);
				try {
					final GMetaOrganizations gmo = Api.getInstance().gson.fromJson(response.httpResponse.responseBody, GMetaOrganizations.class);
					return gmo.save().get();
				} catch (final Exception e) {
					Log.e("Exception", e.getMessage(), e);
					throw new ApiException(e);
				}
			}
		};
		final FutureTask<List<Organization>> task = new FutureTask<List<Organization>>(callable);
		Application.getExecutor().execute(task);
		return task;
	}

	// **********************************//
	// *********** ROLES API ************//
	// **********************************//

	/**
	 * Change a person's role
	 * 
	 * @param personId
	 * @param role
	 * @param return
	 */
	public static FutureTask<Boolean> changeRole(final long personId, final String role) {
		final Callable<Boolean> callable = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				final String url = Api.getInstance().buildUrlPath("roles", personId + ".json");

				final HttpParams params = new HttpParams();
				params.put("_method", "put");
				params.put("role", role);

				Api.getInstance().doRequest(Method.POST, url, null, params);
				return true;
			}
		};
		final FutureTask<Boolean> task = new FutureTask<Boolean>(callable);
		Application.getExecutor().execute(task);
		return task;
	}

	// **********************************//
	// *********** SURVEY API ***********//
	// **********************************//

	/**
	 * Gets the url for the survey webview
	 * 
	 * @return
	 * @throws OperationCanceledException
	 * @throws AuthenticatorException
	 * @throws NoAccountException
	 * @throws IOException
	 */
	public static FutureTask<String> getSurveyUrl() {
		final Callable<String> callable = new Callable<String>() {
			@Override
			public String call() throws Exception {
				Session.getInstance().getAccessToken();
				Session.getInstance().getOrganizationId();

				final HttpParams params = new HttpParams();
				params.put("access_token", Session.getInstance().getAccessToken());
				params.put("org_id", Session.getInstance().getOrganizationId());
				params.put("mobile", "1");
				Api.getInstance().appendLoggingParams(params);

				return Configuration.getSurveyUrl() + "?" + params.getParamString();
			}
		};
		final FutureTask<String> task = new FutureTask<String>(callable);
		Application.getExecutor().execute(task);
		return task;
	}

	// **********************************//
	// *********** OAUTH API ************//
	// **********************************//

	/**
	 * Gets a user's access token from an authorization code
	 * 
	 * @param code
	 * @return
	 */
	public static FutureTask<GAuthTokenDone> getAccessToken(final String code) {
		final Callable<GAuthTokenDone> callable = new Callable<GAuthTokenDone>() {
			@Override
			public GAuthTokenDone call() throws Exception {
				final HttpParams params = new HttpParams();
				params.put("client_id", Configuration.getOauthClientId());
				params.put("client_secret", Configuration.getOauthClientSecret());
				params.put("code", code);
				params.put("grant_type", "authorization_code");
				params.put("scope", Configuration.getOauthScope());
				params.put("redirect_uri", Configuration.getOauthUrl() + "/done.json");

				final ApiResponse response = Api.getInstance().doRequest(Method.POST, Configuration.getOauthUrl() + "/access_token", null, params, false, -1);
				try {
					final GAuthTokenDone done = Api.getInstance().gson.fromJson(response.httpResponse.responseBody, GAuthTokenDone.class);
					return done;
				} catch (final Exception e) {
					throw new ApiException(e);
				}
			}
		};
		final FutureTask<GAuthTokenDone> task = new FutureTask<GAuthTokenDone>(callable);
		Application.getExecutor().execute(task);
		return task;
	}
}