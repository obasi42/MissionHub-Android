package com.missionhub.contactlist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.missionhub.R;
import com.missionhub.application.DrawableCache;
import com.missionhub.model.Person;
import com.missionhub.ui.AnimateOnceImageLoadingListener;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.util.U;
import com.missionhub.util.U.Gender;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

public class ContactListAdapter extends ObjectArrayAdapter {

    private final int mContactItemLayout;

    private final int mProgressItemLayout;

    private final DisplayImageOptions mImageLoaderOptions;

    private final AnimateOnceImageLoadingListener mImageLoaderListener;

    private final int mAvatarSizePx = Math.round(U.dpToPixel(50));

    public ContactListAdapter(final Context context) {
        this(context, R.layout.item_contact);
    }

    public ContactListAdapter(final Context context, final int contactItemLayout) {
        this(context, contactItemLayout, R.layout.item_contact_progress);
    }

    public ContactListAdapter(final Context context, final int contactItemLayout, final int progressItemLayout) {
        super(context);
        mContactItemLayout = contactItemLayout;
        mProgressItemLayout = progressItemLayout;

        mImageLoaderOptions = U.getContactImageDisplayOptions();
        mImageLoaderListener = new AnimateOnceImageLoadingListener(250);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(final int position) {
        try {
            final Object object = getItem(position);
            if (object instanceof ContactItem) {
                return ((ContactItem) object).person.getId();
            }
        } catch (final Exception e) { /* ignore */}
        return super.getItemId(position);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final Object object = getItem(position);
        View view = convertView;

        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();

            if (object instanceof ContactItem) {
                view = getLayoutInflater().inflate(mContactItemLayout, null);
                holder.checkmark = (ImageView) view.findViewById(R.id.checkmark);
                holder.avatar = (ImageView) view.findViewById(R.id.avatar);
                holder.name = (TextView) view.findViewById(R.id.name);
                holder.status = (TextView) view.findViewById(R.id.status);
                holder.gender = (TextView) view.findViewById(R.id.gender);
                holder.email = (TextView) view.findViewById(R.id.email);
                holder.phone = (TextView) view.findViewById(R.id.phone);
            } else if (object instanceof ProgressItem) {
                view = getLayoutInflater().inflate(mProgressItemLayout, null);
                holder.progressText = (TextView) view.findViewById(R.id.progress_text);
            }

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (object instanceof ContactItem) {
            final ContactItem item = (ContactItem) object;

            if (holder.checkmark != null) {
                if (((ListView) parent).isItemChecked(position)) {
                    holder.checkmark.setImageDrawable(DrawableCache.getDrawable(R.drawable.check_on_normal_holo_light));
                } else {
                    holder.checkmark.setImageDrawable(DrawableCache.getDrawable(R.drawable.check_off_normal_holo_light));
                }
            }

            if (item.person != null) {

                if (holder.avatar != null) {
                    String url = U.getProfilePicture(item.person, mAvatarSizePx, mAvatarSizePx);
                    ImageLoader.getInstance().displayImage(url, holder.avatar, mImageLoaderOptions, mImageLoaderListener);
                }

                if (holder.name != null) {
                    if (!U.isNullEmpty(item.person.getName())) {
                        holder.name.setText(item.person.getName());
                    } else {
                        holder.name.setText("");
                    }
                }

                if (holder.status != null) {
                    if (!U.isNullEmpty(item.person.getStatus())) {
                        holder.status.setText(item.person.getStatus().toString());
                    } else {
                        holder.status.setText("");
                    }
                }

                if (holder.gender != null) {
                    if (!U.isNullEmpty(item.person.getGender())) {
                        holder.gender.setText(Gender.valueOf(item.person.getGender()).toString());
                    } else {
                        holder.gender.setText("");
                    }
                }

                if (holder.email != null) {
                    if (item.person.getPrimaryEmailAddress() != null) {
                        holder.email.setText(item.person.getPrimaryEmailAddress().getEmail());
                    } else {
                        holder.email.setText("");
                    }
                }

                if (holder.phone != null) {
                    if (item.person.getPrimaryPhoneNumber() != null) {
                        holder.phone.setText(item.person.getPrimaryPhoneNumber().getNumber());
                    } else {
                        holder.phone.setText("");
                    }
                }
            }
        } else if (object instanceof ProgressItem) {
            final ProgressItem item = (ProgressItem) object;

            if (holder.progressText != null) {
                if (!U.isNull(item.text)) {
                    holder.progressText.setText(item.text);
                } else {
                    holder.progressText.setText(R.string.loading);
                }
            }
        }

        return view;
    }

    private static class ViewHolder {
        ImageView avatar;
        TextView name;
        TextView status;
        TextView gender;
        TextView email;
        TextView phone;
        ImageView checkmark;
        TextView progressText;
    }

    public static class ContactItem {
        public Person person;

        public ContactItem(final Person person) {
            this.person = person;
        }
    }

    public static class ProgressItem {
        public String text;

        public ProgressItem() {
        }

        public ProgressItem(final String text) {
            this.text = text;
        }
    }
}