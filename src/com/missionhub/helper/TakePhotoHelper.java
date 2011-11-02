package com.missionhub.helper;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

public class TakePhotoHelper {

	public static final String TAG = TakePhotoHelper.class.getSimpleName();

	public static void onActivityResult(Context ctx, int code, Intent data) {
		final File file = getTempFile(ctx);
		try {
			Bitmap captureBmp = Media.getBitmap(ctx.getContentResolver(), Uri.fromFile(file));
			Log.d(TAG, "GOT PHOTO");
		} catch (Exception e) {
			Log.d(TAG, "ERROR GETTING PHOTO", e);
		}
	}

	public static File getTempFile(Context context) {
		// it will return /sdcard/image.tmp
		final File path = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
		if (!path.exists()) {
			path.mkdir();
		}
		return new File(path, "image.tmp");
	}

}