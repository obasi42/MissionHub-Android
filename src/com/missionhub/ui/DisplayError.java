package com.missionhub.ui;

import com.missionhub.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DisplayError { 
	
	public static final AlertDialog display(Context ctx, Throwable t) {
		String title = ctx.getString(R.string.alert_error);
		String message = ctx.getString(R.string.alert_error_msg);
		
		if (t.getMessage() != null) {
			message = t.getMessage();
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title)
				.setIcon(R.drawable.ic_dialog_alert)
				.setMessage(message)
				.setNeutralButton(R.string.alert_close, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		return builder.create();
	}
}