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
import com.missionhub.util.ObjectUtils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
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

    public static ApiRequest<Person> getPersonMe(final String facebookToken) {
        return getPersonMe(facebookToken, null);
    }

    public static ApiRequest<Person> getPersonMe(final String facebookToken, final ApiOptions options) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(HttpRequest.HEADER_AUTHORIZATION, "Facebook " + facebookToken);
        return getPersonMe(ApiOptions.builder().authenticated(false).headers(headers).merge(options).build());
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

    /* Permissions */

    public static ApiRequest<Permission> getPermission(final long permissionId) {
        return getPermission(permissionId, null);
    }

    public static ApiRequest<Permission> getPermission(final long permissionId, final ApiOptions options) {
        return new ApiRequest<Permission>(ApiOptions.builder().method(HttpRequest.METHOD_GET).url(buildUrl("permissions", permissionId)).responseParser(permissionParser).merge(options).build());
    }

    public static ApiRequest<List<Permission>> listPermissions() {
        return listPermissions(null, null);
    }

    public static ApiRequest<List<Permission>> listPermissions(final ApiOptions options, final ListOptions listOptions) {
        final Map<String, String> params = new HashMap<String, String>();
        if (listOptions != null) {
            listOptions.toParams(params);
        }
        return new ApiRequest<List<Permission>>(ApiOptions.builder().method(HttpRequest.METHOD_GET).url(buildUrl("permissions")).responseParser(permissionsParser).params(params).merge(options).build());
    }

    public static ApiRequest<List<Person>> bulkUpdatePermissions(final Collection<Long> personIds, final Long addPermission, final Long removePermission, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("include_archived", "true");
        params.put("filters[ids]", StringUtils.join(personIds, ","));
        if (addPermission != null) {
            params.put("add_permission", String.valueOf(addPermission));
        }
        if (removePermission != null) {
            params.put("remove_permission", String.valueOf(removePermission));
        }
        return new ApiRequest<List<Person>>(ApiOptions.builder().method(HttpRequest.METHOD_POST).url(buildUrl("organizational_permissions", "bulk")).responseParser(organizationalPermissionsParser).params(params).merge(options).build());
    }

    /* Labels */

    public static ApiRequest<Label> getLabel(final long labelId) {
        return getLabel(labelId, null);
    }

    public static ApiRequest<Label> getLabel(final long labelId, final ApiOptions options) {
        return new ApiRequest<Label>(ApiOptions.builder().method(HttpRequest.METHOD_GET).url(buildUrl("labels", labelId)).responseParser(labelParser).merge(options).build());
    }

    public static ApiRequest<Label> createLabel(final GLabel label) {
        return createLabel(label, null);
    }

    public static ApiRequest<Label> createLabel(final GLabel label, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        label.toParams(params);
        return new ApiRequest<Label>(ApiOptions.builder().method(HttpRequest.METHOD_POST).url(buildUrl("labels")).responseParser(labelParser).params(params).merge(options).build());
    }

    public static ApiRequest<Label> updateLabel(final GLabel label) {
        return updateLabel(label, null);
    }

    public static ApiRequest<Label> updateLabel(final GLabel label, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        label.toParams(params);
        return new ApiRequest<Label>(ApiOptions.builder().method(HttpRequest.METHOD_PUT).url(buildUrl("labels", label.id)).responseParser(labelParser).params(params).merge(options).build());
    }

    public static ApiRequest<Void> deleteLabel(final long labelId) {
        return deleteLabel(labelId, null);
    }

    public static ApiRequest<Void> deleteLabel(final long labelId, final ApiOptions options) {
        return new ApiRequest<Void>(ApiOptions.builder().method(HttpRequest.METHOD_DELETE).url(buildUrl("labels", labelId)).responseParser(new ApiResponseParser<Void>() {
            @Override
            public Void parseResponse(final ApiRequest response) throws Exception {
                final Label label = Application.getDb().getLabelDao().load(labelId);
                if (label != null) {
                    label.deleteWithRelations();
                }
                return null;
            }
        }).merge(options).build());
    }

    public static ApiRequest<List<Label>> listLabels() {
        return listLabels(null, null);
    }

    public static ApiRequest<List<Label>> listLabels(final ApiOptions options, final ListOptions listOptions) {
        final Map<String, String> params = new HashMap<String, String>();
        if (listOptions != null) {
            listOptions.toParams(params);
        }
        return new ApiRequest<List<Label>>(ApiOptions.builder().method(HttpRequest.METHOD_GET).url(buildUrl("labels")).responseParser(labelsParser).params(params).merge(options).build());
    }

    public static ApiRequest<List<Person>> bulkUpdateLabels(final Collection<Long> personIds, final Collection<Long> addLabels, final Collection<Long> removeLabels, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("include_archived", "true");
        params.put("filters[ids]", StringUtils.join(personIds, ","));
        if (ObjectUtils.isNotEmpty(addLabels)) {
            params.put("add_labels", StringUtils.join(addLabels, ","));
        }
        if (ObjectUtils.isNotEmpty(removeLabels)) {
            params.put("remove_labels", StringUtils.join(removeLabels, ","));
        }
        return new ApiRequest<List<Person>>(ApiOptions.builder().method(HttpRequest.METHOD_POST).url(buildUrl("organizational_labels", "bulk")).responseParser(organizationalLabelsParser).params(params).merge(options).build());
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
        params.put("filters[person_id]", StringUtils.join(personIds, ","));
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

    /* Interactions */
    public static ApiRequest<Interaction> getInteraction(final long interactionId) {
        return getInteraction(interactionId, null);
    }

    public static ApiRequest<Interaction> getInteraction(final long interactionId, final ApiOptions options) {
        return new ApiRequest<Interaction>(ApiOptions.builder().method(HttpRequest.METHOD_GET).url(buildUrl("interactions", interactionId)).responseParser(interactionParser).merge(options).build());
    }

    public static ApiRequest<Interaction> createInteraction(final GInteraction interaction) {
        return createInteraction(interaction, null);
    }

    public static ApiRequest<Interaction> createInteraction(final GInteraction interaction, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        interaction.toParams(params);
        return new ApiRequest<Interaction>(ApiOptions.builder().method(HttpRequest.METHOD_POST).url(buildUrl("interactions")).responseParser(interactionParser).params(params).merge(options).build());
    }

    public static ApiRequest<Interaction> updateInteraction(final GInteraction interaction) {
        return updateInteraction(interaction, null);
    }

    public static ApiRequest<Interaction> updateInteraction(final GInteraction interaction, final ApiOptions options) {
        final Map<String, String> params = new HashMap<String, String>();
        interaction.toParams(params);
        return new ApiRequest<Interaction>(ApiOptions.builder().method(HttpRequest.METHOD_PUT).url(buildUrl("interactions", interaction.id)).responseParser(interactionParser).params(params).merge(options)
                .build());
    }

    public static ApiRequest<Void> deleteInteraction(final long interactionId) {
        return deleteInteraction(interactionId, null);
    }

    public static ApiRequest<Void> deleteInteraction(final long interactionId, final ApiOptions options) {
        return new ApiRequest<Void>(ApiOptions.builder().method(HttpRequest.METHOD_DELETE).url(buildUrl("interactions", interactionId)).responseParser(new ApiResponseParser<Void>() {
            @Override
            public Void parseResponse(final ApiRequest response) throws Exception {
                final Interaction interaction = Application.getDb().getInteractionDao().load(interactionId);
                if (interaction != null) {
                    interaction.deleteWithRelations();
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

    private static final ApiResponseParser<Permission> permissionParser = new ApiResponseParser<Permission>() {
        @Override
        public Permission parseResponse(final ApiRequest response) throws Exception {
            final GPermission permission = sGson.fromJson(response.getBody(), GPermission.class);
            return permission.save(false);
        }
    };

    private static final ApiResponseParser<List<Permission>> permissionsParser = new ApiResponseParser<List<Permission>>() {
        @Override
        public List<Permission> parseResponse(final ApiRequest response) throws Exception {
            final GPermissions permissions = sGson.fromJson(response.getBody(), GPermissions.class);
            return permissions.save(false);
        }
    };

    private static final ApiResponseParser<Label> labelParser = new ApiResponseParser<Label>() {
        @Override
        public Label parseResponse(final ApiRequest response) throws Exception {
            final GLabel label = sGson.fromJson(response.getBody(), GLabel.class);
            return label.save(false);
        }
    };

    private static final ApiResponseParser<List<Label>> labelsParser = new ApiResponseParser<List<Label>>() {
        @Override
        public List<Label> parseResponse(final ApiRequest response) throws Exception {
            final GLabels labels = sGson.fromJson(response.getBody(), GLabels.class);
            return labels.save(false);
        }
    };

    public static final ApiResponseParser<List<Person>> organizationalPermissionsParser = new ApiResponseParser<List<Person>>() {
        @Override
        public List<Person> parseResponse(final ApiRequest response) throws Exception {
            final GPeople people = sGson.fromJson(response.getBody(), GPeople.class);
            return people.save(false);
        }
    };

    public static final ApiResponseParser<List<Person>> organizationalLabelsParser = new ApiResponseParser<List<Person>>() {
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

    private static final ApiResponseParser<Interaction> interactionParser = new ApiResponseParser<Interaction>() {
        @Override
        public Interaction parseResponse(final ApiRequest response) throws Exception {
            final GInteraction interaction = sGson.fromJson(response.getBody(), GInteraction.class);
            return interaction.save(false);
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
                if (part instanceof Iterable) {
                    sb.append(StringUtils.join((Iterable<?>) part, ","));
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
            Log.i(TAG, method + " " + url);
            if (headers != null) {
                Log.d(TAG, "==== headers ====");
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    Log.d(TAG, entry.getKey() + " : " + entry.getValue());
                }
            }
            if (params != null) {
                Log.d(TAG, "==== params ====");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    Log.d(TAG, entry.getKey() + " : " + entry.getValue());
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
        answers(GAnswerSheet.class), survey(GAnswerSheet.class),
        assigned_to(GContactAssignment.class), person(GContactAssignment.class),
        initiators(GInteraction.class), interaction_type(GInteraction.class), receiver(GInteraction.class), creator(GInteraction.class), last_updater(GInteraction.class), // interactions
        contacts(GOrganization.class), admins(GOrganization.class), leaders(GOrganization.class), people(GOrganization.class), surveys(GOrganization.class), groups(GOrganization.class), keywords(GOrganization.class), labels(GOrganization.class), // organizations
        label(GLabel.class), // organizational label
        permission(GPermission.class), // organizational permission
        phone_numbers(GPerson.class), email_addresses(GPerson.class), person_transfers(GPerson.class), contact_assignments(GPerson.class), assigned_tos(GPerson.class), // people
        answer_sheets(GPerson.class), all_organizational_permissions(GPerson.class), all_organization_and_children(GPerson.class),
        interactions(GPerson.class), organizational_labels(GPerson.class), addresses(GPerson.class), user(GPerson.class), current_address(GPerson.class), organizational_permission(GPerson.class),
        questions(GSurvey.class), all_questions(GSurvey.class), archived_questions(GSurvey.class), keyword(GSurvey.class); // surveys

        private final Type mValid;

        Include(Type valid) {
            mValid = valid;
        }

        public boolean isValidOn(Type type) {
            return type == mValid;
        }
    }
}