package com.missionhub.fragment;

import roboguice.inject.InjectView;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.missionhub.R;
import com.missionhub.activity.MainActivity;
import com.missionhub.application.Application;
import com.missionhub.application.ObjectStore;
import com.missionhub.application.Session;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.ui.ObjectArrayAdapter.ItemIdProvider;
import com.missionhub.ui.ObjectArrayAdapter.SupportEnable;
import com.missionhub.util.U;

public class MainMenuFragment extends BaseFragment implements OnItemClickListener {

	/** the list view to hold the menu items */
	@InjectView(R.id.list) private ListView mListView;

	/** the menu item adapter */
	private MainMenuAdapter mAdapter;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main_menu, null);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (savedInstanceState != null) {
			try {
				mAdapter = (MainMenuAdapter) ObjectStore.getInstance().retrieveObject(savedInstanceState.getString("mAdapter"));
			} catch (final Exception e) { /* ignore */}
		}
		if (mAdapter == null) {
			mAdapter = new MainMenuAdapter();

			mAdapter.add(new MainMenuItem(R.id.menu_item_dashboard, R.string.menu_dashboard));
			mAdapter.add(new MainMenuItem(R.id.menu_item_my_contacts, R.string.menu_my_contacts, R.drawable.ic_main_menu_card));
			mAdapter.add(new MainMenuItem(R.id.menu_item_all_contacts, R.string.menu_all_contacts, R.drawable.ic_main_menu_contacts));
			mAdapter.add(new MainMenuItem(R.id.menu_item_groups, R.string.menu_groups));
			mAdapter.add(new MainMenuItem(R.id.menu_item_surveys, R.string.menu_surveys, R.drawable.ic_main_menu_survey));

			mAdapter.add(new MainMenuDivider(R.string.menu_div_account));
			mAdapter.add(new MainMenuItem(R.id.menu_item_preferences, R.string.menu_preferences));
			mAdapter.add(new MainMenuItem(R.id.menu_item_logout, R.string.menu_logout));

			mAdapter.add(new MainMenuDivider(R.string.menu_div_missionhub));
			mAdapter.add(new MainMenuItem(R.id.menu_item_about, R.string.menu_about));
			mAdapter.add(new MainMenuItem(R.id.menu_item_help, R.string.menu_help_center));
		}
		mAdapter.setContext(getActivity());

		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		mAdapter.setContext(null);
		outState.putString("mAdapter", ObjectStore.getInstance().storeObject(mAdapter));
	}

	/**
	 * The menu adapter for the main menu
	 */
	public class MainMenuAdapter extends ObjectArrayAdapter {

		public MainMenuAdapter() {
			super(MainMenuFragment.this.getActivity());
		}

		@Override
		public View getSupportView(final int position, final View convertView, final ViewGroup parent) {

			final Object item = getItem(position);
			View view = convertView;

			ViewHolder holder = null;
			if (view == null) {
				final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				holder = new ViewHolder();
				if (item instanceof MainMenuItem) {
					view = inflater.inflate(R.layout.item_main_menu_item, null);
					holder.divider = view.findViewById(R.id.divider);
					holder.icon = (ImageView) view.findViewById(R.id.icon);
					holder.title = (TextView) view.findViewById(R.id.title);

				} else if (item instanceof MainMenuDivider) {
					view = inflater.inflate(R.layout.item_main_menu_divider, null);
					holder.title = (TextView) view.findViewById(R.id.title);
				}
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			if (item instanceof MainMenuItem) {
				if (position == 0) {
					holder.divider.setVisibility(View.GONE);
				} else {
					holder.divider.setVisibility(View.VISIBLE);
				}

				if (((MainMenuItem) item).iconResourceId == 0) {
					holder.icon.setVisibility(View.INVISIBLE);
				} else {
					holder.icon.setImageResource(((MainMenuItem) item).iconResourceId);
					holder.icon.setVisibility(View.VISIBLE);
				}
				if (U.isNullEmpty(((MainMenuItem) item).title)) {
					holder.title.setVisibility(View.GONE);
				} else {
					holder.title.setText(((MainMenuItem) item).title);
					holder.title.setVisibility(View.VISIBLE);
				}
			} else if (item instanceof MainMenuDivider) {
				if (U.isNullEmpty(((MainMenuDivider) item).title)) {
					holder.title.setVisibility(View.GONE);
				} else {
					holder.title.setText(((MainMenuDivider) item).title);
					holder.title.setVisibility(View.VISIBLE);
				}
			}

			return view;
		}

		@Override
		public View getSupportDropDownView(final int position, final View convertView, final ViewGroup parent) {
			return getSupportView(position, convertView, parent);
		}

		class ViewHolder {
			View divider;
			ImageView icon;
			TextView title;
		}
	}

	/**
	 * Represents a menu item
	 */
	public static class MainMenuItem implements ItemIdProvider {
		int id = 0;
		int iconResourceId = 0;
		String title;

		public MainMenuItem(final String title) {
			this(0, title, 0);
		}

		public MainMenuItem(final int id, final String title) {
			this(id, title, 0);
		}

		public MainMenuItem(final int id, final int title) {
			this(id, Application.getContext().getString(title), 0);
		}

		public MainMenuItem(final int id, final int title, final int iconResourceId) {
			this(id, Application.getContext().getString(title), iconResourceId);
		}

		public MainMenuItem(final int id, final String title, final int iconResourceId) {
			this.id = id;
			this.title = title;
			this.iconResourceId = iconResourceId;
		}

		@Override
		public long getItemId() {
			return id;
		}
	}

	/**
	 * Represents a menu divider
	 */
	public static class MainMenuDivider implements SupportEnable {
		String title;

		public MainMenuDivider() {}

		public MainMenuDivider(final int title) {
			this(Application.getContext().getString(title));
		}

		public MainMenuDivider(final String title) {
			this.title = title;
		}

		@Override
		public boolean isEnabled() {
			return false;
		}
	}

	public MainActivity getMainActivity() {
		return (MainActivity) getActivity();
	}

	@Override
	public void onItemClick(final AdapterView<?> adapter, final View view, final int position, final long id) {
		switch ((int) id) {
		case R.id.menu_item_dashboard:
			getMainActivity().switchFragment(DashboardFragment.class);
			break;
		case R.id.menu_item_my_contacts:
			getMainActivity().switchFragment(MyContactsFragment.class);
			break;
		case R.id.menu_item_all_contacts:
			getMainActivity().switchFragment(AllContactsFragment.class);
			break;
		case R.id.menu_item_groups:
			getMainActivity().switchFragment(GroupsFragment.class);
			break;
		case R.id.menu_item_surveys:
			getMainActivity().switchFragment(SurveysFragment.class);
			break;
		case R.id.menu_item_logout:
			Session.getInstance().logout();
			break;
		case R.id.menu_item_preferences:
			getMainActivity().openPreferences();
			break;
		case R.id.menu_item_about:
			getMainActivity().openAbout();
			break;
		case R.id.menu_item_help:
			getMainActivity().openHelp();
			break;
		}
		getMainActivity().showAbove();
	}
}