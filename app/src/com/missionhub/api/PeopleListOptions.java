package com.missionhub.api;

import com.missionhub.model.generic.FollowupStatus;
import com.missionhub.model.generic.Gender;

import java.util.Collection;
import java.util.Set;

public class PeopleListOptions extends ListOptions {

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

}