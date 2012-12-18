package com.missionhub.contactlist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.missionhub.R;
import com.missionhub.application.DrawableCache;
import com.missionhub.model.Person;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.util.U;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ContactListAdapter extends ObjectArrayAdapter {

	private final int mContactItemLayout;

	private final int mProgressItemLayout;

	private final DisplayImageOptions mImageLoaderOptions;

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

		mImageLoaderOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.default_contact).showStubImage(R.drawable.default_contact).cacheInMemory().cacheOnDisc().build();
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
					if (!U.isNullEmpty(item.person.getPicture())) {
						ImageLoader.getInstance().displayImage(item.person.getPicture(), holder.avatar, mImageLoaderOptions);
					} else {
						holder.avatar.setImageDrawable(DrawableCache.getDrawable(R.drawable.default_contact));
					}
				}

				if (holder.name != null) {
					if (!U.isNullEmpty(item.person.getName())) {
						holder.name.setText(item.person.getName());
					} else {
						holder.name.setText("");
					}
				}

				if (holder.status != null) {
					//TODO: show status
//					if (!U.isNullEmpty(item.person.getStatus())) {
//						holder.status.setText(U.translateStatus(item.person.getStatus()));
//					} else {
//						holder.status.setText("");
//					}
				}

				if (holder.gender != null) {
					//TODO: show gender
//					if (!U.isNullEmpty(item.person.getGender())) {
//						holder.gender.setText(U.translateStatus(item.person.getGender()));
//					} else {
//						holder.gender.setText("");
//					}
				}

				if (holder.email != null) {
					item.person.getEmailAddressList();
					// TODO: show email address
//					
//					if (!U.isNullEmpty(item.person.getEmail_address())) {
//						holder.email.setText(U.translateStatus(item.person.getEmail_address()));
//					} else {
//						holder.email.setText("");
//					}
				}

				if (holder.phone != null) {
					//TODO: show phone number
//					if (!U.isNullEmpty(item.person.getPhone_number())) {
//						holder.phone.setText(U.translateStatus(item.person.getPhone_number()));
//					} else {
//						holder.phone.setText("");
//					}
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

		public ProgressItem() {}

		public ProgressItem(final String text) {
			this.text = text;
		}
	}
}