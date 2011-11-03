package com.missionhub.ui.widget;

import com.missionhub.ui.widget.item.CenteredTextItem;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CenteredTextItemView extends TextView implements ItemView {

    public CenteredTextItemView(Context context) {
        this(context, null);
    }

    public CenteredTextItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CenteredTextItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
	public void prepareItemView() {
    }

    @Override
	public void setObject(Item object) {
        final CenteredTextItem item = (CenteredTextItem) object;
        setText(item.text);
        setClickable(true);
    }
    
    @Override
	public Class<? extends Item> getItemClass() {
		return CenteredTextItem.class;
	}
}