package com.missionhub.ui.widget;

import greendroid.widget.item.Item;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.missionhub.R;
import com.missionhub.api.model.sql.Person;
import com.missionhub.ui.widget.SelectableListView.SupportActivatable;
import com.missionhub.ui.widget.item.ContactListItem;
import com.missionhub.util.U;

public class ContactListItemView extends LinearLayout implements ListItemView, Checkable, SupportActivatable {

	private static Drawable mCheckOn;
	private static Drawable mCheckOff;

	private ImageView mPicture;
	private TextView mName;
	private TextView mStatus;
	private TextView mEmail;
	private TextView mPhone;
	private TextView mUpdated;
	private ImageView mCheckMark;
	private boolean mChecked = false;
	private boolean mActivated = false;

	public ContactListItemView(final Context context) {
		this(context, null);
	}

	public ContactListItemView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void prepareItemView() {
		if (mCheckOn == null) {
			mCheckOn = getResources().getDrawable(R.drawable.btn_check_on_normal_holo_light);
		}
		if (mCheckOff == null) {
			mCheckOff = getResources().getDrawable(R.drawable.btn_check_off_normal_holo_light);
		}

		mPicture = (ImageView) findViewById(R.id.picture);
		mName = (TextView) findViewById(R.id.name);
		mStatus = (TextView) findViewById(R.id.status);
		mEmail = (TextView) findViewById(R.id.email);
		mPhone = (TextView) findViewById(R.id.phone);
		mUpdated = (TextView) findViewById(R.id.updated);
		mCheckMark = (ImageView) findViewById(R.id.checkmark);
	}

	@Override
	public void setObject(final Item item) {
		setObject(item, null, -1);
	}

	@Override
	public void setObject(final Item item, final ViewGroup parent, final int position) {
		final ContactListItem listItem = (ContactListItem) item;

		final Person person = listItem.mPerson;

		if (!U.isNullEmpty(person.getName())) {
			mName.setText(person.getName());
		}

		if (!U.isNullEmpty(person.getStatus())) {
			mStatus.setText(person.getStatus());
			mStatus.setTextColor(R.color.dark_gray);
		} else {
			mStatus.setText("no status");
			mStatus.setTextColor(R.color.gray);
		}

		if (mEmail != null) {
			if (!U.isNullEmpty(person.getEmail_address())) {
				mEmail.setText(person.getEmail_address());
				mEmail.setVisibility(View.VISIBLE);
			} else {
				mEmail.setVisibility(View.GONE);
			}
		}

		if (mPhone != null) {
			if (!U.isNullEmpty(person.getPhone_number())) {
				mPhone.setText(person.getPhone_number());
				mPhone.setVisibility(View.VISIBLE);
			} else {
				mPhone.setVisibility(View.GONE);
			}
		}

		// TODO: implement
		if (mUpdated != null) {
			mUpdated.setText("2012/03/30");
			mPhone.setVisibility(View.VISIBLE);
		}

		if (((ListView) parent).isItemChecked(position)) {
			mCheckMark.setImageDrawable(mCheckOn);
		} else {
			mCheckMark.setImageDrawable(mCheckOff);
		}
	}

	@Override
	public Class<? extends Item> getItemClass() {
		return ContactListItem.class;
	}

	@Override
	public void toggle() {
		setChecked(!mChecked);
	}

	@Override
	public boolean isChecked() {
		return mChecked;
	}

	@Override
	public void setChecked(final boolean checked) {
		if (mChecked != checked) {
			mChecked = checked;
			refreshDrawableState();
		}
	}

	@Override
	protected int[] onCreateDrawableState(final int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 2);
		if (isChecked()) {
			mergeDrawableStates(drawableState, new int[] { android.R.attr.state_checked });
		}
		if (isSupportActivated()) {
			mergeDrawableStates(drawableState, new int[] { R.attr.state_support_activated });
		}
		return drawableState;
	}

	@Override
	public void setSupportActivated(final boolean activated) {
		mActivated = activated;
		refreshDrawableState();
	}

	@Override
	public boolean isSupportActivated() {
		return mActivated;
	}
}