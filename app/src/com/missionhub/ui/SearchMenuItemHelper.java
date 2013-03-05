package com.missionhub.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.missionhub.R;
import com.missionhub.contactlist.ContactListFragment;
import com.missionhub.util.U;
import org.holoeverywhere.app.Fragment;

public class SearchMenuItemHelper implements OnQueryTextListener, OnFocusChangeListener {

    public static final String TAG = SearchMenuItemHelper.class.getSimpleName();

    private final Fragment mFragment;
    private SearchMenuItemListener mListener;
    private final Handler mHandler = new Handler();
    private SearchView mSearchView;
    private MenuItem mSearchItem;
    private final long mDelay = 300;
    private String mQuery;

    public SearchMenuItemHelper(final Fragment fragment) {
        mFragment = fragment;

        if (fragment instanceof SearchMenuItemListener) {
            mListener = (SearchMenuItemListener) fragment;
        }
    }

    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        mSearchView = new SearchView(mFragment.getSupportActivity().getSupportActionBar().getThemedContext());
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnQueryTextFocusChangeListener(this);
        mSearchView.setQueryHint("Search Contacts...");

        mSearchItem = menu.add(Menu.NONE, R.id.action_search, Menu.NONE, R.string.action_search).setIcon(R.drawable.ic_action_search)
                .setActionView(mSearchView).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
    }

    public SearchView getSearchView() {
        return mSearchView;
    }

    private final Runnable delayedSearch = new Runnable() {
        @Override
        public void run() {
            mListener.onSearchTextChange(mQuery);
        }
    };

    @Override
    public boolean onQueryTextChange(final String query) {
        if (mListener == null) return false;

        if (query.length() == 0 && (mQuery == null || mQuery.length() == 0)) return true;
        if (mQuery != null && mQuery.equalsIgnoreCase(query)) return true;

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
    public boolean onQueryTextSubmit(final String query) {
        if (mSearchView != null) {
            mSearchView.clearFocus();
        }
        onQueryTextChange(query);

        return true;
    }

    public interface SearchMenuItemListener {

        public void onSearchTextChange(String query);

    }

    @Override
    public void onFocusChange(final View v, final boolean hasFocus) {
        if (!hasFocus && mSearchItem != null && U.isNullEmpty(mQuery)) {
            mSearchItem.collapseActionView();
        }
    }

}