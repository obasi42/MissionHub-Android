package com.missionhub.ui.itemview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.application.SettingsManager;
import com.missionhub.model.Organization;
import com.missionhub.ui.item.AccountItem;
import com.missionhub.ui.item.Item;

/**
 * Views for use in PickAccountDialogFragment
 */
public class AccountItemView extends FrameLayout implements ItemView {

	private TextView mName;
	private TextView mOrganization;

	public AccountItemView(final Context context) {
		this(context, null);
	}

	public AccountItemView(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AccountItemView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void prepareItemView() {
		mName = (TextView) findViewById(R.id.name);
		mOrganization = (TextView) findViewById(R.id.organization);
	}

	@Override
	public void setObject(final Item item, final ViewGroup parent, final int position) {
		final AccountItem a = (AccountItem) item;
		if (a.person != null) {
			mName.setText(a.person.getName());
			mName.setVisibility(View.VISIBLE);
			final Organization org = Application.getDb().getOrganizationDao().load(SettingsManager.getSessionOrganizationId(a.person.getId()));
			if (org != null) {
				mOrganization.setText(org.getName());
				mOrganization.setVisibility(View.VISIBLE);
			} else {
				mOrganization.setVisibility(View.GONE);
			}
		} else {
			mName.setVisibility(View.GONE);
		}
	}

	@Override
	public Class<? extends Item> getItemClass() {
		return AccountItem.class;
	}

	public static class NewAccountItemView extends FrameLayout implements ItemView {

		public NewAccountItemView(final Context context) {
			this(context, null);
		}

		public NewAccountItemView(final Context context, final AttributeSet attrs) {
			this(context, attrs, 0);
		}

		public NewAccountItemView(final Context context, final AttributeSet attrs, final int defStyle) {
			super(context, attrs, defStyle);
		}

		@Override
		public void prepareItemView() {}

		@Override
		public void setObject(final Item item, final ViewGroup parent, final int position) {}

		@Override
		public Class<? extends Item> getItemClass() {
			return AccountItem.NewAccountItem.class;
		}

	}

}