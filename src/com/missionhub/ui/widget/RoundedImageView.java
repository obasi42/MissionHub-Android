package com.missionhub.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {
	
	private final RectF mBounds;
	private final Path mClippingPath;
	private final int mCornerRadius;

	public RoundedImageView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		float f = context.getResources().getDisplayMetrics().density;
		int i = (int) (4.0F * f + 0.5F);
		this.mCornerRadius = i;
		Path localPath = new Path();
		this.mClippingPath = localPath;
		RectF localRectF = new RectF();
		this.mBounds = localRectF;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Path localPath = this.mClippingPath;
		canvas.clipPath(localPath);
		super.onDraw(canvas);
	}

	@Override
	public void onLayout(boolean changed, int left, int top, int right, int bottom) {
		this.mClippingPath.reset();
		RectF localRectF1 = this.mBounds;
		float left2 = getPaddingLeft();
		float top2 = getPaddingTop();
		float right2 = right - left - getPaddingRight();
		float bottom2 = bottom - top - getPaddingBottom();
		localRectF1.set(left2, top2, right2, bottom2);
		Path localPath = this.mClippingPath;
		RectF localRectF2 = this.mBounds;
		float r1 = this.mCornerRadius;
		float r2 = this.mCornerRadius;
		Path.Direction localDirection = Path.Direction.CW;
		localPath.addRoundRect(localRectF2, r1, r2, localDirection);
		super.onLayout(changed, left, top, right, bottom);
	}
}