package com.missionhub.ui;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.widget.SearchView;

import org.apache.commons.lang3.StringUtils;

import java.lang.ref.WeakReference;

public class SearchHelper implements SearchView.OnQueryTextListener, View.OnFocusChangeListener {

    private Handler mSearchHandler = new Handler(Looper.getMainLooper());
    private Runnable mSearchRunnable;
    private String mSearchQuery;
    private WeakReference<SearchView> mSearchView;
    private OnSearchQueryChangedListener mListener;

    public void setSearchView(SearchView searchView) {
        mSearchView = new WeakReference<SearchView>(searchView);
        if (StringUtils.isNotEmpty(mSearchQuery)) {
            searchView.setQuery(mSearchQuery, false);
        }
        searchView.setOnQueryTextListener(this);
        searchView.setOnQueryTextFocusChangeListener(this);
        searchView.clearFocus();
    }

    private SearchView getSearchView() {
        return (mSearchView != null && mSearchView.get() != null) ? mSearchView.get() : null;
    }

    public void clearFocus() {
        final SearchView view = getSearchView();
        if (view != null) {
            view.clearFocus();
            ((ViewGroup) view.getParent()).requestFocus();
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (!hasFocus) {
            clearFocus();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        clearFocus();
        return onQueryTextChange(s);
    }

    @Override
    public boolean onQueryTextChange(final String query) {
        if (query.length() == 0 && (mSearchQuery == null || mSearchQuery.length() == 0))
            return true;
        if (mSearchQuery != null && mSearchQuery.equalsIgnoreCase(query)) return true;

        mSearchQuery = query;

        if (mSearchRunnable != null) {
            mSearchHandler.removeCallbacks(mSearchRunnable);
        }
        mSearchRunnable = new Runnable() {
            @Override
            public void run() {
                if (mListener == null) return;

                mListener.onSearchQueryChanged(query);
            }
        };
        if (query.length() > 0) {
            mSearchHandler.postDelayed(mSearchRunnable, 250);
        } else {
            mSearchRunnable.run();
        }
        return true;
    }

    public void setOnSearchQueryChangedListener(OnSearchQueryChangedListener onSearchQueryChangedListener) {
        mListener = onSearchQueryChangedListener;
    }

    public interface OnSearchQueryChangedListener {
        public void onSearchQueryChanged(String query);
    }
}
