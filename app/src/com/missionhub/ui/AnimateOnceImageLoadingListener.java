package com.missionhub.ui;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AnimateOnceImageLoadingListener extends SimpleImageLoadingListener {

    final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
    final int mDurationMillis;

    public AnimateOnceImageLoadingListener(int durationMillis) {
        mDurationMillis = durationMillis;
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        if (loadedImage != null) {
            ImageView imageView = (ImageView) view;
            boolean firstDisplay = !displayedImages.contains(imageUri);
            if (firstDisplay) {
                FadeInBitmapDisplayer.animate(imageView, mDurationMillis);
                displayedImages.add(imageUri);
            } else {
                imageView.setImageBitmap(loadedImage);
            }
        }
    }

}