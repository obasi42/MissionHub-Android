package com.missionhub.ui.widget;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

import com.missionhub.R;
import com.missionhub.api.model.sql.Person;
import com.missionhub.ui.widget.item.ContactListItem;
import com.missionhub.util.U;

public class ContactListItemView extends LinearLayout implements ItemView, OnClickListener, Checkable {
	
	private ContactListItem mListItem;
	private ImageView mPicture;
	private TextView mName;
	private TextView mStatus;
	private TextView mEmail;
	private TextView mPhone;
	private TextView mUpdated;
	private ImageView mCheckMark;
	private View mCheckMarkContainer;
	private boolean mChecked = false;

	public ContactListItemView(final Context context) {
		this(context, null);
	}

	public ContactListItemView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void prepareItemView() {
		mPicture = (ImageView) findViewById(R.id.picture);
		mName = (TextView) findViewById(R.id.name);
		mStatus = (TextView) findViewById(R.id.status);
		mEmail = (TextView) findViewById(R.id.email);
		mPhone = (TextView) findViewById(R.id.phone);
		mUpdated = (TextView) findViewById(R.id.updated);
		mCheckMark = (ImageView) findViewById(R.id.checkmark);
		mCheckMarkContainer = (View) findViewById(R.id.checkmark_container);
	}

	@Override
	public void setObject(final Item object) {
		mListItem = (ContactListItem) object;

		final Person person = mListItem.mPerson;
		
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
		
		//TODO: implement
		if (mUpdated != null) {
			mUpdated.setText("2012/03/30");
			mPhone.setVisibility(View.VISIBLE);
		}
		
		mCheckMarkContainer.setOnClickListener(this);
	}
	

	@Override
	public Class<? extends Item> getItemClass() {
		return ContactListItem.class;
	}
	
	@Override
	public void onClick(View v) {
		toggle();
	}

	@Override
	public boolean isChecked() {
		return mChecked;
	}

	@Override
	public void setChecked(final boolean checked) {
		mChecked = checked;
		
		if (mChecked) {
			Toast.makeText(getContext(), "Checked", Toast.LENGTH_SHORT).show();
			
			mCheckMark.setImageResource(R.drawable.btn_check_on_normal_holo_light);
		} else {
			
			Toast.makeText(getContext(), "Unchecked", Toast.LENGTH_SHORT).show();
			mCheckMark.setImageResource(R.drawable.btn_check_off_normal_holo_light);
		}
	}

	@Override
	public void toggle() {
		if (mChecked) {
			setChecked(false);
		} else {
			setChecked(true);
		}
	}
}