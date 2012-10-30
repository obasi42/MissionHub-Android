package com.missionhub.fragment;

public class MyContactsFragment extends ContactListFragment {
	
}








//package com.missionhub.fragment;
//
//import roboguice.inject.InjectView;
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewGroup.LayoutParams;
//import android.widget.AbsListView;
//import android.widget.FrameLayout;
//
//import com.actionbarsherlock.app.ActionBar;
//import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
//import com.missionhub.R;
//import com.missionhub.api.Api.ContactListOptions;
//import com.missionhub.ui.ItemAdapter;
//import com.missionhub.ui.item.SimpleSpinnerItem;
//import com.missionhub.ui.widget.ContactListView;
//
//public class MyContactsFragment extends BaseFragment implements OnNavigationListener {
//
//	@InjectView(R.id.fragment_my_contacts_container) FrameLayout mContainer;
//	private ContactListView mContactList;
//
//	private int mCurrentNavItem = -1;
//
//	@Override
//	public void onAttach(final Activity activity) {
//		super.onAttach(activity);
//
//		buildNavigation();
//	}
//
//	@Override
//	public void onCreate(final Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setRetainInstance(true);
//
//		if (savedInstanceState != null) {
//			mCurrentNavItem = savedInstanceState.getInt("mCurrentNavItem", -1);
//			getSherlockActivity().getSupportActionBar().setSelectedNavigationItem(mCurrentNavItem);
//		}
//	}
//
//	@Override
//	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
//		return inflater.inflate(R.layout.fragment_my_contacts, container, false);
//	}
//
//	@Override
//	public void onViewCreated(final View view, final Bundle savedInstanceState) {
//		super.onViewCreated(view, savedInstanceState);
//
//		// initialize the contact list if null
//		if (mContactList == null) {
//			mContactList = new ContactListView(view.getContext());
//			mContactList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
//		}
//
//		mContainer.addView(mContactList, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//	}
//
//	@Override
//	public void onDestroyView() {
//		mContainer.removeView(mContactList);
//		super.onDestroyView();
//	}
//
//	@Override
//	public void onDestroy() {
//		mContactList = null;
//		super.onDestroy();
//	}
//
//	@Override
//	public void onSaveInstanceState(final Bundle savedInstanceState) {
//		super.onSaveInstanceState(savedInstanceState);
//		savedInstanceState.putInt("mCurrentNavItem", mCurrentNavItem);
//	}
//
//	private void buildNavigation() {
//		final ItemAdapter adapter = new ItemAdapter(getActivity());
//		adapter.add(new SimpleSpinnerItem("All"));
//		adapter.add(new SimpleSpinnerItem("In-Progress"));
//		adapter.add(new SimpleSpinnerItem("Completed"));
//
//		getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(Boolean.FALSE);
//		getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//		getSherlockActivity().getSupportActionBar().setListNavigationCallbacks(adapter, this);
//	}
//
//	@Override
//	public boolean onNavigationItemSelected(final int itemPosition, final long itemId) {
//		if (mContactList != null && mCurrentNavItem != itemPosition) {
//			mCurrentNavItem = itemPosition;
//
//			final ContactListOptions options = new ContactListOptions();
//			options.setFilter("assigned_to", "me");
//			switch (itemPosition) {
//			case 0:
//				// no further options
//				break;
//			case 1:
//				options.setFilter("status", "uncontacted");
//				options.addFilter("status", "attempted_contact");
//				options.addFilter("status", "contacted");
//				break;
//			case 2:
//				options.setFilter("status", "completed");
//				break;
//			}
//			//mContactList.setContactListOptions(options);
//			return true;
//		}
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//}