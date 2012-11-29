package com.missionhub.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Build;
import ch.boye.httpclientandroidlib.client.utils.URIBuilder;

import com.google.gson.Gson;
import com.missionhub.application.Application;
import com.missionhub.application.Configuration;
import com.missionhub.application.Session;
import com.missionhub.application.Session.NoAccountException;
import com.missionhub.model.FollowupComment;
import com.missionhub.model.Organization;
import com.missionhub.model.Person;
import com.missionhub.model.gson.GAuthTokenDone;
import com.missionhub.model.gson.GContact;
import com.missionhub.model.gson.GMetaCommentTop;
import com.missionhub.model.gson.GMetaContact;
import com.missionhub.model.gson.GMetaOrganizations;
import com.missionhub.model.gson.GMetaPeople;
import com.missionhub.model.gson.GPerson;
import com.missionhub.network.HttpClient;
import com.missionhub.network.HttpClient.HttpClientFuture;
import com.missionhub.network.HttpClient.HttpMethod;
import com.missionhub.network.HttpClient.ResponseType;
import com.missionhub.network.HttpHeaders;
import com.missionhub.network.HttpParams;
import com.missionhub.network.HttpResponse;
import com.missionhub.util.U;

/**
 * The Android MissionHub API Client.
 */
public class Api {

	/** the singleton api object */
	private static Api sApi;

	/** the logging tag */
	public static final String TAG = Api.class.getSimpleName();

	/** the gson parser */
	private final Gson gson = new Gson();

	private Api() {}

	/**
	 * Returns the instance of the API.
	 * 
	 * @return instance of the api
	 */
	public synchronized static Api getInstance() {
		if (sApi == null) {
			sApi = new Api();
		}
		return sApi;
	}

	private HttpResponse doRequest(final HttpMethod method, final String url) throws Exception {
		return doRequest(method, url, null, null, true, ResponseType.STRING);
	}

	private HttpResponse doRequest(final HttpMethod method, final String url, final HttpParams params) throws Exception {
		return doRequest(method, url, null, params, true, ResponseType.STRING);
	}

	private HttpResponse doRequest(final HttpMethod method, final String url, final HttpHeaders headers, final HttpParams params) throws Exception {
		return doRequest(method, url, headers, params, true, ResponseType.STRING);
	}

	private HttpResponse doRequest(final HttpMethod method, final String url, final HttpHeaders headers, final HttpParams params, final boolean authenticated) throws Exception {
		return doRequest(method, url, headers, params, authenticated, ResponseType.STRING);
	}

	private HttpResponse doRequest(final HttpMethod method, final String url, HttpHeaders headers, final HttpParams params, final boolean authenticated, final ResponseType responseType)
			throws Exception {
		HttpResponse response = null;
		try {
			// create the headers object if needed and add the api version header
			if (headers == null) {
				headers = new HttpHeaders();
			}
			if (headers.getHeaders("Accept").size() <= 0) {
				headers.setHeader("Accept", "application/vnd.missionhub-v" + Configuration.getApiVersion() + "+json");
			}

			// add oauth token to the request if needed
			if (authenticated) {
				headers.setHeader("Authorization", "OAuth: " + Session.getInstance().getAccessToken());
			}

			appendLoggingParams(params);

			// create the client and get the response
			final HttpClient client = new HttpClient();
			client.setResponseType(responseType);
			final HttpClientFuture future = client.doRequest(method, url, headers, params);
			response = future.get();

			throwApiException(response);

			return response;
		} catch (final Exception e) {
			if (e instanceof ApiException) {
				throw e;
			}

			// check for authentication errors
			if (e instanceof IOException) {
				if ((response != null && response.statusCode == 401) || e.getMessage().contains("authentication")) {
					Session.getInstance().reportInvalidAccessToken();
					throw new AccessTokenException(e);
				}
			}

			// check for api errors if the response was a string
			throwApiException(response);

			// throw the original exception
			throw e;
		}
	}

	private void throwApiException(final HttpResponse response) throws ApiException {
		if (response != null && !U.isNullEmpty(response.responseBody)) {
			ApiErrorGson error = null;
			try {
				error = gson.fromJson(response.responseBody, ApiErrorGson.class);
			} catch (final Exception e) { /* ignore */}
			if (error != null && error.error != null) {
				if (error.error.code.equalsIgnoreCase("56")) {
					Session.getInstance().reportInvalidAccessToken();
					throw new AccessTokenException(error);
				} else {
					throw new ApiException(error);
				}
			}
		}
	}

