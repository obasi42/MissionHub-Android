package com.missionhub.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.TitleProvider;

public class ViewPagerAdapter extends PagerAdapter implements TitleProvider {

	/** should the adapter be notified on changes to pages */
	private boolean mNotify = true;

	/** the pages */
	List<View> mPages = new ArrayList<View>();

	/** the page titles */
	Map<View, String> mTitles = new HashMap<View, String>();

	/**
	 * Adds a page to the pager
	 * 
	 * @param view
	 */
	public void addPage(final View view) {
		mPages.add(view);
		if (mNotify) {
			notifyDataSetChanged();
		}
	}

	/**
	 * Adds a page to the pager with a title
	 * 
	 * @param view
	 * @param title
	 */
	public void addPage(final View page, final String title) {
		setPageTitle(page, title);
		addPage(page);
	}

	/**
	 * Removes a page from the adapter
	 * 
	 * @param view
	 */
	public void removePage(final View view) {
		mPages.remove(view);
		setPageTitle(view, null);
		if (mNotify) {
			notifyDataSetChanged();
		}
	}

	/**
	 * Sets the title for a page
	 * 
	 * @param page
	 * @param title
	 */
	public void setPageTitle(final View page, final String title) {
		if (title == null) {
			mTitles.remove(page);
		} else {
			mTitles.put(page, title);
		}
	}

	/**
	 * Sets if the adapter should be notified after adding or removing a page
	 * 
	 * @param notify
	 */
	public void setNotifyOnChange(final boolean notify) {
		mNotify = notify;
	}

	@Override
	public Object instantiateItem(final ViewGroup container, final int position) {
		((ViewPager) container).addView(mPages.get(position), 0);
		return mPages.get(position);
	}

	@Override
	public void destroyItem(final ViewGroup container, final int position, final Object view) {
		((ViewPager) container).removeView((View) view);
	}

	@Override
	public int getCount() {
		return mPages.size();
	}

	@Override
	public boolean isViewFromObject(final View view, final Object object) {
		return view == object;
	}

	@Override
	public void finishUpdate(final View view) {}

	@Override
	public void restoreState(final Parcelable p, final ClassLoader c) {}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(final View view) {}

	@Override
	public String getTitle(final int position) {
		return mTitles.get(mPages.get(position));
	}
}