package com.missionhub.ui.itemview.delete;
//package com.missionhub.ui.itemview;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.ProgressBar;
//
//import com.missionhub.R;
//import com.missionhub.ui.item.Item;
//import com.missionhub.ui.item.ProgressItem;
//import com.missionhub.util.U;
//
///**
// * Views for use in PickAccountDialogFragment
// */
//public class ProgressItemView extends FrameLayout implements ItemView {
//
//	private ProgressBar mProgress;
//
//	public ProgressItemView(final Context context) {
//		this(context, null);
//	}
//
//	public ProgressItemView(final Context context, final AttributeSet attrs) {
//		this(context, attrs, 0);
//	}
//
//	public ProgressItemView(final Context context, final AttributeSet attrs, final int defStyle) {
//		super(context, attrs, defStyle);
//	}
//
//	@Override
//	public void prepareItemView() {
//		mProgress = (ProgressBar) findViewById(R.id.progress);
//	}
//
//	@Override
//	public void setObject(final Item item, final ViewGroup parent, final int position) {
//		final ProgressItem progress = (ProgressItem) item;
//		if (!U.isNullEmpty(mProgress, progress)) {
//			mProgress.setIndeterminate(progress.indeterminate);
//			mProgress.setProgress(progress.progress);
//		}
//	}
//
//	@Override
//	public Class<? extends Item> getItemClass() {
//		return ProgressItem.class;
//	}
//
//}