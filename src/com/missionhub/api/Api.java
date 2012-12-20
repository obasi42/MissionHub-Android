package com.missionhub.api;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

import android.os.Build;
import android.util.Log;
import ch.boye.httpclientandroidlib.client.utils.URIBuilder;

import com.google.gson.Gson;
import com.missionhub.application.Application;
import com.missionhub.application.Configuration;
import com.missionhub.application.Session;
import com.missionhub.model.ContactAssignment;
import com.missionhub.model.ContactAssignmentDao;
import com.missionhub.model.FollowupComment;
import com.missionhub.model.Organization;
import com.missionhub.model.Person;
import com.missionhub.model.Role;
import com.missionhub.model.gson.GAccessToken;
import com.missionhub.model.gson.GContactAssignment;
import com.missionhub.model.gson.GContactAssignments;
import com.missionhub.model.gson.GErrors;
import com.missionhub.model.gson.GErrorsDepreciated;
import com.missionhub.model.gson.GFollowupComment;
import com.missionhub.model.gson.GOrganization;
import com.missionhub.model.gson.GOrganizations;
import com.missionhub.model.gson.GPeople;
import com.missionhub.model.gson.GPerson;
import com.missionhub.model.gson.GRole;
import com.missionhub.model.gson.GRoles;
import com.missionhub.network.HttpClient;
import com.missionhub.network.HttpClient.HttpClientFuture;
import com.missionhub.network.HttpClient.HttpMethod;
import com.missionhub.network.HttpClient.ResponseType;
import com.missionhub.network.HttpHeaders;
import com.missionhub.network.HttpParams;
import com.missionhub.network.HttpResponse;
import com.missionhub.util.U;

public class Api {

	/** the singleton api object */
	private static Api sApi;

	/** the singleton gson parser */
	protected static final Gson sGson = new Gson();

	/** the current api call id */
	protected static final AtomicLong sCallId = new AtomicLong();

	/** the active calls */
	private static final Map<Object, ApiCall<?>> sCalls = Collections.synchronizedMap(new WeakHashMap<Object, ApiCall<?>>());

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

	protected synchronized void registerCall(final ApiCall<?> call) {
		sCalls.put(call.getId(), call);
	}

	protected synchronized void unregisterCall(final Object callId) {
		sCalls.remove(callId);
	}

	/**
	 * Returns the call by the given id. Expect a null value as the call is immediately unregistered on cancellation or
	 * completion.
	 * 
	 * @param callId
	 * @return
	 */
	public synchronized static ApiCall<?> getCallById(final Object callId) {
		return sCalls.get(callId);
	}

	/**
	 * Cancels a running call by id
	 * 
	 * @param callId
	 * @param mayInterruptIfRunning
	 * @return
	 */
	public synchronized static boolean cancelCallById(final Object callId, final boolean mayInterruptIfRunning) {
		final ApiCall<?> call = sCalls.get(callId);
		if (call != null) {
			return call.cancel(mayInterruptIfRunning);
		}
		return false;
	}

	/* People */

	public static ApiCall<Person> getPerson(final long personId) {
		return getPerson(personId, null);
	}

	public static ApiCall<Person> getPerson(final long personId, final ApiOptions options) {
		return new ApiCall<Person>(ApiOptions.builder().method(HttpMethod.GET).url(buildUrl("people", personId)).responseParser(personParser).merge(options).build());
	}

	public static ApiCall<Person> getPersonMe() {
		return getPersonMe(null);
	}

	public static ApiCall<Person> getPersonMe(final ApiOptions options) {
		return new ApiCall<Person>(ApiOptions.builder().method(HttpMethod.GET).url(buildUrl("people", "me")).responseParser(personParser).merge(options).build());
	}

	public static ApiCall<Person> createPerson(final GPerson person) {
		return createPerson(person, null);
	}

	public static ApiCall<Person> createPerson(final GPerson person, final ApiOptions options) {
		final HttpParams params = new HttpParams();
		person.toParams(params);
		return new ApiCall<Person>(ApiOptions.builder().method(HttpMethod.POST).url(buildUrl("people")).responseParser(personParser).params(params).merge(options).build());
	}

	public static ApiCall<Person> updatePerson(final GPerson person) {
		return updatePerson(person, null);
	}

	public static ApiCall<Person> updatePerson(final GPerson person, final ApiOptions options) {
		final HttpParams params = new HttpParams();
		person.toParams(params);
		return new ApiCall<Person>(ApiOptions.builder().method(HttpMethod.PUT).url(buildUrl("people", person.id)).responseParser(personParser).params(params).merge(options).build());
	}

