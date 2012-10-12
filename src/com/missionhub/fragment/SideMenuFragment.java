package com.missionhub.fragment;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.missionhub.R;
import com.missionhub.ui.ItemAdapter;
import com.missionhub.ui.item.SideMenuItem;

public class SideMenuFragment extends RoboSherlockFragment {

	@InjectView(R.id.listview) ListView mListView;
	private ItemAdapter mAdapter;
	private SideMenu mSideMenu;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		inflater = LayoutInflater.from(getActivity());
		return inflater.inflate(R.layout.fragment_side_menu, null);
	}

	@Override
	public void onViewCreated(final View v, final Bundle savedInstanceState) {
		super.onViewCreated(v, savedInstanceState);
		mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				((SideMenuProvider) getActivity()).onSideMenuItemSelected((SideMenuItem) parent.getAdapter().getItem(position));
			}
		});
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);

		if (activity instanceof SideMenuProvider) {
			mSideMenu = new SideMenu();
			mAdapter = new ItemAdapter(activity);
			((SideMenuProvider) activity).onCreateSideMenu(mSideMenu);
			updateAdapter();
		}
	}

	private void updateAdapter() {
		for (final SideMenuItem item : mSideMenu.getItems()) {
			mAdapter.add(item);
		}
	}

	public static class SideMenu {

		List<SideMenuItem> mItems = new ArrayList<SideMenuItem>();

		private SideMenu() {}

		public void addItem(final SideMenuItem item) {
			mItems.add(item);
		}

		public void addItem(final SideMenuItem item, final int position) {
			mItems.add(position, item);
		}

		public boolean removeItem(final SideMenuItem item) {
			return mItems.remove(item);
		}

		public boolean removeItemById(final int id) {
			for (int i = 0; i < mItems.size(); i++) {
				final SideMenuItem item = mItems.get(i);
				if (item.id == id) {
					mItems.remove(i);
					return true;
				}
			}
			return false;
		}

		protected List<SideMenuItem> getItems() {
			return mItems;
		}
	}

	public interface SideMenuProvider {
		public void onCreateSideMenu(SideMenu menu);

		public void onSideMenuItemSelected(SideMenuItem item);
	}

}