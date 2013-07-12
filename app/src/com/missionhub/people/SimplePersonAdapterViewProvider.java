package com.missionhub.people;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.missionhub.R;
import com.missionhub.model.Person;
import com.missionhub.ui.AnimateOnceImageLoadingListener;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.util.DisplayUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.holoeverywhere.widget.TextView;

/**
 * Provides list item views for a simple person
 */
public class SimplePersonAdapterViewProvider extends PersonAdapterViewProvider {

    @Override
    public View getPersonView(ObjectArrayAdapter adapter, final Person person, int position, View convertView, ViewGroup parent) {
        View view = convertView;
        PersonViewHolder holder;
        if (view == null) {
            holder = new PersonViewHolder();
            view = adapter.getLayoutInflater().inflate(R.layout.item_person_simple, parent, false);
            holder.avatar = (ImageView) view.findViewById(R.id.avatar);
            holder.text1 = (TextView) view.findViewById(android.R.id.text1);
            view.setTag(holder);
        } else {
            holder = (PersonViewHolder) view.getTag();
        }

        if (holder.avatar != null) {
            if (mImageLoaderOptions == null) {
                mImageLoaderOptions = DisplayUtils.getContactImageDisplayOptions();
            }
            if (mImageLoaderListener == null) {
                mImageLoaderListener = new AnimateOnceImageLoadingListener(250);
            }
            ImageLoader.getInstance().displayImage(person.getPictureUrl(mAvatarSizePx, mAvatarSizePx), holder.avatar, mImageLoaderOptions, mImageLoaderListener);
        }

        safeSet(holder.text1, person.getName());

        return view;
    }
}