	public static ApiCall<Void> deletePerson(final long personId) {
		return deletePerson(personId, null);
	}

	public static ApiCall<Void> deletePerson(final long personId, final ApiOptions options) {
		return new ApiCall<Void>(ApiOptions.builder().method(HttpMethod.DELETE).url(buildUrl("people", personId)).responseParser(new ApiResponseParser<Void>() {
			@Override
			public Void parseResponse(final HttpResponse response) throws Exception {
				final Person person = Application.getDb().getPersonDao().load(personId);
				if (person != null) {
					person.deleteWithRelations();
				}
				return null;
			}
		}).merge(options).build());
	}

	public static ApiCall<List<Person>> listPeople(final ListOptions listOptions) {
		return listPeople(listOptions, null);
	}

	public static ApiCall<List<Person>> listPeople(final ListOptions listOptions, final ApiOptions options) {
		final HttpParams params = new HttpParams();
		listOptions.toParams(params);
		return new ApiCall<List<Person>>(ApiOptions.builder().method(HttpMethod.GET).url(buildUrl("people")).responseParser(peopleParser).params(params).merge(options).build());
	}

	/* Roles (Labels) */
	public static ApiCall<Role> getRole(final long roleId) {
		return getRole(roleId, null);
	}

	public static ApiCall<Role> getRole(final long roleId, final ApiOptions options) {
		return new ApiCall<Role>(ApiOptions.builder().method(HttpMethod.GET).url(buildUrl("roles", roleId)).responseParser(roleParser).merge(options).build());
	}

	public static ApiCall<Role> createRole(final GRole person) {
		return createRole(person, null);
	}

	public static ApiCall<Role> createRole(final GRole role, final ApiOptions options) {
		final HttpParams params = new HttpParams();
		role.toParams(params);
		return new ApiCall<Role>(ApiOptions.builder().method(HttpMethod.POST).url(buildUrl("roles")).responseParser(roleParser).params(params).merge(options).build());
	}

	public static ApiCall<Role> updateRole(final GRole role) {
		return updateRole(role, null);
	}

	public static ApiCall<Role> updateRole(final GRole role, final ApiOptions options) {
		final HttpParams params = new HttpParams();
		role.toParams(params);
		return new ApiCall<Role>(ApiOptions.builder().method(HttpMethod.PUT).url(buildUrl("roles", role.id)).responseParser(roleParser).params(params).merge(options).build());
	}

	public static ApiCall<Void> deleteRole(final long roleId) {
		return deleteRole(roleId, null);
	}

	public static ApiCall<Void> deleteRole(final long roleId, final ApiOptions options) {
		return new ApiCall<Void>(ApiOptions.builder().method(HttpMethod.DELETE).url(buildUrl("roles", roleId)).responseParser(new ApiResponseParser<Void>() {
			@Override
			public Void parseResponse(final HttpResponse response) throws Exception {
				final Role role = Application.getDb().getRoleDao().load(roleId);
				if (role != null) {
					role.deleteWithRelations();
				}
				return null;
			}
		}).merge(options).build());
	}

	public static ApiCall<List<Role>> listRoles() {
		return listRoles();
	}

	public static ApiCall<List<Role>> listRoles(final ApiOptions options, final ListOptions listOptions) {
		final HttpParams params = new HttpParams();
		if (listOptions != null) {
			listOptions.toParams(params);
		}
		return new ApiCall<List<Role>>(ApiOptions.builder().method(HttpMethod.GET).url(buildUrl("roles")).responseParser(rolesParser).params(params).merge(options).build());
	}

	/* Contact Assignments */

	public static ApiCall<List<ContactAssignment>> bulkUpdateContactAssignments(final Collection<GContactAssignment> assignments) {
		return bulkUpdateContactAssignments(assignments, null);
	}

	public static ApiCall<List<ContactAssignment>> bulkUpdateContactAssignments(final Collection<GContactAssignment> assignments, final ApiOptions options) {
		final HttpParams params = new HttpParams();
		addContactAssignmentParams(params, assignments);
		return new ApiCall<List<ContactAssignment>>(ApiOptions.builder().method(HttpMethod.POST).url(buildUrl("contact_assignments", "bulk_update")).responseParser(contactAssignmentsParser)
				.params(params).merge(options).build());
	}

	public static ApiCall<Void> bulkDeleteContactAssignments(final Collection<Long> assignmentIds) {
		return bulkDeleteContactAssignments(assignmentIds, null);
	}

