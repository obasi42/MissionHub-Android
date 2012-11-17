package com.missionhub.ui;


public class StaticContactListProvider extends ContactListProvider {
	@Override
	public void getMore() {}

	@Override
	public boolean hasMore() {
		return false;
	}

	@Override
	public boolean isWorking() {
		return false;
	}
}