package com.missionhub.ui.widget;

import com.missionhub.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Tab extends LinearLayout {

	private TextView mTextView;
	
	public Tab(Context context) {
		super(context);
	}
	
	public Tab(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public static Tab inflate(Context context, ViewGroup parent, CharSequence label, Drawable icon) {
		Tab tab = (Tab) LayoutInflater.from(context).inflate(R.layout.widget_tab, null);
		tab.setLabel(label);
		tab.setIcon(icon);
		return tab;
	}
	
	public static Tab inflate(Context context, ViewGroup parent, int label, int icon) {
		Tab tab = (Tab) LayoutInflater.from(context).inflate(R.layout.widget_tab, null);
		tab.setLabel(label);
		tab.setIcon(icon);
		return tab;
	}
	
	private void setIcon(Drawable icon) {
		((ImageView) findViewById(R.id.icon)).setImageDrawable(icon);
	}
	
	private void setIcon(int id) {
		setIcon(getResources().getDrawable(id));
	}
	
	private void setLabel(CharSequence label) {
		mTextView.setText(label);
	}
	
	private void setLabel(int id) {
		setLabel(getResources().getString(id));
		
	}
	
	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
	}
	
	@Override
	protected void onFinishInflate() {
		mTextView = (TextView) findViewById(R.id.label);
	}
}