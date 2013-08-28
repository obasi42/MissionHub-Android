package com.missionhub.api;

import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.model.InteractionType;
import com.missionhub.model.Label;
import com.missionhub.model.Permission;
import com.missionhub.model.Person;
import com.missionhub.model.generic.FollowupStatus;
import com.missionhub.model.generic.Gender;
import com.missionhub.util.ResourceUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class PeopleListOptions extends ListOptions implements Serializable {

    private PeopleListOptions(final ListOptions filters) {
        super(filters);
    }

    public static class Builder {

        private final ListOptions filters = new ListOptions();

        public Builder id(final long personId) {
            filters.addFilter("ids", personId);
            return this;
        }

        public Builder id(final Collection<Long> personIds) {
            filters.addFilter("ids", personIds);
            return this;
        }

        public Builder label(final long labelId) {
            filters.addFilter("labels", labelId);
            return this;
        }

        public Builder labels(final Collection<Long> labelIds) {
            filters.addFilter("labels", labelIds);
            return this;
        }

        public Builder permission(final long permissionId) {
            filters.addFilter("permissions", permissionId);
            return this;
        }

        public Builder permissions(final Collection<Long> permissionIds) {
            filters.addFilter("permissions", permissionIds);
            return this;
        }

        public Builder interaction(final long interactionTypeId) {
            filters.addFilter("interactions", interactionTypeId);
            return this;
        }

        public Builder interactions(final Collection<Long> interactionTypeIds) {
            filters.addFilter("interactions", interactionTypeIds);
            return this;
        }

        public Builder firstNameLike(final String firstName) {
            filters.addFilter("first_name_like", firstName);
            return this;
        }

        public Builder lastNameLike(final String lastName) {
            filters.addFilter("last_name_like", lastName);
            return this;
        }

        public Builder nameOrEmailLike(final String nameOrEmail) {
            filters.addFilter("name_or_email_like", nameOrEmail);
            return this;
        }

        public Builder nameLike(final String name) {
            filters.addFilter("name_like", name);
            return this;
        }

        public Builder emailLike(final String email) {
            filters.addFilter("email_like", email);
            return this;
        }

        public Builder gender(final Gender gender) {
            filters.addFilter("gender", gender.toFilter());
            return this;
        }

        public Builder followupStatus(final FollowupStatus status) {
            filters.addFilter("followup_status", status.name());
            return this;
        }

        public Builder followupStatus(final Set<FollowupStatus> statuses) {
            for (final FollowupStatus status : statuses) {
                filters.addFilter("followup_status", status.name());
            }
            return this;
        }

        public Builder assignedTo(final long personId) {
            filters.addFilter("assigned_to", personId);
            return this;
        }

        public Builder assignedTo(final Collection<Long> peopleIds) {
            filters.addFilter("assigned_to", peopleIds);
            return this;
        }

        public PeopleListOptions build() {
            return new PeopleListOptions(filters);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Object clone() {
        return new PeopleListOptions((ListOptions) super.clone());
    }

    public String toHumanString() {
        List<String> parts = new ArrayList<String>();

        // query
        String search = getFilterValue("name_or_email_like");
        if (StringUtils.isNotEmpty(search)) {
            parts.add(ResourceUtils.getString(R.string.list_options_matching) + " <b>" + search + "</b>");
        }

        // assigned tos
        Collection<String> assignedTos = new ArrayList<String>();
        Collection<String> assignedToIds = getFilterValues("assigned_to");
        for (String personId : assignedToIds) {
            Person p = Application.getDb().getPersonDao().load(Long.parseLong(personId));
            if (p != null) {
                assignedTos.add("<b>" + p.getName() + "</b>");
            }
        }
        if (!assignedTos.isEmpty()) {
            parts.add(ResourceUtils.getString(R.string.list_options_assigned_to) + " " + StringUtils.join(assignedTos, " " + ResourceUtils.getString(R.string.list_options_or) + " "));
        }

        // labels
        Collection<String> labels = new ArrayList<String>();
        Collection<String> labelIds = getFilterValues("labels");
        for (String labelId : labelIds) {
            Label l = Application.getDb().getLabelDao().load(Long.parseLong(labelId));
            if (l != null) {
                labels.add("<b>" + l.getTranslatedName() + "</b>");
            }
        }
        if (!labels.isEmpty()) {
            parts.add(ResourceUtils.getString(R.string.list_options_labeled_with) + " " + StringUtils.join(labels, " " + ResourceUtils.getString(R.string.list_options_or) + " "));
        }

        // interaction types
        Collection<String> interactions = new ArrayList<String>();
        Collection<String> interactionTypeIds = getFilterValues("interactions");
        for (String typeId : interactionTypeIds) {
            InteractionType type = Application.getDb().getInteractionTypeDao().load(Long.parseLong(typeId));
            if (type != null) {
                interactions.add("<b>" + type.getTranslatedName() + "</b>");
            }
        }
        if (!interactions.isEmpty()) {
            parts.add(ResourceUtils.getString(R.string.list_options_with_interactions) + " " + StringUtils.join(interactions, " " + ResourceUtils.getString(R.string.list_options_or) + " "));
        }

        // persmission
        Collection<String> permissions = new ArrayList<String>();
        Collection<String> permissionIds = getFilterValues("permissions");
        for (String permissionId : permissionIds) {
            Permission permission = Application.getDb().getPermissionDao().load(Long.parseLong(permissionId));
            if (permission != null) {
                permissions.add("<b>" + permission.getTranslatedName() + "</b> " + ResourceUtils.getString(R.string.list_options_permissions));
            }
        }
        if (!permissions.isEmpty()) {
            parts.add(" " + ResourceUtils.getString(R.string.list_options_with) + " " + StringUtils.join(permissions, " " + ResourceUtils.getString(R.string.list_options_or) + " "));
        }

        if (parts.isEmpty()) {
            return "";
        } else {
            return ResourceUtils.getString(R.string.list_options_showing_contacts) + " " + StringUtils.join(parts, " " + ResourceUtils.getString(R.string.list_options_and) + " ");
        }
    }

}