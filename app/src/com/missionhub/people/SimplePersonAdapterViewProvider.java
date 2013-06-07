package com.missionhub.people;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.missionhub.R;
import com.missionhub.model.Person;
import com.missionhub.people.DynamicPeopleListProvider.LoadingItem;
import com.missionhub.ui.AdapterViewProvider;
import com.missionhub.ui.AnimateOnceImageLoadingListener;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.util.U;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.holoeverywhere.widget.TextView;

/**
 * Provides list item views for a simple person
 */
public class SimplePersonAdapterViewProvider implements AdapterViewProvider {

    /**
     * The size in pixels of the avatar
     */
    private final int mAvatarSizePx = Math.round(U.dpToPixel(50));
    /**
     * The {@link ImageLoader} display options for avatars
     */
    private DisplayImageOptions mImageLoaderOptions;
    /**
     * The loading listener for avatars
     */
    private AnimateOnceImageLoadingListener mImageLoaderListener;

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(ObjectArrayAdapter adapter, int position, View convertView, ViewGroup parent) {
        Object object = adapter.getItem(position);
        if (object instanceof Person) {
            return getPersonView(adapter, (Person) object, position, convertView, parent);
        } else if (object instanceof LoadingItem) {
            return getLoadingView(adapter, (LoadingItem) object, position, convertView, parent);
        }
        throw new RuntimeException("Unsupported list item type.");
    }

    /**
     * Responsible for the creation and layout of people list items
     *
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    public View getPersonView(ObjectArrayAdapter adapter, Person person, int position, View convertView, ViewGroup parent) {
        View view = convertView;
        PersonViewHolder holder;
        if (view == null) {
            holder = new PersonViewHolder();
            view = adapter.getLayoutInflater().inflate(R.layout.item_person_simple);
            holder.avatar = (ImageView) view.findViewById(R.id.avatar);
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (PersonViewHolder) view.getTag();
        }

        if (holder.avatar != null) {
            if (mImageLoaderOptions == null) {
                mImageLoaderOptions = U.getContactImageDisplayOptions();
            }
            if (mImageLoaderListener == null) {
                mImageLoaderListener = new AnimateOnceImageLoadingListener(250);
            }
            String url = U.getProfilePicture(person, mAvatarSizePx, mAvatarSizePx);
            ImageLoader.getInstance().displayImage(url, holder.avatar, mImageLoaderOptions, mImageLoaderListener);
        }

        if (holder.name != null) {
            if (!U.isNullEmpty(person.getName())) {
                holder.name.setText(person.getName());
                holder.name.setVisibility(View.VISIBLE);
            } else {
                holder.name.setVisibility(View.INVISIBLE);
            }
        }

        return view;
    }

    /**
     * Responsible for the creation and layout of the loading list item
     *
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    public View getLoadingView(ObjectArrayAdapter adapter, LoadingItem item, int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = adapter.getLayoutInflater().inflate(R.layout.item_progress, parent, false);
        }
        return convertView;
    }

    /**
     * View holder for person to optimize list view performance
     */
    public static class PersonViewHolder {
        ImageView avatar;
        TextView name;
    }
}