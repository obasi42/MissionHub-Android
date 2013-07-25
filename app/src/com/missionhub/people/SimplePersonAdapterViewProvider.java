package com.missionhub.people;

import android.view.View;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.model.Person;
import com.missionhub.ui.AnimatedNetworkImageView;
import com.missionhub.ui.NetworkImageOnScrollListener;
import com.missionhub.ui.ObjectArrayAdapter;

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
            holder.avatar = (AnimatedNetworkImageView) view.findViewById(R.id.avatar);
            holder.text1 = (TextView) view.findViewById(android.R.id.text1);
            view.setTag(holder);
        } else {
            holder = (PersonViewHolder) view.getTag();
        }

        if (holder.avatar != null) {
            holder.avatar.setEmptyImageResId(R.drawable.ic_default_contact);
            holder.avatar.setDefaultImageResId(R.drawable.ic_default_contact);
            holder.avatar.setErrorImageResId(R.drawable.ic_default_contact);
            holder.avatar.setImageUrl(person.getPictureUrl(mAvatarSizePx, mAvatarSizePx), ((NetworkImageOnScrollListener.ImageLoaderProvider) parent).getImageLoader());
        }

        safeSet(holder.text1, person.getName());

        return view;
    }
}