package com.missionhub.ui;

import java.util.List;

import com.missionhub.model.Person;

public class StaticContactListProvider extends ContactListProvider {

	@Override
	public List<Person> getMore() throws Exception {
		return null;
	}

	@Override
	public boolean isAtEnd() {
		return true;
	}

	@Override
	public void onError(final Exception e) {}

}