package com.missionhub.api;

import android.os.Build;
import android.util.Log;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.missionhub.application.Application;
import com.missionhub.application.Configuration;
import com.missionhub.application.Session;
import com.missionhub.model.*;
import com.missionhub.model.gson.*;
import com.missionhub.util.U;

import java.util.*;

public class Api {

    /**
     * the singleton api object
     */
    private static Api sApi;

    /**
     * the singleton gson parser
     */
    protected static final Gson sGson = new Gson();

    public static final String TAG = Api.class.getSimpleName();

    private Api() {
    }

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

	/* People */

    public static ApiRequest<Person> getPerson(final long personId) {
        return getPerson(personId, null);
    }

    public static ApiRequest<Person> getPerson(final long personId, final ApiOptions options) {
        return new ApiRequest<Person>(ApiOptions.builder().method(HttpRequest.METHOD_GET).url(buildUrl("people", personId)).responseParser(personParser).merge(options).build());
    }

    public static ApiRequest<Person> getPersonMe() {
        return getPersonMe((ApiOptions) null);
    }

    public static ApiRequest<Person> getPersonMe(final String accessToken) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(HttpRequest.HEADER_AUTHORIZATION, "Bearer " + accessToken);
        return getPersonMe(ApiOptions.builder().authenticated(false).headers(headers).build());
    }

    public static ApiRequest<Person> getPersonMe(final ApiOptions options) {
        return new ApiRequest<Person>(ApiOptions.builder().method(HttpRequest.METHOD_GET).url(buildUrl("people", "me")).responseParser(personParser).merge(options).build());
    }

    public static ApiRequest<Person> createPerson(final GPerson person) {
        return createPerson(person, null);
    }

    public static ApiRequest<Person> createPerson(final GPerson person, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        person.toParams(params);
        return new ApiRequest<Person>(ApiOptions.builder().method(HttpRequest.METHOD_POST).url(buildUrl("people")).responseParser(personParser).params(params).merge(options).build());
    }

    public static ApiRequest<Person> updatePerson(final GPerson person) {
        return updatePerson(person, null);
    }

    public static ApiRequest<Person> updatePerson(final GPerson person, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        person.toParams(params);
        return new ApiRequest<Person>(ApiOptions.builder().method(HttpRequest.METHOD_PUT).url(buildUrl("people", person.id)).responseParser(personParser).params(params).merge(options).build());
    }

    public static ApiRequest<Void> deletePerson(final long personId) {
        return deletePerson(personId, null);
    }

    public static ApiRequest<Void> deletePerson(final long personId, final ApiOptions options) {
        return new ApiRequest<Void>(ApiOptions.builder().method(HttpRequest.METHOD_DELETE).url(buildUrl("people", personId)).responseParser(new ApiResponseParser<Void>() {
            @Override
            public Void parseResponse(final ApiRequest response) throws Exception {
                final Person person = Application.getDb().getPersonDao().load(personId);
                if (person != null) {
                    person.deleteWithRelations();
                }
                return null;
            }
        }).merge(options).build());
    }

    public static ApiRequest<List<Person>> listPeople(final ListOptions listOptions) {
        return listPeople(listOptions, null);
    }

    public static ApiRequest<List<Person>> listPeople(final ListOptions listOptions, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        listOptions.toParams(params);
        return new ApiRequest<List<Person>>(ApiOptions.builder().method(HttpRequest.METHOD_GET).url(buildUrl("people")).responseParser(peopleParser).params(params).merge(options).build());
    }

    /* Roles (Labels) */
    public static ApiRequest<Role> getRole(final long roleId) {
        return getRole(roleId, null);
    }

    public static ApiRequest<Role> getRole(final long roleId, final ApiOptions options) {
        return new ApiRequest<Role>(ApiOptions.builder().method(HttpRequest.METHOD_GET).url(buildUrl("roles", roleId)).responseParser(roleParser).merge(options).build());
    }

    public static ApiRequest<Role> createRole(final GRole person) {
        return createRole(person, null);
    }

    public static ApiRequest<Role> createRole(final GRole role, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        role.toParams(params);
        return new ApiRequest<Role>(ApiOptions.builder().method(HttpRequest.METHOD_POST).url(buildUrl("roles")).responseParser(roleParser).params(params).merge(options).build());
    }

    public static ApiRequest<Role> updateRole(final GRole role) {
        return updateRole(role, null);
    }

    public static ApiRequest<Role> updateRole(final GRole role, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        role.toParams(params);
        return new ApiRequest<Role>(ApiOptions.builder().method(HttpRequest.METHOD_PUT).url(buildUrl("roles", role.id)).responseParser(roleParser).params(params).merge(options).build());
    }

    public static ApiRequest<Void> deleteRole(final long roleId) {
        return deleteRole(roleId, null);
    }

    public static ApiRequest<Void> deleteRole(final long roleId, final ApiOptions options) {
        return new ApiRequest<Void>(ApiOptions.builder().method(HttpRequest.METHOD_DELETE).url(buildUrl("roles", roleId)).responseParser(new ApiResponseParser<Void>() {
            @Override
            public Void parseResponse(final ApiRequest response) throws Exception {
                final Role role = Application.getDb().getRoleDao().load(roleId);
                if (role != null) {
                    role.deleteWithRelations();
                }
                return null;
            }
        }).merge(options).build());
    }

    public static ApiRequest<List<Role>> listRoles() {
        return listRoles(null, null);
    }

    public static ApiRequest<List<Role>> listRoles(final ApiOptions options, final ListOptions listOptions) {
        final Map<String, String> params = new HashMap<String, String>();
        if (listOptions != null) {
            listOptions.toParams(params);
        }
        return new ApiRequest<List<Role>>(ApiOptions.builder().method(HttpRequest.METHOD_GET).url(buildUrl("roles")).responseParser(rolesParser).params(params).merge(options).build());
    }

    /* Organizational Roles (Labels) */

    public static ApiRequest<List<Person>> bulkUpdateRoles(final Collection<Long> personIds, final Collection<Long> addRoles, final Collection<Long> removeRoles, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("include_archived", "true");
        params.put("filters[ids]", U.toCSV(personIds));
        if (!U.isNullEmpty(addRoles)) {
            params.put("add_roles", U.toCSV(addRoles));
        }
        if (!U.isNullEmpty(removeRoles)) {
            params.put("remove_roles", U.toCSV(removeRoles));
        }
        return new ApiRequest<List<Person>>(ApiOptions.builder().method(HttpRequest.METHOD_POST).url(buildUrl("organizational_roles", "bulk")).responseParser(organizationalRolesParser).params(params).merge(options).build());
    }

	/* Contact Assignments */

    public static ApiRequest<List<ContactAssignment>> bulkUpdateContactAssignments(final Collection<GContactAssignment> assignments) {
        return bulkUpdateContactAssignments(assignments, null);
    }

    public static ApiRequest<List<ContactAssignment>> bulkUpdateContactAssignments(final Collection<GContactAssignment> assignments, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        addContactAssignmentParams(params, assignments);
        return new ApiRequest<List<ContactAssignment>>(ApiOptions.builder().method(HttpRequest.METHOD_PUT).url(buildUrl("contact_assignments", "bulk_update")).responseParser(contactAssignmentsParser)
                .params(params).merge(options).build());
    }

    public static ApiRequest<Void> bulkDeleteContactAssignments(final Collection<Long> personIds) {
        return bulkDeleteContactAssignments(personIds, null);
    }

    public static ApiRequest<Void> bulkDeleteContactAssignments(final Collection<Long> personIds, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("filters[person_id]", U.toCSV(personIds));
        return new ApiRequest<Void>(ApiOptions.builder().method(HttpRequest.METHOD_DELETE).url(buildUrl("contact_assignments", "bulk_destroy")).responseParser(new ApiResponseParser<Void>() {
            @Override
            public Void parseResponse(final ApiRequest response) throws Exception {
                List<Long> keys = Application.getDb().getContactAssignmentDao().queryBuilder().where(ContactAssignmentDao.Properties.Person_id.in(personIds)).listKeys();
                Application.getDb().getContactAssignmentDao().deleteByKeyInTx(keys);
                return null;
            }
        }).params(params).merge(options).build());
    }

    public static ApiRequest<List<ContactAssignment>> listContactAssignments(final ListOptions listOptions) {
        return listContactAssignments(listOptions, null);
    }

    public static ApiRequest<List<ContactAssignment>> listContactAssignments(final ListOptions listOptions, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        if (listOptions != null) {
            listOptions.toParams(params);
        }
        return new ApiRequest<List<ContactAssignment>>(ApiOptions.builder().method(HttpRequest.METHOD_GET).url(buildUrl("contact_assignments")).responseParser(contactAssignmentsParser).params(params)
                .merge(options).build());
    }

    private static void addContactAssignmentParams(final Map<String, String> params, final Collection<GContactAssignment> assignments) {
        final Iterator<GContactAssignment> itr = assignments.iterator();
        int i = 0;
        while (itr.hasNext()) {
            final GContactAssignment assignment = itr.next();
            if (assignment == null) continue;

            if (assignment.id > 0) {
                params.put("contact_assignments[" + i + "][id]", String.valueOf(assignment.id));
            }
            if (assignment.assigned_to_id > 0) {
                params.put("contact_assignments[" + i + "][assigned_to_id]", String.valueOf(assignment.assigned_to_id));
            }
            if (assignment.person_id > 0) {
                params.put("contact_assignments[" + i + "][person_id]", String.valueOf(assignment.person_id));
            }
            if (assignment.organization_id > 0) {
                params.put("contact_assignments[" + i + "][organization_id]", String.valueOf(assignment.organization_id));
            }
            i++;
        }
    }

    /* Followup Comments */
    public static ApiRequest<FollowupComment> getFollowupComment(final long commentId) {
        return getFollowupComment(commentId, null);
    }

    public static ApiRequest<FollowupComment> getFollowupComment(final long commentId, final ApiOptions options) {
        return new ApiRequest<FollowupComment>(ApiOptions.builder().method(HttpRequest.METHOD_GET).url(buildUrl("followup_comments", commentId)).responseParser(commentParser).merge(options).build());
    }

    public static ApiRequest<FollowupComment> createFollowupComment(final GFollowupComment comment) {
        return createFollowupComment(comment, null);
    }

    public static ApiRequest<FollowupComment> createFollowupComment(final GFollowupComment comment, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        comment.toParams(params);
        return new ApiRequest<FollowupComment>(ApiOptions.builder().method(HttpRequest.METHOD_POST).url(buildUrl("followup_comments")).responseParser(commentParser).params(params).merge(options).build());
    }

    public static ApiRequest<FollowupComment> updateFollowupComment(final GFollowupComment comment) {
        return updateFollowupComment(comment, null);
    }

    public static ApiRequest<FollowupComment> updateFollowupComment(final GFollowupComment comment, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        comment.toParams(params);
        return new ApiRequest<FollowupComment>(ApiOptions.builder().method(HttpRequest.METHOD_PUT).url(buildUrl("followup_comments", comment.id)).responseParser(commentParser).params(params).merge(options)
                .build());
    }

    public static ApiRequest<Void> deleteFollowupComment(final long commentId) {
        return deleteFollowupComment(commentId, null);
    }

    public static ApiRequest<Void> deleteFollowupComment(final long commentId, final ApiOptions options) {
        return new ApiRequest<Void>(ApiOptions.builder().method(HttpRequest.METHOD_DELETE).url(buildUrl("followup_comments", commentId)).responseParser(new ApiResponseParser<Void>() {
            @Override
            public Void parseResponse(final ApiRequest response) throws Exception {
                final FollowupComment comment = Application.getDb().getFollowupCommentDao().load(commentId);
                if (comment != null) {

                    comment.deleteWithRelations();
                }
                return null;
            }
        }).merge(options).build());
    }

    /* Organizations */
    public static ApiRequest<Organization> getOrganization(final long organizationId) {
        return getOrganization(organizationId, null);
    }

    public static ApiRequest<Organization> getOrganization(final long organizationId, final ApiOptions options) {
        return new ApiRequest<Organization>(ApiOptions.builder().method(HttpRequest.METHOD_GET).url(buildUrl("organizations", organizationId)).responseParser(organizationParser).merge(options).build());
    }

    public static ApiRequest<GOrganization> createOrganization(final GOrganization organization) {
        return createOrganization(organization, null);
    }

    public static ApiRequest<GOrganization> createOrganization(final GOrganization organization, final ApiOptions options) {
        // TODO: implement createOrganization
        throw new RuntimeException("Unimplemented Method");
    }

    public static ApiRequest<GOrganization> updateOrganization(final GOrganization organization) {
        return updateOrganization(organization, null);
    }

    public static ApiRequest<GOrganization> updateOrganization(final GOrganization organization, final ApiOptions options) {
        // TODO: implement updateOrganization
        throw new RuntimeException("Unimplemented Method");
    }

    public static ApiRequest<Void> deleteOrganization(final long organizationId) {
        return deleteOrganization(organizationId, null);
    }

    public static ApiRequest<Void> deleteOrganization(final long organizationId, final ApiOptions options) {
        // TODO: implement deleteOrganization
        throw new RuntimeException("Unimplemented Method");
    }

    public static ApiRequest<List<Organization>> listOrganizations(final ApiOptions options) {
        return listOrganizations(null, options);
    }

    public static ApiRequest<List<Organization>> listOrganizations(final ListOptions listOptions) {
        return listOrganizations(listOptions, null);
    }

    public static ApiRequest<List<Organization>> listOrganizations(final ListOptions listOptions, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        if (listOptions != null) {
            listOptions.toParams(params);
        }
        return new ApiRequest<List<Organization>>(ApiOptions.builder().method(HttpRequest.METHOD_GET).url(buildUrl("organizations")).responseParser(organizationsParser).params(params).merge(options).build());
    }

    /* Surveys */
    public static String getSurveyUrl() throws ApiException {
        try {
            final Map<String, String> params = new HashMap<String, String>();
            params.put("access_token", Session.getInstance().getAccessToken());
            params.put("org_id", String.valueOf(Session.getInstance().getOrganizationId()));
            params.put("mobile", "1");
            return HttpRequest.encode(HttpRequest.append(Configuration.getSurveyUrl(), params));
        } catch (Exception e) {
            throw ApiException.wrap(e);
        }
    }

	/* OAuth */

    /**
     * Returns the access token from a grant code
     *
     * @param code
     * @return
     */
    public static ApiRequest<GAccessToken> getAccessToken(final String code) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("client_id", Configuration.getOauthClientId());
        params.put("client_secret", Configuration.getOauthClientSecret());
        params.put("code", code);
        params.put("grant_type", "authorization_code");
        params.put("scope", Configuration.getOauthScope());
        params.put("redirect_uri", Configuration.getOauthUrl() + "/done.json");

        return new ApiRequest<GAccessToken>(ApiOptions.builder().method(HttpRequest.METHOD_POST).url(Configuration.getOauthUrl() + "/access_token").params(params).authenticated(false)
                .responseParser(new ApiResponseParser<GAccessToken>() {
                    @Override
                    public GAccessToken parseResponse(final ApiRequest response) throws Exception {
                        return sGson.fromJson(response.getBody(), GAccessToken.class);
                    }
                }).build());
    }

    /* Generic Response Parsers */
    private static final ApiResponseParser<Person> personParser = new ApiResponseParser<Person>() {
        @Override
        public Person parseResponse(final ApiRequest response) throws Exception {
            final GPeople people = sGson.fromJson(response.getBody(), GPeople.class);
            return people.person.save(false);
        }
    };

    private static final ApiResponseParser<List<Person>> peopleParser = new ApiResponseParser<List<Person>>() {
        @Override
        public List<Person> parseResponse(final ApiRequest response) throws Exception {
            final GPeople people = sGson.fromJson(response.getBody(), GPeople.class);
            return people.save(false);
        }
    };

    private static final ApiResponseParser<Role> roleParser = new ApiResponseParser<Role>() {
        @Override
        public Role parseResponse(final ApiRequest response) throws Exception {
            final GRole role = sGson.fromJson(response.getBody(), GRole.class);
            return role.save(false);
        }
    };

    private static final ApiResponseParser<List<Role>> rolesParser = new ApiResponseParser<List<Role>>() {
        @Override
        public List<Role> parseResponse(final ApiRequest response) throws Exception {
            final GRoles roles = sGson.fromJson(response.getBody(), GRoles.class);
            return roles.save(false);
        }
    };

    public static final ApiResponseParser<List<Person>> organizationalRolesParser = new ApiResponseParser<List<Person>>() {
        @Override
        public List<Person> parseResponse(final ApiRequest response) throws Exception {
            final GPeople people = sGson.fromJson(response.getBody(), GPeople.class);
            return people.save(false);
        }
    };

    private static final ApiResponseParser<List<ContactAssignment>> contactAssignmentsParser = new ApiResponseParser<List<ContactAssignment>>() {
        @Override
        public List<ContactAssignment> parseResponse(final ApiRequest response) throws Exception {
            final GContactAssignments assignments = sGson.fromJson(response.getBody(), GContactAssignments.class);
            return assignments.save(false);
        }
    };

    private static final ApiResponseParser<FollowupComment> commentParser = new ApiResponseParser<FollowupComment>() {
        @Override
        public FollowupComment parseResponse(final ApiRequest response) throws Exception {
            final GFollowupComment comment = sGson.fromJson(response.getBody(), GFollowupComment.class);
            return comment.save(false);
        }
    };

    private static final ApiResponseParser<Organization> organizationParser = new ApiResponseParser<Organization>() {
        @Override
        public Organization parseResponse(final ApiRequest response) throws Exception {
            final GOrganizations orgs = sGson.fromJson(response.getBody(), GOrganizations.class);
            return orgs.save(false).get(0);
        }
    };

    private static final ApiResponseParser<List<Organization>> organizationsParser = new ApiResponseParser<List<Organization>>() {
        @Override
        public List<Organization> parseResponse(final ApiRequest response) throws Exception {
            final GOrganizations orgs = sGson.fromJson(response.getBody(), GOrganizations.class);
            return orgs.save(false);
        }
    };

    public static interface ApiResponseParser<T> {
        public T parseResponse(ApiRequest response) throws Exception;
    }

	/* Helpers */

    /**
     * Builds an api url from parts
     *
     * @param parts
     * @return
     */
    private static String buildUrl(final Object... parts) {
        final StringBuilder sb = new StringBuilder(Configuration.getApiUrl());
        sb.append('/');
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
     * @param method        the HttpMethod to use
     * @param url           the url
     * @param headers       the http headers
     * @param params        the url params
     * @param authenticated true if the access token header should be added
     * @return the http response object
     * @throws Exception
     */
    protected HttpRequest createRequest(final String url, final String method, Map<String, String> headers, Map<String, String> params, final boolean authenticated) throws Exception {
        // create headers and params if null
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        if (params == null) {
            params = new HashMap<String, String>();
        }

        // add the api version header
        if (headers.get("API-VERSION") == null) {
            headers.put("API-VERSION", Configuration.getApiVersion());
        }

        // add oauth token to the request if needed
        if (authenticated) {
            headers.put(HttpRequest.HEADER_AUTHORIZATION, "Bearer " + Session.getInstance().getAccessToken());

            if (Session.getInstance().getOrganizationId() >= 0) {
                params.put("organization_id", String.valueOf(Session.getInstance().getOrganizationId()));
            }
        }

        logRequest(url, method, headers, params, authenticated);

        appendLoggingParams(params);

        HttpRequest request;
        if (HttpRequest.METHOD_POST.equals(method) || HttpRequest.METHOD_PUT.equals(method)) {
            request = new HttpRequest(HttpRequest.encode(url), method);
        } else {
            request = new HttpRequest(HttpRequest.encode(HttpRequest.append(url, params)), method);
        }

        request.acceptGzipEncoding().uncompress(true);
        request.headers(headers);

        if (HttpRequest.METHOD_POST.equals(method) || HttpRequest.METHOD_PUT.equals(method)) {
            request.form(params);
        }

        return request;
    }

    private void logRequest(String url, String method, Map<String, String> headers, Map<String, String> params, boolean authenticated) {
        if (Configuration.getEnvironment() == Configuration.Environment.DEVELOPMENT) {
            Log.e(TAG, method + " " + url);
            if (headers != null) {
                Log.w(TAG, "==== headers ====");
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    Log.w(TAG, entry.getKey() + " : " + entry.getValue());
                }
            }
            if (params != null) {
                Log.w(TAG, "==== params ====");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    Log.w(TAG, entry.getKey() + " : " + entry.getValue());
                }
            }
        }
    }

    /**
     * Appends parameters for logging
     *
     * @param params
     */
    private void appendLoggingParams(final Map<String, String> params) {
        try {
            params.put("platform", "android");
            params.put("platform_product", Build.PRODUCT);
            params.put("platform_release", android.os.Build.VERSION.RELEASE);
            params.put("app", String.valueOf(Application.getVersionCode()));
        } catch (final Exception ignore) { /* ignore */}
    }

    /**
     * Enum of all available includes
     */
    public enum Include {
        answers, surveys, answer_sheets, all_organizational_roles, all_organization_and_children, organizational_roles, followup_comments, contact_assignments, assigned_tos, current_address, user, phone_numbers, person_transfers, email_addresses, all_questions, elements, keyword, contacts, admins, leaders, people, groups, keywords, assigned_to, person, comments_on_me, rejoicables
    }
}