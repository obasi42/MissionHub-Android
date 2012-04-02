package com.missionhub.ui.widget;

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

	public static final int SIDE_LEFT = 1;
	public static final int SIDE_RIGHT = 2;

	private int mSelectionSide = SIDE_RIGHT;
	private int mSelectionWidth = 48;

	private boolean mSelectionMode = false;
	private int mStartPosition;

	public SelectableListView(final Context context) {
		super(context);
	}

	public SelectableListView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

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
	public void setItemChecked(final int position, final boolean value) {
		super.setItemChecked(position, value);
	}

	public void clearChecked() {
		final SparseBooleanArray CItem = getCheckedItemPositions();
		for (int i = 0; i < CItem.size(); i++) {
			if (CItem.valueAt(i)) {
				super.setItemChecked(CItem.keyAt(i), false);
			}
		}
	}

	public void setSelectionWidth(final int width) {
		mSelectionWidth = width;
	}

	public void setSelectionSide(final int selectionSide) {
		mSelectionSide = selectionSide;
	}

}