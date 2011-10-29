package com.missionhub.ui.widget;

import com.missionhub.R;
import com.missionhub.ui.widget.item.ContactAboutItem;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactAboutItemView extends LinearLayout implements ItemView {

	private TextView mTextView;
	private TextView mSubtextView;
	private ImageView mImageView;

	public ContactAboutItemView(Context context) {
		this(context, null);
	}

	public ContactAboutItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void prepareItemView() {
		mTextView = (TextView) findViewById(R.id.text);
		mSubtextView = (TextView) findViewById(R.id.subtext);
		mImageView = (ImageView) findViewById(R.id.icon);
	}

	@Override
	public void setObject(Item object) {
		final ContactAboutItem item = (ContactAboutItem) object;
		if (item.text != null) {
			mTextView.setText(item.text);
			mTextView.setVisibility(View.VISIBLE);
		} else {
			mTextView.setVisibility(View.GONE);
		}
		if (item.subtext != null) {
			mSubtextView.setText(item.subtext);
			mSubtextView.setVisibility(View.VISIBLE);
		} else {
			mSubtextView.setVisibility(View.GONE);
		}
		if (item.icon != null) {
			mImageView.setImageDrawable(item.icon);
			mImageView.setVisibility(View.VISIBLE);
		} else {
			mImageView.setVisibility(View.GONE);
		}
		
		// seemingly backward logic so that the itemview handles the click 
		// rather than the listview as to not show a selection when pressed
		if (item.action == null) {
			setClickable(true);
		} else {
			setClickable(false);
		}
	}
}
