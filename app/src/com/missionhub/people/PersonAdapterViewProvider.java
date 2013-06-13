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
public class PersonAdapterViewProvider implements AdapterViewProvider {

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
     * The valid display options
     */
    public static enum Display {
        NAME, STATUS, GENDER, EMAIL, PHONE, PERMISSION, DATE_CREATED
    }

    /**
     * The current line 2 display option
     */
    private Display mLine2 = Display.GENDER;

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
    public View getPersonView(ObjectArrayAdapter adapter, final Person person, int position, View convertView, ViewGroup parent) {
        View view = convertView;
        PersonViewHolder holder;
        if (view == null) {
            holder = new PersonViewHolder();
            view = adapter.getLayoutInflater().inflate(R.layout.item_person);
            holder.avatar = (ImageView) view.findViewById(R.id.avatar);
            holder.text1 = (TextView) view.findViewById(android.R.id.text1);
            holder.text2 = (TextView) view.findViewById(android.R.id.text2);
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

        safeSet(holder.text1, person.getName());

        holder.text2.setOnClickListener(null);
        Person.PersonViewCache mCache = person.getViewCache();

        switch (mLine2) {
            case STATUS:
                safeSet(holder.text2, mCache.status);
                break;
            case GENDER:
                safeSet(holder.text2, mCache.gender);
                break;
            case EMAIL:
                safeSet(holder.text2, mCache.email);
                holder.text2.setOnClickListener(mCache.emailClickListener);
                break;
            case PHONE:
                safeSet(holder.text2, mCache.phone);
                holder.text2.setOnClickListener(mCache.phoneClickListener);
                break;
            case PERMISSION:
                safeSet(holder.text2, mCache.permission);
                break;
            case DATE_CREATED:
                safeSet(holder.text2, mCache.dateCreated);
                break;
            default:
                safeSet(holder.text2, null);
        }

        return view;
    }

    /**
     * Sets the text of a text field or sets it empty
     * @param view
     * @param text
     */
    private void safeSet(TextView view, CharSequence text) {
        if (!U.isNullEmpty(text)) {
            view.setText(text);
        } else {
            view.setText("");
        }
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
    private static class PersonViewHolder {
        ImageView avatar;
        TextView text1;
        TextView text2;
    }

    /**
     * @return The line 2 display parameter
     */
    public Display getLine2() {
        return mLine2;
    }

    /**
     * Sets the line 2 display parameter
     * @param line2
     */
    public void setLine2(Display line2) {
        mLine2 = line2;
    }
}