	public static ApiCall<Void> bulkDeleteContactAssignments(final Collection<Long> assignmentIds, final ApiOptions options) {
		final HttpParams params = new HttpParams();
		params.add("filters[ids]", U.toCSV(assignmentIds));
		return new ApiCall<Void>(ApiOptions.builder().method(HttpMethod.DELETE).url(buildUrl("contact_assignments", "bulk_destroy")).responseParser(new ApiResponseParser<Void>() {
			@Override
			public Void parseResponse(final HttpResponse response) throws Exception {
				Application.getDb().getContactAssignmentDao().queryBuilder().where(ContactAssignmentDao.Properties.Id.in(assignmentIds)).buildDelete().executeDelete();
				return null;
			}
		}).params(params).merge(options).build());
	}

	public static ApiCall<List<ContactAssignment>> listContactAssignments(final ListOptions listOptions) {
		return listContactAssignments(listOptions, null);
	}

	public static ApiCall<List<ContactAssignment>> listContactAssignments(final ListOptions listOptions, final ApiOptions options) {
		final HttpParams params = new HttpParams();
		if (listOptions != null) {
			listOptions.toParams(params);
		}
		return new ApiCall<List<ContactAssignment>>(ApiOptions.builder().method(HttpMethod.GET).url(buildUrl("contact_assignments")).responseParser(contactAssignmentsParser).params(params)
				.merge(options).build());
	}

	private static void addContactAssignmentParams(final HttpParams params, final Collection<GContactAssignment> assignments) {
		final Iterator<GContactAssignment> itr = assignments.iterator();
		int i = 0;
		while (itr.hasNext()) {
			final GContactAssignment assignment = itr.next();
			if (assignment == null) continue;

			if (assignment.id > 0) {
				params.add("contact_assignments[" + i + "][id]", assignment.id);
			}
			if (assignment.assigned_to_id > 0) {
				params.add("contact_assignments[" + i + "][assigned_to_id]", assignment.assigned_to_id);
			}
			if (assignment.person_id > 0) {
				params.add("contact_assignments[" + i + "][person_id]", assignment.person_id);
			}
			if (assignment.organization_id > 0) {
				params.add("contact_assignments[" + i + "][organization_id]", assignment.organization_id);
			}
			i++;
		}
	}

	/* Followup Comments */
	public static ApiCall<FollowupComment> getFollowupComment(final long commentId) {
		return getFollowupComment(commentId, null);
	}

	public static ApiCall<FollowupComment> getFollowupComment(final long commentId, final ApiOptions options) {
		return new ApiCall<FollowupComment>(ApiOptions.builder().method(HttpMethod.GET).url(buildUrl("followup_comments", commentId)).responseParser(commentParser).merge(options).build());
	}

	public static ApiCall<FollowupComment> createFollowupComment(final GFollowupComment comment) {
		return createFollowupComment(comment, null);
	}

	public static ApiCall<FollowupComment> createFollowupComment(final GFollowupComment comment, final ApiOptions options) {
		final HttpParams params = new HttpParams();
		comment.toParams(params);
		return new ApiCall<FollowupComment>(ApiOptions.builder().method(HttpMethod.POST).url(buildUrl("followup_comments")).responseParser(commentParser).params(params).merge(options).build());
	}

	public static ApiCall<FollowupComment> updateFollowupComment(final GFollowupComment comment) {
		return updateFollowupComment(comment, null);
	}

	public static ApiCall<FollowupComment> updateFollowupComment(final GFollowupComment comment, final ApiOptions options) {
		final HttpParams params = new HttpParams();
		comment.toParams(params);
		return new ApiCall<FollowupComment>(ApiOptions.builder().method(HttpMethod.PUT).url(buildUrl("followup_comments", comment.id)).responseParser(commentParser).params(params).merge(options)
				.build());
	}

	public static ApiCall<Void> deleteFollowupComment(final long commentId) {
		return deleteFollowupComment(commentId, null);
	}

	public static ApiCall<Void> deleteFollowupComment(final long commentId, final ApiOptions options) {
		return new ApiCall<Void>(ApiOptions.builder().method(HttpMethod.DELETE).url(buildUrl("followup_comments", commentId)).responseParser(new ApiResponseParser<Void>() {
			@Override
			public Void parseResponse(final HttpResponse response) throws Exception {
				final FollowupComment comment = Application.getDb().getFollowupCommentDao().load(commentId);
				if (comment != null) {

					comment.deleteWithRelations();
				}
				return null;
			}
		}).merge(options).build());
	}

	/* Organizations */
	public static ApiCall<Organization> getOrganization(final long organizationId) {
		return getOrganization(organizationId, null);
	}

