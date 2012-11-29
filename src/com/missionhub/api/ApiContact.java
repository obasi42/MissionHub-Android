package com.missionhub.api;

import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.missionhub.network.HttpParams;
import com.missionhub.util.U;

public class ApiContact {

	public String firstName = "";
	public String lastName = "";
	public String gender = "";

	public String emailEmail = "";
	public boolean emailPrimary = false;

	public String phoneNumber = "";
	public String phoneLocation = "mobile";
	public boolean phonePrimary = false;

	public String address1 = "";
	public String address2 = "";
	public String addressCity = "";
	public String addressCountry = "";
	public String addressState = "";
	public String addressZip = "";

	public boolean assignToMe = false;

	public HashMultimap<Long, String> mAnswers = HashMultimap.create();

	public void appendParams(final HttpParams params) {
		if (!U.isNullEmpty(firstName)) {
			params.add("person[first_name]", firstName);
		}
		if (!U.isNullEmpty(lastName)) {
			params.add("person[last_name]", lastName);
		}
		if (!U.isNullEmpty(gender)) {
			params.add("person[gender]", gender);
		}
		if (!U.isNullEmpty(phoneNumber)) {
			params.add("person[phone_number][number]", phoneNumber);
			params.add("person[phone_number][location]", phoneLocation);
			params.add("person[phone_number][primary]", phonePrimary);
		}
		if (!U.isNullEmpty(emailEmail)) {
			params.add("person[email_address][email]", emailEmail);
			params.add("person[email_address][primary]", emailPrimary);
		}
		if (!U.isNullEmpty(address1)) {
			params.add("person[current_address_attributes][address1]", address1);
		}
		if (!U.isNullEmpty(address2)) {
			params.add("person[current_address_attributes][address2]", address2);
		}
		if (!U.isNullEmpty(addressCity)) {
			params.add("person[current_address_attributes][city]", addressCity);
		}
		if (!U.isNullEmpty(addressCountry)) {
			params.add("person[current_address_attributes][country]", addressCountry);
		}
		if (!U.isNullEmpty(addressState)) {
			params.add("person[current_address_attributes][state]", addressState);
		}
		if (!U.isNullEmpty(addressZip)) {
			params.add("person[current_address_attributes][zip]", addressZip);
		}

		params.add("assign_to_me", assignToMe);

		for (final Long key : mAnswers.keySet()) {
			final Set<String> values = mAnswers.get(key);
			if (values.size() <= 1) {
				for (final String value : values) {
					params.add("answers[" + key + "]", value);
				}
			} else {
				int count = 0;
				for (final String value : values) {
					params.add("answers[" + key + "][" + count + "]", value);
					count++;
				}
			}
		}
	}

	public CharSequence getName() {
		return (firstName + " " + lastName).trim();
	}

	public void setName(final String string) {
		final String[] parts = string.split(" ");

		if (parts.length == 1) {
			firstName = parts[0];
		} else if (parts.length == 2) {
			firstName = parts[0];
			lastName = parts[1];
		} else if (parts.length > 2) {
			for (int i = 0; i < parts.length - 1; i++) {
				firstName += parts[i] + " ";
			}
			firstName = firstName.trim();
			lastName = parts[parts.length - 1];
		}
	}

	public boolean isValid() {
		return !U.isNullEmpty(firstName);
	}
}