package com.missionhub.api;

import java.util.Collection;
import java.util.Set;

import com.missionhub.util.U.FollowupStatus;

public class PersonListOptions extends ListOptions {

	private PersonListOptions(final ListOptions filters) {
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

		public Builder role(final long roleId) {
			filters.addFilter("roles", roleId);
			return this;
		}

		public Builder role(final Collection<Long> roleIds) {
			filters.addFilter("roles", roleIds);
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

		public Builder gender(final String gender) {
			filters.addFilter("gender", gender);
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
			filters.addFilter("assinged_to", peopleIds);
			return this;
		}

		public PersonListOptions build() {
			return new PersonListOptions(filters);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

}