	public static ApiCall<Organization> getOrganization(final long organizationId, final ApiOptions options) {
		return new ApiCall<Organization>(ApiOptions.builder().method(HttpMethod.GET).url(buildUrl("organizations", organizationId)).responseParser(organizationParser).merge(options).build());
	}

	public static ApiCall<GOrganization> createOrganization(final GOrganization organization) {
		return createOrganization(organization, null);
	}

	public static ApiCall<GOrganization> createOrganization(final GOrganization organization, final ApiOptions options) {
		// TODO: implement createOrganization
		throw new RuntimeException("Unimplemented Method");
	}

	public static ApiCall<GOrganization> updateOrganization(final GOrganization organization) {
		return updateOrganization(organization, null);
	}

	public static ApiCall<GOrganization> updateOrganization(final GOrganization organization, final ApiOptions options) {
		// TODO: implement updateOrganization
		throw new RuntimeException("Unimplemented Method");
	}

	public static ApiCall<Void> deleteOrganization(final long organizationId) {
		return deleteOrganization(organizationId, null);
	}

	public static ApiCall<Void> deleteOrganization(final long organizationId, final ApiOptions options) {
		// TODO: implement deleteOrganization
		throw new RuntimeException("Unimplemented Method");
	}

	public static ApiCall<List<Organization>> listOrganizations(final ApiOptions options) {
		return listOrganizations(null, options);
	}

	public static ApiCall<List<Organization>> listOrganizations(final ListOptions listOptions) {
		return listOrganizations(listOptions, null);
	}

	public static ApiCall<List<Organization>> listOrganizations(final ListOptions listOptions, final ApiOptions options) {
		final HttpParams params = new HttpParams();
		if (listOptions != null) {
			listOptions.toParams(params);
		}
		return new ApiCall<List<Organization>>(ApiOptions.builder().method(HttpMethod.GET).url(buildUrl("organizations")).responseParser(organizationsParser).params(params).merge(options).build());
	}

