package com.missionhub.util.facebook;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.missionhub.model.Person;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.URLConnectionImageDownloader;

public class FacebookImageDownloader extends URLConnectionImageDownloader {

	@Override
	public InputStream getStreamFromNetwork(final URI uri) throws IOException {
		// bail before trying to download non-existent picture
		if (uri.toASCIIString().equalsIgnoreCase("http://graph.facebook.com//picture")) {
			return null;
		}
		return super.getStreamFromNetwork(uri);
	}

	@Override
	public InputStream getStreamFromOtherSource(final URI uri) throws IOException {
		if (uri.getScheme().equalsIgnoreCase("fb")) {
			try {
				final GFQLPicCrop crop = FQL.getPicCrop(Integer.parseInt(uri.getHost()));
				return getStreamFromNetwork(URI.create(crop.uri));
			} catch (final Exception e) {
				/* ignore */
			}
		} else if (uri.getScheme().equals("fb_square")) {
			return getStreamFromNetwork(URI.create("http://graph.facebook.com/" + uri.getHost() + "/picture?type=square"));
		}
		return null;
	}

	public static void removeFromCache(final Person person) {
		if (person == null) return;

		try {
			// remove from memory cache
			final List<String> toRemove = new ArrayList<String>();
			for (final String key : ImageLoader.getInstance().getMemoryCache().keys()) {
				if (key.contains(String.valueOf(person.getFb_uid()))) {
					toRemove.add(key);
				}
			}
			for (final String key : toRemove) {
				ImageLoader.getInstance().getMemoryCache().remove(key);
			}

			// remove from disk cache
			ImageLoader.getInstance().getDiscCache().get("fb://" + person.getFb_uid()).delete();
			ImageLoader.getInstance().getDiscCache().get("fb_square://" + person.getFb_uid()).delete();
		} catch (final Exception e) { /* ignore */}
	}
}