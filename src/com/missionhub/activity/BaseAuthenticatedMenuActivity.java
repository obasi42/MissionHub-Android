package com.missionhub.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.actionbarsherlock.view.MenuItem;
import com.missionhub.R;
import com.missionhub.fragment.SideMenuFragment;
import com.missionhub.fragment.SideMenuFragment.SideMenu;
import com.missionhub.fragment.SideMenuFragment.SideMenuProvider;
import com.missionhub.ui.item.SideMenuItem;
import com.missionhub.util.U;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivityBase;
import com.slidingmenu.lib.app.SlidingActivityHelper;

public class BaseAuthenticatedMenuActivity extends BaseAuthenticatedActivity implements SlidingActivityBase, SideMenuProvider {

	/** the menu helper that handles most of the menu operations */
	private SlidingActivityHelper mHelper;;

	/** the menu fragment */
	private SideMenuFragment mMenuFragment;

	/** used to restore the menu visibility state */
	private boolean mIsBehindShowing;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// requestWindowFeature(Window.FEATURE_PROGRESS);

		// set up the menu
		mHelper = new SlidingActivityHelper(this);
		mHelper.onCreate(savedInstanceState);
		mHelper.setSlidingActionBarEnabled(true);

		// set the home button to enabled... otherwise it's impossible to get to the menu
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// calculate the right margin of the menu
		final DisplayMetrics dm = getResources().getDisplayMetrics();
		final int width = dm.widthPixels - getResources().getDimensionPixelSize(R.dimen.menu_width);
		getSlidingMenu().setBehindOffset(width);
		getSlidingMenu().setShadowWidth(Math.round(U.dpToPixel(5, this)));

		// set the behind view to a frame with the menu_container id that the menu fragment can attach to
		final FrameLayout layout = new FrameLayout(this);
		layout.setId(R.id.menu_container);
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		setBehindContentView(layout);
	}

	@Override
	public void onPostCreate(final Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate(savedInstanceState);

		// create or attach the menu fragment to the behind view
		final FragmentManager fm = getSupportFragmentManager();
		final FragmentTransaction ft = fm.beginTransaction();
		mMenuFragment = (SideMenuFragment) fm.findFragmentByTag("menu_fragment");
		if (mMenuFragment == null) {
			mMenuFragment = new SideMenuFragment();
			ft.add(R.id.menu_container, mMenuFragment, "menu_fragment");
		} else {
			ft.attach(mMenuFragment);
		}
		ft.commitAllowingStateLoss();
		fm.executePendingTransactions();

		// restore the visibility state of the menu
		if (mIsBehindShowing) {
			showBehind();
		} else {
			showAbove();
		}
	}

	@Override
	public void onSaveInstanceState(final Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBoolean("mIsBehindShowing", getSlidingMenu().isBehindShowing());
	}

	@Override
	public void onRestoreInstanceState(final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			mIsBehindShowing = savedInstanceState.getBoolean("mIsBehindShowing", false);
		}
	}

	@Override
	public View findViewById(final int id) {
		final View v = super.findViewById(id);
		if (v != null) return v;
		return mHelper.findViewById(id);
	}

	@Override
	public void setContentView(final int id) {
		setContentView(getLayoutInflater().inflate(id, null));
	}

	@Override
	public void setContentView(final View v) {
		setContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	@Override
	public void setContentView(final View v, final LayoutParams params) {
		super.setContentView(v, params);
		mHelper.registerAboveContentView(v, params);
	}

	public void setBehindContentView(final int id) {
		setBehindContentView(getLayoutInflater().inflate(id, null));
	}

	public void setBehindContentView(final View v) {
		setBehindContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	@Override
	public void setBehindContentView(final View v, final LayoutParams params) {
		mHelper.setBehindContentView(v, params);
	}

	@Override
	public SlidingMenu getSlidingMenu() {
		return mHelper.getSlidingMenu();
	}

	@Override
	public void toggle() {
		mHelper.toggle();
	}

	@Override
	public void showAbove() {
		mHelper.showAbove();
	}

	@Override
	public void showBehind() {
		mHelper.showBehind();
	}

	public void setSlidingActionBarEnabled(final boolean b) {
		mHelper.setSlidingActionBarEnabled(b);
	}

	@Override
	public boolean onKeyUp(final int keyCode, final KeyEvent event) {
		final boolean b = mHelper.onKeyUp(keyCode, event);
		if (b) return b;
		return super.onKeyUp(keyCode, event);
	}

	public void toggleMenu() {
		toggle();
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			toggleMenu();
			return true;
		}
		return false;
	}

	@Override
	public void onCreateSideMenu(final SideMenu menu) {

	}

	@Override
	public void onSideMenuItemSelected(final SideMenuItem item) {
		showAbove();
	}
}