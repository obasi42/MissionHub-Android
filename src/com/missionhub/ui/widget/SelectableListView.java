package com.missionhub.ui.widget;

import com.missionhub.util.U;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.ListView;

public class SelectableListView extends ListView {

	/** constant for left side selection */
	public static final int SIDE_LEFT = 1;

	/** constant for right side selection */
	public static final int SIDE_RIGHT = 2;

	/** the current selection side */
	private int mSelectionSide = SIDE_RIGHT;

	/** the current selection width */
	private int mSelectionWidth = (int) U.dpToPixel(48, getContext());

	/** state variable for determining if a press is in selection mode */
	private boolean mSelectionMode = false;

	/** touch start position when entering selection mode */
	private int mStartPosition;

	/** listener to handle item check events */
	private OnItemCheckedListener mOnItemCheckedListener;

	/**
	 * Creates a new SelectableListView Instance
	 * 
	 * @param context
	 */
	public SelectableListView(final Context context) {
		super(context);
	}

	/**
	 * Creates a new SelectableListView Instance with an AttributeSet
	 * 
	 * @param context
	 * @param attrs
	 */
	public SelectableListView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Creates a new SelectableListView Instance with an AttributeSet and
	 * default style
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SelectableListView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean performItemClick(final View view, final int position, final long id) {
		final OnItemClickListener mOnItemClickListener = getOnItemClickListener();
		if (mOnItemClickListener != null) {
			playSoundEffect(SoundEffectConstants.CLICK);
			if (view != null) {
				view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
			}
			mOnItemClickListener.onItemClick(this, view, position, id);
			return true;
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(final MotionEvent ev) {

		final int action = ev.getAction();
		final int x = (int) ev.getX();
		final int y = (int) ev.getY();

		if (action == MotionEvent.ACTION_DOWN && ((mSelectionSide == SIDE_LEFT && x < mSelectionWidth) || (mSelectionSide == SIDE_RIGHT && x > getWidth() - mSelectionWidth))) {
			mSelectionMode = true;
			mStartPosition = pointToPosition(x, y);
		}
		if (!mSelectionMode) {
			return super.onTouchEvent(ev);
		}
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			if (pointToPosition(x, y) != mStartPosition) {
				mSelectionMode = false;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
		default:
			mSelectionMode = false;
			final int mItemPosition = pointToPosition(x, y);
			if (mStartPosition != AdapterView.INVALID_POSITION) {
				setItemChecked(mItemPosition, !isItemChecked(mItemPosition));
			}
		}

		return true;
	}

	@Override
	public void setItemChecked(final int position, final boolean checked) {
		super.setItemChecked(position, checked);
		if (mOnItemCheckedListener != null) {
			mOnItemCheckedListener.onSetItemChecked(position, checked);
			checkForAllUnchecked();
		}
	}

	/**
	 * Clears all checked items
	 */
	public void clearChecked() {
		final SparseBooleanArray CItem = getCheckedItemPositions();
		for (int i = 0; i < CItem.size(); i++) {
			if (CItem.valueAt(i)) {
				super.setItemChecked(CItem.keyAt(i), false);
			}
		}
		checkForAllUnchecked();
	}

	/**
	 * Checks to see if all items are unchecked. Notifies the
	 * OnItemCheckedListener if set.
	 */
	private void checkForAllUnchecked() {
		if (mOnItemCheckedListener != null) {
			@SuppressWarnings("deprecation") int checkedCount = getCheckItemIds().length;
			if (checkedCount == 0) {
				mOnItemCheckedListener.onAllUnchecked();
			}
		}
	}

	/**
	 * Sets the width in dip of the selection box
	 * 
	 * @param width
	 */
	public void setSelectionWidth(final int widthDip) {
		mSelectionWidth = (int) U.dpToPixel(widthDip, getContext());
	}

	/**
	 * Sets the side of the selection box.
	 * 
	 * @param selectionSide
	 */
	public void setSelectionSide(final int selectionSide) {
		mSelectionSide = selectionSide;
	}

	/**
	 * Interface for handling on item checked events.
	 */
	public interface OnItemCheckedListener {
		void onSetItemChecked(int position, boolean checked);

		void onAllUnchecked();
	}

	/**
	 * Sets the OnItemCheckedListener
	 * 
	 * @param onItemCheckedListener
	 */
	public void setOnItemCheckedListener(OnItemCheckedListener onItemCheckedListener) {
		mOnItemCheckedListener = onItemCheckedListener;
	}

}