	/**
	 * Appends parameters for logging
	 * 
	 * @param params
	 */
	private void appendLoggingParams(final HttpParams params) {
		try {
			params.add("platform", "android");
			params.add("platform_product", Build.PRODUCT);
			params.add("platform_release", android.os.Build.VERSION.RELEASE);
			params.add("app", Application.getVersionCode());
		} catch (final Exception ignore) { /* ignore */}
	}

	/**
	 * Appends parameters for logging
	 * 
	 * @param builder
	 */
	private void appendLoggingParams(final URIBuilder builder) {
		try {
			builder.addParameter("platform", "android");
			builder.addParameter("platform_product", Build.PRODUCT);
			builder.addParameter("platform_release", android.os.Build.VERSION.RELEASE);
			builder.addParameter("app", String.valueOf(Application.getVersionCode()));
		} catch (final Exception ignore) { /* ignore */}
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
	public static FutureTask<Person> getPerson(final long personId) {
		final Callable<Person> callable = new Callable<Person>() {
			@Override
			public Person call() throws Exception {
				final String url = Api.getInstance().buildUrlPath("people", personId + ".json");
				final HttpResponse response = Api.getInstance().doRequest(HttpMethod.GET, url);
				try {
					final GMetaPeople gmp = Api.getInstance().gson.fromJson(response.responseBody, GMetaPeople.class);
					final Person p = gmp.people[0].save(false);
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
				final HttpResponse response = Api.getInstance().doRequest(HttpMethod.GET, url);
				try {
					final GMetaPeople gmp = Api.getInstance().gson.fromJson(response.responseBody, GMetaPeople.class);
					final Person p = gmp.people[0].save(false);
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
				final HttpResponse response = Api.getInstance().doRequest(HttpMethod.GET, url);
				try {
					final GMetaPeople gmp = Api.getInstance().gson.fromJson(response.responseBody, GMetaPeople.class);
					final List<Person> persons = new ArrayList<Person>();
					for (final GPerson gperson : gmp.people) {
						persons.add(gperson.save(false));
					}
					return persons;
				} catch (final Exception e) {
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
				params.add("ids", U.toCSV(personIds));
				// params.add("type", type.name());
				params.add("assign_to", U.toCSV(toIds));

				final HttpHeaders headers = new HttpHeaders();
				headers.setHeader("Accept", "application/vnd.missionhub-v1+json");

				Api.getInstance().doRequest(HttpMethod.POST, url, headers, params);
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
				for (final Long id : personIds) {
					final String url = Api.getInstance().buildUrlPath("contact_assignments", id + ".json");

					final HttpParams params = new HttpParams();
					params.add("ids", id);
					params.add("_method", "delete");

					Api.getInstance().doRequest(HttpMethod.POST, url, params);
				}
				return true;
				//
				// final String url = Api.getInstance().buildUrlPath("contact_assignments", U.toCSV(personIds) +
				// ".json");
				//
				// final HttpParams params = new HttpParams();
				// params.add("ids", U.toCSV(personIds));
				// params.add("_method", "delete");
				//
				// Api.getInstance().doRequest(HttpMethod.POST, url, params);
				// return true;
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
				final HttpResponse response = Api.getInstance().doRequest(HttpMethod.GET, url);
				try {
					final GMetaContact contacts = Api.getInstance().gson.fromJson(response.responseBody, GMetaContact.class);
					// we should only have one result, so just return the first one
					return contacts.contacts[0].save(false);
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
				final HttpResponse response = Api.getInstance().doRequest(HttpMethod.GET, url);
				try {
					final GMetaContact contacts = Api.getInstance().gson.fromJson(response.responseBody, GMetaContact.class);
					final List<Person> persons = new ArrayList<Person>();
					for (final GContact contact : contacts.contacts) {
						persons.add(contact.save(false));
					}
					return persons;
				} catch (final Exception e) {
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
	public static FutureTask<List<Person>> getContactList(final ApiContactListOptions options) {
		final Callable<List<Person>> callable = new Callable<List<Person>>() {
			@Override
			public List<Person> call() throws Exception {
				final String url = Api.getInstance().buildUrlPath("contacts.json");

				final HttpParams params = new HttpParams();
				options.appendParams(params);

				final HttpResponse response = Api.getInstance().doRequest(HttpMethod.GET, url, params);
				try {
					final GMetaContact contacts = Api.getInstance().gson.fromJson(response.responseBody, GMetaContact.class);
					final List<Person> people = contacts.save(false);
					return people;
				} catch (final Exception e) {
					throw new ApiException(e);
				}
			}
		};
		final FutureTask<List<Person>> task = new FutureTask<List<Person>>(callable);
		Application.getExecutor().execute(task);
		return task;
	}

	public static FutureTask<Person> createContact(final ApiContact contact) {
		final Callable<Person> callable = new Callable<Person>() {
			@Override
			public Person call() throws Exception {
				final String url = Api.getInstance().buildUrlPath("contacts.json");

				final HttpParams params = new HttpParams();
				contact.appendParams(params);

				final HttpResponse response = Api.getInstance().doRequest(HttpMethod.POST, url, params);
				try {
					final GPerson person = Api.getInstance().gson.fromJson(response.responseBody, GPerson.class);
					return person.save(false);
				} catch (final Exception e) {
					throw new ApiException(e);
				}
			}
		};
		final FutureTask<Person> task = new FutureTask<Person>(callable);
		Application.getExecutor().execute(task);
		return task;
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
				final HttpResponse response = Api.getInstance().doRequest(HttpMethod.GET, url);
				try {
					final GMetaCommentTop comments = Api.getInstance().gson.fromJson(response.responseBody, GMetaCommentTop.class);

					return comments.save(false);
				} catch (final Exception e) {
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
	public static FutureTask<Boolean> addComment(final JsonComment comment) {
		final Callable<Boolean> callable = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				final String url = Api.getInstance().buildUrlPath("followup_comments");

				final JSONObject jsonComment = new JSONObject();

				if (comment.organizationId < 0) {
					comment.organizationId = Session.getInstance().getOrganizationId();
				}

				if (comment.commenterId < 0) {
					comment.commenterId = Session.getInstance().getPerson().getId();
				}

				if (U.isNullEmpty(comment.status)) {
					comment.status = "";
				}

				if (U.isNullEmpty(comment.comment)) {
					comment.comment = "";
				}

				jsonComment.put("organization_id", comment.organizationId);
				jsonComment.put("contact_id", comment.personId);
				jsonComment.put("commenter_id", comment.commenterId);
				jsonComment.put("comment", comment.comment);
				jsonComment.put("status", comment.status);

				final JSONArray jsonRejoicables = new JSONArray();
				if (comment.rejoicables != null) {
					for (final String rejoicable : comment.rejoicables) {
						if (U.isNullEmpty(rejoicable)) continue;
						jsonRejoicables.put(rejoicable);
					}
				}

				final JSONObject json = new JSONObject();
				json.put("followup_comment", jsonComment);
				json.put("rejoicables", jsonRejoicables);

				final HttpParams params = new HttpParams();
				params.add("json", json.toString());

				Api.getInstance().doRequest(HttpMethod.POST, url, null, params);
				return true;
			}
		};
		final FutureTask<Boolean> task = new FutureTask<Boolean>(callable);
		Application.getExecutor().execute(task);
		return task;
	}

	public static class JsonComment {

		public long personId = -1;
		public long commenterId = -1;
		public long organizationId = -1;
		public String comment;
		public String status;
		public List<String> rejoicables;

		public JsonComment(final long personId, final String comment, final String status, final List<String> rejoicables) {
			this.personId = personId;
			this.comment = comment;
			this.status = status;
			this.rejoicables = rejoicables;
		}

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
				params.add("_method", "delete");

				Api.getInstance().doRequest(HttpMethod.POST, url, params);
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
				final HttpResponse response = Api.getInstance().doRequest(HttpMethod.GET, url);
				try {
					final GMetaOrganizations gmo = Api.getInstance().gson.fromJson(response.responseBody, GMetaOrganizations.class);
					return gmo.organizations[0].save(false);
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

				final HttpResponse response = Api.getInstance().doRequest(HttpMethod.GET, url);
				try {
					final GMetaOrganizations gmo = Api.getInstance().gson.fromJson(response.responseBody, GMetaOrganizations.class);
					return gmo.save(false);
				} catch (final Exception e) {
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
				params.add("_method", "put");
				params.add("role", role);

				Api.getInstance().doRequest(HttpMethod.POST, url, params);
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

				final URIBuilder builder = new URIBuilder(Configuration.getSurveyUrl());
				builder.addParameter("access_token", Session.getInstance().getAccessToken());
				builder.addParameter("org_id", String.valueOf(Session.getInstance().getOrganizationId()));
				builder.addParameter("mobile", "1");

				Api.getInstance().appendLoggingParams(builder);

				return builder.build().toURL().toString();
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
				params.add("client_id", Configuration.getOauthClientId());
				params.add("client_secret", Configuration.getOauthClientSecret());
				params.add("code", code);
				params.add("grant_type", "authorization_code");
				params.add("scope", Configuration.getOauthScope());
				params.add("redirect_uri", Configuration.getOauthUrl() + "/done.json");

				final HttpResponse response = Api.getInstance().doRequest(HttpMethod.POST, Configuration.getOauthUrl() + "/access_token", null, params, false);
				try {
					final GAuthTokenDone done = Api.getInstance().gson.fromJson(response.responseBody, GAuthTokenDone.class);
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