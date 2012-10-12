package com.missionhub.ui.itemview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.missionhub.R;
import com.missionhub.application.DrawableCache;
import com.missionhub.model.Person;
import com.missionhub.ui.item.ContactListItem;
import com.missionhub.ui.item.Item;
import com.missionhub.ui.widget.SelectableListView.SupportActivatable;
import com.missionhub.util.U;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ContactListItemView extends LinearLayout implements LayoutItemView, SupportActivatable, Checkable {

	private int mLayoutId;

	private ImageView mPicture;
	private TextView mName;
	private TextView mStatus;
	private ImageView mCheckMark;

	private boolean mChecked = false;
	private boolean mActivated = false;
	
	private DisplayImageOptions mImageOptions = new DisplayImageOptions.Builder()
	.showStubImage(R.drawable.default_contact)
	.cacheInMemory()
	.cacheOnDisc()
	.build();

	public ContactListItemView(final Context context) {
		this(context, null, -1);
	}

	public ContactListItemView(final Context context, final AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public ContactListItemView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);		
	}

	@Override
	public void prepareItemView() {
		mPicture = (ImageView) findViewById(R.id.picture);
		mName = (TextView) findViewById(R.id.name);
		mStatus = (TextView) findViewById(R.id.status);
		mCheckMark = (ImageView) findViewById(R.id.checkmark);
	}

	@Override
	public void setObject(final Item item, final ViewGroup parent, final int position) {
		final Person person = ((ContactListItem) item).person;
		if (person != null) {
			
			if (mPicture != null) {
				if (!U.isNullEmpty(person.getPicture())) {
					ImageLoader.getInstance().displayImage(person.getPicture(), mPicture, mImageOptions);
				} else {
					mPicture.setImageDrawable(DrawableCache.getDrawable(R.drawable.default_contact));
				}
			}

			if (mName != null) {
				if (!U.isNullEmpty(person.getName())) {
					mName.setVisibility(View.VISIBLE);
					mName.setText(person.getName());
				} else {
					mName.setVisibility(View.GONE);
				}
			}

			if (mStatus != null) {
				if (!U.isNullEmpty(person.getStatus())) {
					mStatus.setVisibility(View.VISIBLE);
					mStatus.setText(person.getStatus());
					mStatus.setTextColor(getResources().getColor(R.color.dark_gray));
				} else {
					mStatus.setVisibility(View.GONE);
					mStatus.setText("no status");
					mStatus.setTextColor(getResources().getColor(R.color.gray));
				}
			}

			if (mCheckMark != null) {
				if (((ListView) parent).isItemChecked(position)) {
					mCheckMark.setImageDrawable(DrawableCache.getDrawable(R.drawable.btn_check_on_normal_holo_light));
				} else {
					mCheckMark.setImageDrawable(DrawableCache.getDrawable(R.drawable.btn_check_off_holo_light));
				}
			}
		}
	}

	@Override
	public Class<? extends Item> getItemClass() {
		return ContactListItem.class;
	}

	@Override
	public int getLayoutId() {
		return mLayoutId;
	}

	@Override
	public void setLayoutId(final int id) {
		mLayoutId = id;
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