	/* Surveys */
	public static ApiCall<String> getSurveyUrl() {
		return new ApiCall<String>(ApiOptions.builder().build()) {
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
	}

	/* OAuth */
	/**
	 * Returns the access token from a grant code
	 * 
	 * @param code
	 * @return
	 */
	public static ApiCall<GAccessToken> getAccessToken(final String code) {
		final HttpParams params = new HttpParams();
		params.add("client_id", Configuration.getOauthClientId());
		params.add("client_secret", Configuration.getOauthClientSecret());
		params.add("code", code);
		params.add("grant_type", "authorization_code");
		params.add("scope", Configuration.getOauthScope());
		params.add("redirect_uri", Configuration.getOauthUrl() + "/done.json");

		return new ApiCall<GAccessToken>(ApiOptions.builder().method(HttpMethod.POST).url(Configuration.getOauthUrl() + "/access_token").params(params).authenticated(false)
				.responseParser(new ApiResponseParser<GAccessToken>() {
					@Override
					public GAccessToken parseResponse(final HttpResponse response) throws Exception {
						return sGson.fromJson(response.responseBody, GAccessToken.class);
					}
				}).build());
	}

	/* Generic Response Parsers */
	private static final ApiResponseParser<Person> personParser = new ApiResponseParser<Person>() {
		@Override
		public Person parseResponse(final HttpResponse response) throws Exception {
			final GPeople people = sGson.fromJson(response.responseBody, GPeople.class);
			return people.person.save(false);
		}
	};

	private static final ApiResponseParser<List<Person>> peopleParser = new ApiResponseParser<List<Person>>() {
		@Override
		public List<Person> parseResponse(final HttpResponse response) throws Exception {
			final GPeople people = sGson.fromJson(response.responseBody, GPeople.class);
			return people.save(false);
		}
	};

	private static final ApiResponseParser<Role> roleParser = new ApiResponseParser<Role>() {
		@Override
		public Role parseResponse(final HttpResponse response) throws Exception {
			final GRole role = sGson.fromJson(response.responseBody, GRole.class);
			return role.save(false);
		}
	};

	private static final ApiResponseParser<List<Role>> rolesParser = new ApiResponseParser<List<Role>>() {
		@Override
		public List<Role> parseResponse(final HttpResponse response) throws Exception {
			final GRoles roles = sGson.fromJson(response.responseBody, GRoles.class);
			return roles.save(false);
		}
	};

	private static final ApiResponseParser<List<ContactAssignment>> contactAssignmentsParser = new ApiResponseParser<List<ContactAssignment>>() {
		@Override
		public List<ContactAssignment> parseResponse(final HttpResponse response) throws Exception {
			final GContactAssignments assignments = sGson.fromJson(response.responseBody, GContactAssignments.class);
			return assignments.save(false);
		}
	};

	private static final ApiResponseParser<FollowupComment> commentParser = new ApiResponseParser<FollowupComment>() {
		@Override
		public FollowupComment parseResponse(final HttpResponse response) throws Exception {
			final GFollowupComment comment = sGson.fromJson(response.responseBody, GFollowupComment.class);
			return comment.save(false);
		}
	};

	private static final ApiResponseParser<Organization> organizationParser = new ApiResponseParser<Organization>() {
		@Override
		public Organization parseResponse(final HttpResponse response) throws Exception {
			final GOrganization org = sGson.fromJson(response.responseBody, GOrganization.class);
			return org.save(false);
		}
	};

	private static final ApiResponseParser<List<Organization>> organizationsParser = new ApiResponseParser<List<Organization>>() {
		@Override
		public List<Organization> parseResponse(final HttpResponse response) throws Exception {
			final GOrganizations orgs = sGson.fromJson(response.responseBody, GOrganizations.class);
			return orgs.save(false);
		}
	};

	public static interface ApiResponseParser<T> {

		public T parseResponse(HttpResponse response) throws Exception;

	}

	/* Helpers */
	/**
	 * Builds an api url from parts
	 * 
	 * @param parts
	 * @return
	 */
	private static String buildUrl(final Object... parts) {
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

	/**
	 * Makes a request against the api
	 * 
	 * @param method
	 *            the HttpMethod to use
	 * @param url
	 *            the url
	 * @param headers
	 *            the http headers
	 * @param params
	 *            the url params
	 * @param authenticated
	 *            true if the access token header should be added
	 * @param responseType
	 *            the ResponseType
	 * @return the http response object
	 * @throws Exception
	 */
	protected HttpResponse doRequest(final HttpMethod method, final String url, HttpHeaders headers, HttpParams params, final boolean authenticated, final ResponseType responseType) throws Exception {
		HttpResponse response = null;
		try {
			// create the headers object if null
			if (headers == null) {
				headers = new HttpHeaders();
			}

			// add the api version header
			if (headers.getHeaders("API-VERSION").size() <= 0) {
				headers.setHeader("API-VERSION", Configuration.getApiVersion());
			}

			// create the params object if null
			if (params == null) {
				params = new HttpParams();
			}

			// add oauth token to the request if needed
			if (authenticated) {
				headers.setHeader("Authorization", "Bearer " + Session.getInstance().getAccessToken());

				if (Session.getInstance().getOrganizationId() >= 0) {
					params.add("organization_id", Session.getInstance().getOrganizationId());
				}
			}

			appendLoggingParams(params);

			Log.e("API", method.name() + ": " + url);
			Log.e("API", headers.toString());
			Log.e("API", params.toString());

			// create the client and get the response
			final HttpClient client = new HttpClient();
			client.setResponseType(responseType);
			final HttpClientFuture future = client.doRequest(method, url, headers, params);
			response = future.get();
			maybeThrowException(response);

			Log.d("API", response.responseBody);

			return response;
		} catch (final Exception e) {
			// we just threw this.. no need to process it again
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
			maybeThrowException(response);

			// throw the original exception
			throw e;
		}
	}

	/**
	 * Parses the response for a json error
	 * 
	 * @param response
	 * @throws ApiException
	 */
	private void maybeThrowException(final HttpResponse response) throws ApiException {
		if (response != null && !U.isNullEmpty(response.responseBody)) {
			ApiException exception = null;
			try {
				final GErrors errors = sGson.fromJson(response.responseBody, GErrors.class);
				if (errors.errors == null) throw new Exception();
				exception = errors.getException();
			} catch (final Exception e) {
				/* ignore */
			}

			if (exception == null) {
				try {
					final GErrorsDepreciated error = sGson.fromJson(response.responseBody, GErrorsDepreciated.class);
					if (error.error != null && error.error.code.equalsIgnoreCase("56")) {
						Session.getInstance().reportInvalidAccessToken();
						exception = new AccessTokenException(error);
					} else {
						exception = error.getException();
					}
				} catch (final Exception e) {
					/* ignore */
				}
			}
			if (exception != null) {
				throw exception;
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

	/** Enum of all available includes */
	public enum Include {
		answers, surveys, answer_sheets, all_organizational_roles, organizational_roles, followup_comments, contact_assignments, current_address, user, phone_numbers, person_transfers, email_addresses, questions, keyword, contacts, admins, leaders, people, groups, keywords, assigned_to, person, comments_on_me, rejoicables
	}
}