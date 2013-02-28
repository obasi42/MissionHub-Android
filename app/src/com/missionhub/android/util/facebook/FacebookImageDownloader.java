package com.missionhub.android.util.facebook;

import android.content.Context;
import com.missionhub.android.model.Person;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class FacebookImageDownloader extends BaseImageDownloader {

    public FacebookImageDownloader(Context context) {
        super(context);
    }

    @Override
    public InputStream getStreamFromNetwork(final URI uri, final Object extra) throws IOException {
        return super.getStreamFromNetwork(uri, extra);
    }

    @Override
    public InputStream getStreamFromOtherSource(final URI uri, final Object extra) throws IOException {
        if (uri.getScheme().equalsIgnoreCase("fb")) {
            try {
                final GFQLPicCrop crop = FQL.getPicCrop(Integer.parseInt(uri.getHost()));
                return getStreamFromNetwork(URI.create(crop.uri), extra);
            } catch (final Exception e) {
                /* ignore */
            }
        } else if (uri.getScheme().equals("fb_square")) {
            return getStreamFromNetwork(URI.create("http://graph.facebook.com/" + uri.getHost() + "/picture?type=square"), extra);
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