package com.missionhub.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.missionhub.R;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabBar extends LinearLayout implements View.OnClickListener {

    private List<TabBarButton> mButtons = Collections.synchronizedList(new ArrayList<TabBarButton>());
    private int mActive = 0;
    private OnButtonClickListener mOnClickListener;

    public TabBar(Context context) {
        super(context);
    }

    public TabBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onClick(View view) {
        setActiveTab((TabBarButton) view.getParent());
    }

    private void triggerOnTabClicked(TabBarButton button) {
        if (mOnClickListener != null) {
            mOnClickListener.onTabClicked(mButtons.indexOf(button), button);
        }
    }

    private void setActiveTab(TabBarButton button) {
        mActive = mButtons.indexOf(button);

        for (TabBarButton current : mButtons) {
            current.setActive(button == current);
        }

        triggerOnTabClicked(button);
    }

    public int getActiveTab() {
        return mActive;
    }

    public int getActiveTabId() {
        return mButtons.get(mActive).getId();
    }

    public void setActiveTab(int index) {
        setActiveTab(mButtons.get(index));
    }

    public void setActiveTabById(int id) {
        for (TabBarButton current : mButtons) {
            if (current.getId() == id) {
                setActiveTab(current);
                break;
            }
        }
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof TabBarButton) {
                prepareButton((TabBarButton) child);
                mButtons.add((TabBarButton) child);
            }
        }

        if (!mButtons.isEmpty()) {
            mButtons.get(0).setActive(true);
        }
    }

    private void prepareButton(TabBarButton button) {
        button.setActive(false);
        button.setOnClickListener(this);
    }


    public static interface OnButtonClickListener {
        public void onTabClicked(int index, TabBarButton button);
    }


    public static class TabBarButton extends RelativeLayout {

        private CharSequence mTitle;
        private Boolean mActive;

        private TextView mTitleView;
        private View mCaretView;

        public TabBarButton(Context context) {
            super(context);
        }

        public TabBarButton(Context context, AttributeSet attrs) {
            super(context, attrs);
            parseAttributes(context, attrs);
        }

        public TabBarButton(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            parseAttributes(context, attrs);
        }

        private void parseAttributes(Context context, AttributeSet attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabBarButton);

            mActive = a.getBoolean(R.styleable.TabBarButton_tb_active, false);
            mTitle = a.getText(R.styleable.TabBarButton_tb_title);

            a.recycle();
        }

        @Override
        public void onFinishInflate() {
            super.onFinishInflate();
            LayoutInflater.from(getContext()).inflate(R.layout.widget_tab_bar_button, this, true);
            mTitleView = (TextView) findViewById(R.id.title);
            setTitle(mTitle);

            mCaretView = findViewById(R.id.caret);
            setActive(mActive);
        }

        public void setTitle(CharSequence title) {
            mTitle = title;
            mTitleView.setText(title);
        }

        public void setActive(boolean active) {
            mActive = active;
            if (mActive) {
                mCaretView.setVisibility(View.VISIBLE);
            } else {
                mCaretView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void setOnClickListener(OnClickListener onClickListener) {
            mTitleView.setOnClickListener(onClickListener);
        }
    }
}
