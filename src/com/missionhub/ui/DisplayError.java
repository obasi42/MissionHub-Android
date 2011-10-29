package com.missionhub.ui;

import com.google.gson.JsonParseException;
import com.missionhub.MHException;
import com.missionhub.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.webkit.WebViewClient;

public class DisplayError { 
	
	public static final AlertDialog display(Context ctx, Throwable t) {
		String title = ctx.getString(R.string.alert_error);
		String message = ctx.getString(R.string.alert_error_msg);
		try {
			if (t.getMessage() != null) {
				message = t.getMessage();
			}
			if (t instanceof java.net.ConnectException) {
				title = ctx.getString(R.string.error_no_network);
				message = ctx.getString(R.string.error_no_network_msg);
			} else if (t.getMessage() != null && message.indexOf("Unable to invoke no-args constructor for class") >= 0) {
				title = ctx.getString(R.string.error_unexpected_response);
				message = ctx.getString(R.string.error_unexpected_response_msg);
			} else if (t instanceof JsonParseException) {
				title = ctx.getString(R.string.error_unexpected_response);
				message = ctx.getString(R.string.error_unexpected_response_msg);
			} else if (t instanceof MHException) {
				if (((MHException) t).getTitle() != null) {
					title = ((MHException) t).getTitle();
				}
			}
		} catch (Exception e) { Log.e("DisplayError", "Error Setup Failed", e); }
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

	public static AlertDialog display(Context ctx, int errorCode, String description, String failingUrl) {
		if (errorCode == WebViewClient.ERROR_CONNECT) {
			return display(ctx, new java.net.ConnectException());
		} else {
			return display(ctx, new Exception(description));
		}
	}
}