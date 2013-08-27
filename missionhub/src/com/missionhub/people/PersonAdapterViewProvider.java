package com.missionhub.people;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.model.Person;
import com.missionhub.people.DynamicPeopleListProvider.EmptyItem;
import com.missionhub.people.DynamicPeopleListProvider.LoadingItem;
import com.missionhub.ui.AdapterViewProvider;
import com.missionhub.ui.AnimatedNetworkImageView;
import com.missionhub.ui.NetworkImageOnScrollListener;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.util.DisplayUtils;

import org.apache.commons.lang3.StringUtils;
import org.holoeverywhere.widget.TextView;

/**
 * Provides list item views for a simple person
 */
public class PersonAdapterViewProvider implements AdapterViewProvider {

    /**
     * The current view context
     */
    private Context mContext;

    /**
     * The size in pixels of the avatar
     */
    protected final int mAvatarSizePx = Math.round(DisplayUtils.dpToPixel(50));

    /**
     * The valid display options
     */
    public static enum Display {
        NAME, STATUS, GENDER, EMAIL, PHONE, PERMISSION, LABELS
    }

    /**
     * The current line 2 display option
     */
    private Display mLine2 = Display.STATUS;

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
        } else if (object instanceof EmptyItem) {
            return getEmptyView(adapter, (EmptyItem) object, position, convertView, parent);
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
            view = adapter.getLayoutInflater().inflate(R.layout.item_person, parent, false);
            holder.avatar = (AnimatedNetworkImageView) view.findViewById(R.id.avatar);
            holder.text1 = (TextView) view.findViewById(android.R.id.text1);
            holder.text2 = (TextView) view.findViewById(android.R.id.text2);
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

        holder.text2.setOnClickListener(null);
        holder.text2.setClickable(false);
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
                if (mCache.emailClickListener != null) {
                    holder.text2.setOnClickListener(mCache.emailClickListener);
                    holder.text2.setClickable(true);
                }
                break;
            case PHONE:
                safeSet(holder.text2, mCache.phone);
                if (mCache.phoneClickListener != null) {
                    holder.text2.setOnClickListener(mCache.phoneClickListener);
                    holder.text2.setClickable(true);
                }
                break;
            case PERMISSION:
                safeSet(holder.text2, mCache.permission);
                break;
            case LABELS:
                safeSet(holder.text2, mCache.labels);
                break;
            default:
                safeSet(holder.text2, null);
        }

        return view;
    }

    /**
     * Sets the text of a text field or sets it empty
     *
     * @param view
     * @param text
     */
    protected void safeSet(TextView view, CharSequence text) {
        if (StringUtils.isNotEmpty(text)) {
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
     * Responsible for the creation and layout of the empty list item
     *
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    public View getEmptyView(ObjectArrayAdapter adapter, EmptyItem item, int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = adapter.getLayoutInflater().inflate(R.layout.item_empty, parent, false);
        }
        return convertView;
    }

    /**
     * View holder for person to optimize list view performance
     */
    public static class PersonViewHolder {
        AnimatedNetworkImageView avatar;
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
     *
     * @param line2
     */
    public void setLine2(Display line2) {
        mLine2 = line2;
    }

}