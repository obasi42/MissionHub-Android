package com.missionhub.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnFocusChangeListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.missionhub.R;
import com.missionhub.fragment.ContactListFragment;

public class SearchMenuItemHelper implements OnQueryTextListener, OnFocusChangeListener {
	
	/** the logging tag */
	public static final String TAG = SearchMenuItemHelper.class.getSimpleName();
	
	/** the search interface */
	private SearchMenuItemListener mListener;
	
	/** handler to delay text change listener calls */
	private Handler mHandler = new Handler();
	
	/** the search view */
	private SearchView mSearchView;
	
	/** the submenu */
	private SubMenu mSearchMenu;
	
	/** the search menu item */
	private MenuItem mQuickSearchItem;
	
	/** the clear search menu item */
	private MenuItem mClearSearchItem;
	
	/** the interface delay in millis */
	private long mDelay = 300;
	
	/** the current search query */
	private String mQuery;
	
//	/** the quick search expanded state */
//	private boolean mStateQuickSearchExpanded = false;
	
	private ContactListFragment mFragment;
	
	public SearchMenuItemHelper(ContactListFragment fragment) {
		mFragment = fragment;
		
		if (fragment instanceof SearchMenuItemListener) {
			mListener = (SearchMenuItemListener) fragment;
		}
	}
	
	public void onSaveInstanceState(Bundle outState) {
//		Log.e(TAG, "onSaveInstanceState");
//		
//		if (mSearchView != null) {
//			Log.e(TAG, "mSearchView NOT NULL");
//		}
//		
//		if (mSearchView != null) outState.putString("mQuery", String.valueOf(mSearchView.getQuery()));
//		if (mQuickSearchItem != null) outState.putBoolean("mStateQuickSearchExpanded", mQuickSearchItem.isActionViewExpanded());
	}
	
	public void onRestoreInstanceState(Bundle savedInstanceState) {
//		Log.e(TAG, "onRestoreInstanceState outside");
//		
//		if (savedInstanceState != null) {
//			
//			Log.e(TAG, "onRestoreInstanceState inside");
//			
//			mQuery = savedInstanceState.getString("mQuery");
//			if (mSearchView != null && mQuery != null) {
//				Log.e("TAG", "SET QUERY: " + mQuery);
//				mSearchView.setQuery(mQuery, false);
//			}
//			
//			Log.e("TAG", "SAVED QUERY: " + mQuery);
//			
//			mStateQuickSearchExpanded = savedInstanceState.getBoolean("mStateQuickSearchExpanded");
//		}
	}
	
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	mSearchView = new SearchView(mFragment.getSherlockActivity().getSupportActionBar().getThemedContext());
    	mSearchView.setIconifiedByDefault(true);
    	mSearchView.setOnQueryTextListener(this);
    	mSearchView.setOnQueryTextFocusChangeListener(this);
    	mSearchView.setQueryHint("Search Contacts...");
    	
    	mSearchMenu = menu.addSubMenu("Search");
		
    	mSearchMenu.getItem()
        	.setIcon(R.drawable.ic_search)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
    	mQuickSearchItem = mSearchMenu.add("Quick");
    	mQuickSearchItem.setActionView(mSearchView)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
    	
    	mSearchMenu.add("Advanced");
    	mSearchMenu.add("Saved Searches").setEnabled(false);
    	mClearSearchItem = mSearchMenu.add("Clear Search").setVisible(false);
    }
    
    private Runnable delayedSearch = new Runnable() {
		public void run() {
			mListener.onSearchTextChange(mQuery);
		}
	};
    
	@Override
	public boolean onQueryTextChange(String query) {
		if (mListener == null) return false;
		
		if (query.length() == 0 && (mQuery == null || mQuery.length() == 0)) return true; // don't post if the string is and has been 0 length
		if (mQuery != null && mQuery.equalsIgnoreCase(query)) return true; // don't post if the query didn't actually change
		
		mQuery = query;
		
		if (mQuery.length() > 0) {
			mHandler.removeCallbacks(delayedSearch);
			mHandler.postDelayed(delayedSearch, mDelay);
		} else {
			delayedSearch.run();
		}
		
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		if (mListener == null) return false;
		
		mListener.onSearchSubmit(query);
		
		if (mQuickSearchItem != null)
			mQuickSearchItem.collapseActionView();
		
		return true;
	}

	public void setListener(SearchMenuItemListener listener) {
		mListener = listener; 
	}
	
	public interface SearchMenuItemListener {
		
		public void onSearchTextChange(String query);

		public void onSearchSubmit(String query);
		
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus && mQuickSearchItem != null) {
			mQuickSearchItem.collapseActionView();
		}
	}
}