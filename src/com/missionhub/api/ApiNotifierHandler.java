package com.missionhub.api;

import java.util.ArrayList;

import com.missionhub.api.ApiNotifier.Type;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ApiNotifierHandler extends Handler {

	private ArrayList<String> t = new ArrayList<String>();

	public ApiNotifierHandler() {
		super();
	}

	public ApiNotifierHandler(String... tags) {
		super();
		for (final String tag : tags) {
			t.add(tag);
		}
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		final Type type = ApiNotifier.Type.values()[msg.what];
		final Bundle bundle = msg.getData();
		final String tag = bundle.getString("tag");
		final Throwable throwable = (Throwable) bundle.getSerializable("throwable");
		final long id = bundle.getLong("id");
		if (t.contains(tag)) {
			handleMessage(type, tag, bundle, throwable, id);
		}
	}

	public void handleMessage(Type type, String tag, Bundle bundle, Throwable throwable, long rowId) {

	}

}