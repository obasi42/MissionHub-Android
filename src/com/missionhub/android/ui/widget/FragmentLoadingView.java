package com.missionhub.android.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.missionhub.R;
import com.missionhub.android.util.U;

public class FragmentLoadingView extends FrameLayout {

	private TextView mLoadingText;

	public FragmentLoadingView(final Context context) {
		super(context);
		inflateView(context, null);
	}

	public FragmentLoadingView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		inflateView(context, attrs);
	}

	private void inflateView(final Context context, final AttributeSet attrs) {
		String loadingText = null;
		int layout = R.layout.widget_fragment_loading;
		if (attrs != null) {
			final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.widget_loading_fragment);
			loadingText = a.getString(R.styleable.widget_loading_fragment_loading_text);
			layout = a.getResourceId(R.styleable.widget_loading_fragment_layout, R.layout.widget_fragment_loading);
		}

		View.inflate(context, layout, this);

		mLoadingText = (TextView) findViewById(R.id.text);
		if (!U.isNullEmpty(loadingText) && mLoadingText != null) {
			mLoadingText.setText(loadingText);
		}
	}
}