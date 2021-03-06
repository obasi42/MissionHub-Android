package com.missionhub.ui.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;

import com.missionhub.util.DisplayUtils;

import org.holoeverywhere.widget.ListView;

public class SelectableListView extends ListView {

    /**
     * selection modes
     */
    public static final int MODE_NORMAL = 1;
    public static final int MODE_CLICK_ONLY = 2;
    public static final int MODE_SELECT_ONLY = 3;

    /**
     * constant for left side selection
     */
    public static final int SIDE_LEFT = 1;

    /**
     * constant for right side selection
     */
    public static final int SIDE_RIGHT = 2;

    /**
     * the current selection side
     */
    private int mSelectionSide = SIDE_LEFT;

    /**
     * the current selection width
     */
    private int mSelectionWidth = (int) DisplayUtils.dpToPixel(56);

    /**
     * state variable for determining if a press is in selection mode
     */
    private boolean mSelectionMode = false;

    /**
     * touch start position when entering selection mode
     */
    private int mStartPosition;

    /**
     * the activated list item
     */
    private int mActivatedItem = -1;

    /**
     * the selection mode
     */
    private int mMode = MODE_NORMAL;

    /**
     * listener to handle item check events
     */
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
     * Creates a new SelectableListView Instance with an AttributeSet and default style
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

    /**
     * Override onTouchEvent to capture touch events for setting items as checked
     */
    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        // if the choice mode is none, just pass the touch event
        if (getChoiceMode() == CHOICE_MODE_NONE || mMode == MODE_CLICK_ONLY)
            return super.onTouchEvent(ev);

        final int action = ev.getAction();
        final int x = (int) ev.getX();
        final int y = (int) ev.getY();

        if (action == MotionEvent.ACTION_DOWN
                && (mMode == MODE_SELECT_ONLY || ((mSelectionSide == SIDE_LEFT && x < mSelectionWidth) || (mSelectionSide == SIDE_RIGHT && x > getWidth() - mSelectionWidth)))) {
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
    public Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt("mActivatedItem", mActivatedItem);
        bundle.putInt("mSelectionSide", mSelectionSide);
        bundle.putInt("mSelectionWidth", mSelectionWidth);
        bundle.putInt("mMode", mMode);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(final Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            mActivatedItem = bundle.getInt("mActivatedItem");
            mSelectionSide = bundle.getInt("mSelectionSide");
            mSelectionWidth = bundle.getInt("mSelectionWidth");
            mMode = bundle.getInt("mMode");
            super.onRestoreInstanceState(bundle.getParcelable("superState"));
            return;
        }

        super.onRestoreInstanceState(state);
    }

    /**
     * Override setItemChecked to attach the OnItemCheckedListener
     */
    @Override
    public synchronized void setItemChecked(final int position, final boolean checked) {
        super.setItemChecked(position, checked);
        if (mOnItemCheckedListener != null) {
            try {
                mOnItemCheckedListener.onSetItemChecked(position, checked);
            } catch (Exception e) {
                super.setItemChecked(position, false);
            }
            checkForAllUnchecked();
        }
    }

    public synchronized void setAllItemsChecked() {
        try {
            for (int i = 0; i < getAdapter().getCount(); i++) {
                setItemChecked(i, true);
            }
        } catch (Exception e) { /* ignore */ }
    }

    @Override
    public synchronized void clearChoices() {
        super.clearChoices();
        updateOnScreenCheckedViews();
        if (mOnItemCheckedListener != null) {
            mOnItemCheckedListener.onAllUnchecked();
        }
    }


    /**
     * Checks to see if all items are unchecked. Notifies the OnItemCheckedListener if set.
     */
    private void checkForAllUnchecked() {
        if (mOnItemCheckedListener != null) {
            final int checkedCount = getCheckedItemIds().length;
            if (checkedCount == 0) {
                mOnItemCheckedListener.onAllUnchecked();
            }
        }
    }

    /**
     * Sets the width in dip of the selection box
     *
     * @param widthDip
     */
    public void setSelectionWidth(final int widthDip) {
        mSelectionWidth = (int) DisplayUtils.dpToPixel(widthDip);
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
    public void setOnItemCheckedListener(final OnItemCheckedListener onItemCheckedListener) {
        mOnItemCheckedListener = onItemCheckedListener;
    }

    public void setSelectionMode(int mode) {
        mMode = mode;
    }

    private synchronized void updateOnScreenCheckedViews() {
        if (getCheckedItemPositions() == null) return;
        final int firstPos = getFirstVisiblePosition();
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int position = firstPos + i;
            final boolean value = getCheckedItemPositions().get(position);
            setStateOnView(child, value);
        }
    }
}