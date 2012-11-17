package com.missionhub.application;

import java.util.WeakHashMap;

import android.graphics.drawable.Drawable;

import com.missionhub.application.Application.OnLowMemoryEvent;

/**
 * A simple cache for drawables that responds to memory events
 */
public class DrawableCache {

	/** singleton instance of the cache */
	private static DrawableCache sDrawableCache;

	/** hashmap that contains the cached drawables */
	private WeakHashMap<Integer, Drawable> mCache;

	/** returns or creates the instance of the cache */
	public synchronized static DrawableCache getInstance() {
		if (sDrawableCache == null) {
			sDrawableCache = new DrawableCache();
			sDrawableCache.mCache = new WeakHashMap<Integer, Drawable>();

			Application.registerEventSubscriber(sDrawableCache, OnLowMemoryEvent.class);
		}
		return sDrawableCache;
	}

	/** called when an OnLowMemoryEvent is posted to the EventBus */
	public void onEvent(final OnLowMemoryEvent event) {
		mCache.clear();
	}

	/** returns the drawable by the resource id */
	public static Drawable getDrawable(final int resource) {
		Drawable drawable = getInstance().mCache.get(resource);
		if (drawable == null) {
			drawable = Application.getContext().getResources().getDrawable(resource);
			getInstance().mCache.put(resource, drawable);
		}
		return drawable;
	}

}