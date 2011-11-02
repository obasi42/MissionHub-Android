package com.missionhub.api;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.missionhub.Application;

public class ApiNotifier {
	
	public static final String TAG = ApiNotifier.class.getSimpleName();
	
	public static enum Type{
		ALL,
		DELETE_ANSWER, UPDATE_ANSWER, 
		DELETE_ASSIGNMENT, UPDATE_ASSIGNMENT,
		DELETE_EDUCATION, UPDATE_EDUCATION,
		UPDATE_FOLLOWUP_COMMENT, UPDATE_FOLLOWUP_COMMENTS,
		DELETE_INTEREST, UPDATE_INTEREST,
		UPDATE_KEYWORD,
		DELETE_LOCATION, UPDATE_LOCATION, 
		UPDATE_ORGANIZATION, 
		DELETE_ORGANIZATIONAL_ROLE, UPDATE_ORGANIZATIONAL_ROLE, UPDATE_ORGANIZATION_STUB,
		UPDATE_PERSON, 
		UPDATE_REJOICABLE,
		UPDATE_QUESTION, DELETE_QUESTION_CHOICE, UPDATE_QUESTION_CHOICE,
		JSON_CONTACT_ASSIGNMENT_ON_START, JSON_CONTACT_ASSIGNMENT_ON_FINISH, JSON_CONTACT_ASSIGNMENT_ON_SUCCESS, JSON_CONTACT_ASSIGNMENT_ON_FAILURE,
		JSON_CONTACTS_ON_START, JSON_CONTACTS_ON_FINISH, JSON_CONTACTS_ON_SUCCESS, JSON_CONTACTS_ON_FAILURE,
		JSON_CONTACTS_LIST_ON_START, JSON_CONTACTS_LIST_ON_FINISH, JSON_CONTACTS_LIST_ON_SUCCESS, JSON_CONTACTS_LIST_ON_FAILURE,
		JSON_FOLLOWUP_COMMENTS_ON_START, JSON_FOLLOWUP_COMMENTS_ON_FINISH, JSON_FOLLOWUP_COMMENTS_ON_SUCCESS, JSON_FOLLOWUP_COMMENTS_ON_FAILURE,
		JSON_FOLLOWUP_COMMENTS_DELETE_ON_START, JSON_FOLLOWUP_COMMENTS_DELETE_ON_FINISH, JSON_FOLLOWUP_COMMENTS_DELETE_ON_SUCCESS, JSON_FOLLOWUP_COMMENTS_DELETE_ON_FAILURE,
		JSON_FOLLOWUP_COMMENTS_POST_ON_START, JSON_FOLLOWUP_COMMENTS_POST_ON_FINISH, JSON_FOLLOWUP_COMMENTS_POST_ON_SUCCESS, JSON_FOLLOWUP_COMMENTS_POST_ON_FAILURE,
		JSON_ORGANIZATIONS_ON_START, JSON_ORGANIZATIONS_ON_FINISH, JSON_ORGANIZATIONS_ON_SUCCESS, JSON_ORGANIZATIONS_ON_FAILURE,
		JSON_PEOPLE_ON_START, JSON_PEOPLE_ON_FINISH, JSON_PEOPLE_ON_SUCCESS, JSON_PEOPLE_ON_FAILURE,
		JSON_ROLES_ON_START, JSON_ROLES_ON_SUCCESS, JSON_ROLES_ON_FINISH, JSON_ROLES_ON_FAILURE,
	}

	//private List<WeakReference<Handler>> handlers = Collections.synchronizedList(new ArrayList<WeakReference<Handler>>());
	private Map<WeakReference<Handler>, Type[]> handlers = Collections.synchronizedMap(new HashMap<WeakReference<Handler>, Type[]>());
	private Map<String, WeakReference<Handler>> handlerIds = Collections.synchronizedMap(new HashMap<String, WeakReference<Handler>>());
	//private Application application;
	
	public ApiNotifier(Application application) {
		//this.application = application;
	}
	
	public synchronized void subscribe(Object o, Handler h, Type ... types) {
		subscribe(o.getClass().getCanonicalName(), h, types);
	}
	
	public synchronized void subscribe(String id, Handler h, Type ... types) {
		if (types.length == 0) Log.w(TAG, "No message types were passed when subscribing. The handler will receive no messages.");
		final WeakReference<Handler> wr = new WeakReference<Handler>(h);
		if (handlerIds.containsKey(id)) {
			unsubscribe(id);
		}
		handlerIds.put(id, wr);
		handlers.put(wr, types);
	}
	
	public synchronized void unsubscribe(Object o) {
		unsubscribe(o.getClass().getCanonicalName());
	}
	
	public synchronized void unsubscribe(String id) {
		final WeakReference<Handler> wr = handlerIds.get(id);
		handlers.remove(wr);
		handlerIds.remove(id);
	}
	
	public synchronized void postMessage(Type type, Bundle data) {
		final List<WeakReference<Handler>> delete = Collections.synchronizedList(new ArrayList<WeakReference<Handler>>());
		final Iterator<WeakReference<Handler>> itr = handlers.keySet().iterator();
		while(itr.hasNext()) {
			final WeakReference<Handler> wr = itr.next();
			final Handler h = wr.get();
			if (h != null) {
				final Type[] types = handlers.get(wr);
				Arrays.sort(types);
				if (Arrays.binarySearch(types, type) >= 0 || Arrays.binarySearch(types, Type.ALL) >= 0) {
					final Message m = h.obtainMessage();
					m.what = type.ordinal();
					m.setData(data);
					h.sendMessage(m);
				}
			} else {
				delete.add(wr);
			}
		}
		for (WeakReference<Handler> wr : delete) {
			handlers.remove(wr);
		}
		delete.clear();
	}
}