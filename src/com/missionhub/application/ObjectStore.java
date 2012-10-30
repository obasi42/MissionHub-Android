package com.missionhub.application;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.missionhub.application.Application.OnLowMemoryEvent;

/**
 * A generic object store for maintaining POJOs across contexts.
 */
public class ObjectStore {

	/** the logging tag */
	public static final String TAG = ObjectStore.class.getSimpleName();

	/** the singleton instance of the object store */
	private static ObjectStore mObjectStoreInstance;

	/** Synchronized map that holds the objects */
	private final Map<String, ObjectHolder> mObjectStore = Collections.synchronizedMap(new HashMap<String, ObjectHolder>());

	/** the default number of milliseconds to keep an object in the cache */
	private final long mDefaultExpires = 2 * 60 * 1000; // 2 minutes

	/**
	 * Creates a new object store
	 */
	private ObjectStore() {}

	/**
	 * Returns the singleton instance of the Object Store
	 * 
	 * @return
	 */
	public static ObjectStore getInstance() {
		if (mObjectStoreInstance == null) {
			mObjectStoreInstance = new ObjectStore();
			Application.registerEventSubscriber(mObjectStoreInstance, OnLowMemoryEvent.class);
		}
		return mObjectStoreInstance;
	}

	/**
	 * Stores and object and returns the key
	 * 
	 * @param mProvider
	 * @return
	 */
	public String storeObject(final Object object) {
		final String key = object.getClass().getName() + '@' + Integer.toHexString(object.hashCode());
		storeObject(key, object);
		return key;
	}

	/**
	 * Stores an object in the object store
	 * 
	 * @param key
	 * @param object
	 */
	public void storeObject(final String key, final Object object) {
		storeObject(key, object, mDefaultExpires);
	}

	/**
	 * Stores an object in the object store
	 * 
	 * @param key
	 * @param object
	 * @param expires
	 *            the number of milliseconds from current the object expires
	 */
	public void storeObject(final String key, final Object object, final long expires) {
		mObjectStore.put(key, new ObjectHolder(object, generateExpiration(expires)));
		purgeExpired();
	}

	/**
	 * Retrieves an object from the object store and removes it from the store to prevent memory leaks.
	 * 
	 * @param key
	 * @return
	 */
	public Object retrieveObject(final String key) {
		return retrieveObject(key, true);
	}

	/**
	 * Removes an object from the store with the option of not removing it.
	 * 
	 * @param key
	 * @param remove
	 * @return
	 */
	public Object retrieveObject(final String key, final boolean remove) {
		final ObjectHolder holder = mObjectStore.get(key);
		if (holder == null) return null;

		if (remove) {
			mObjectStore.remove(key);
		} else {
			// refresh the expires
			holder.expires = generateExpiration();
			mObjectStore.put(key, holder);
		}

		return holder.object;
	}

	/**
	 * Removed expired objects from the object store
	 */
	private void purgeExpired() {
		final Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				final Set<String> keys = mObjectStore.keySet();
				for (final String key : keys) {
					final ObjectHolder holder = mObjectStore.get(key);
					if (holder != null) {
						if (holder.expires < System.nanoTime()) {
							mObjectStore.remove(key);
						}
					}
				}
			}
		});
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}

	/**
	 * Generates the nanosecond time at which the object expired.
	 * 
	 * @return the nanosecond time in the future
	 */
	private long generateExpiration() {
		return generateExpiration(mDefaultExpires);
	}

	/**
	 * Generates the nanosecond time at which the object expired.
	 * 
	 * @param millis
	 *            the number of milliseconds from current.
	 * @return the nanosecond time in the future
	 */
	private long generateExpiration(final long millis) {
		return System.nanoTime() + (millis * 1000000);
	}

	/**
	 * Class to store an object and its expiration.
	 */
	private static class ObjectHolder {
		public long expires;
		public Object object;

		public ObjectHolder(final Object object, final long expires) {
			this.object = object;
			this.expires = expires;
		}
	}

	/**
	 * Responds to system low memory events by purging the expired objects
	 * 
	 * @param event
	 */
	public void onEvent(final OnLowMemoryEvent event) {
		purgeExpired();
	}
}