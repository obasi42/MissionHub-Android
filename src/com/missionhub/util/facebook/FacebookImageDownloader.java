package com.missionhub.util.facebook;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.missionhub.application.Application;
import com.missionhub.model.Person;
import com.nostra13.universalimageloader.core.download.URLConnectionImageDownloader;

public class FacebookImageDownloader extends URLConnectionImageDownloader {

	@Override
	public InputStream getStreamFromOtherSource(final URI uri) throws IOException {
		if (uri.getScheme().equalsIgnoreCase("fb")) {
			try {
				final GFQLPicCrop crop = FQL.getPicCrop(Integer.parseInt(uri.getHost()));
				return getStreamFromNetwork(URI.create(crop.uri));
			} catch (final Exception e) {
				/* ignore */
			}
		}
		return null;
	}

	public static void removeFromCache(final Person person) {
		if (person == null) return;

		// remove from memory cache
		final List<String> toRemove = new ArrayList<String>();
		for (final String key : Application.getImageLoader().getMemoryCache().keys()) {
			if (key.contains(String.valueOf(person.getFb_id()))) {
				toRemove.add(key);
			}
		}
		for (final String key : toRemove) {
			Application.getImageLoader().getMemoryCache().remove(key);
		}

		// remove from disk cache
		Application.getImageLoader().getDiscCache().get("fb://" + person.getFb_id()).delete();
		Application.getImageLoader().getDiscCache().get(person.getPicture()).delete();
	}
}