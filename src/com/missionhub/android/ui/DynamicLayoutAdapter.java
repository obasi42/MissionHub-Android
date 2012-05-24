package com.missionhub.android.ui;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.android.ui.widget.ListItemView;

public class DynamicLayoutAdapter extends ListItemAdapter {

	private final Context mContext;
	private final DynamicLayoutInterface mLayoutInterface;
	
    public DynamicLayoutAdapter(final Context context) {
        super(context);
        mContext = context;
        
        if (!(context instanceof DynamicLayoutInterface)) {
        	throw new RuntimeException("DynamicLayoutAdapter's context must implement DynamicLayoutInterface");
        }
        mLayoutInterface = (DynamicLayoutInterface) mContext;
    }
    
    public interface DynamicLayoutInterface {
    	public Integer getLayoutResource(Item item);
    }
    
    public interface DynamicLayoutItem {
    	public void setLayoutResource(int resource);
    }
    
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final Item item = (Item) getItem(position);
		ItemView cell = (ItemView) convertView;
		
		final Integer layout = mLayoutInterface.getLayoutResource(item);
		if (item instanceof DynamicLayoutItem && layout != null) {
			((DynamicLayoutItem)item).setLayoutResource(layout);
		}
		
		if (cell == null || cell.getItemClass() != item.getClass()) {
			cell = item.newView(mContext, parent);
			cell.prepareItemView();
		} else {
			final Integer viewLayout = (Integer) ((View)cell).getTag(R.id.layout);
			if (viewLayout != null || viewLayout != layout) {
				cell = item.newView(mContext, parent);
				cell.prepareItemView();
			}
		}
		
		if (cell instanceof ListItemView) {
		    ((ListItemView)cell).setObject(item, parent, position);
		} else {
		    cell.setObject(item);
		}
		
		final View view = (View) cell;
		view.setTag(R.id.layout, layout);
		
		return setUpSelectableListView(position, view, parent);
	}
}