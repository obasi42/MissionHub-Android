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
		if (t instanceof java.net.ConnectException) {
			title = ctx.getString(R.string.error_no_network);
			message = ctx.getString(R.string.error_no_network_msg);
		} else if (message.indexOf("Unable to invoke no-args constructor for class") >= 0) {
			title = ctx.getString(R.string.error_unexpected_response);
			message = ctx.getString(R.string.error_unexpected_response_msg);
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