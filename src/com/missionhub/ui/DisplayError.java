package com.missionhub.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.missionhub.R;
import com.missionhub.error.MissionHubException;

public class DisplayError {

	public static final AlertDialog display(final Context ctx, final Throwable t) {
		String title = ctx.getString(R.string.error);
		String message = ctx.getString(R.string.error_msg);
		try {
			if (t.getMessage() != null) {
				message = t.getMessage();
			}
			if (t instanceof java.net.SocketException) {
				title = ctx.getString(R.string.error_no_network);
				message = ctx.getString(R.string.error_no_network_msg);
			} else if (t.getMessage() != null && message.indexOf("Unable to invoke no-args constructor for class") >= 0) {
				title = ctx.getString(R.string.error_unexpected_response);
				message = ctx.getString(R.string.error_unexpected_response_msg);
			} else if (t instanceof JsonParseException) {
				title = ctx.getString(R.string.error_unexpected_response);
				message = ctx.getString(R.string.error_unexpected_response_msg);
			} else if (t instanceof MissionHubException) {
				if (((MissionHubException) t).getTitle() != null) {
					title = ((MissionHubException) t).getTitle();
				}
			}
		} catch (final Exception e) {
			Log.e("DisplayError", "Error Setup Failed", e);
		}
		final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title).setIcon(R.drawable.ic_dialog_alert).setMessage(message).setNeutralButton(R.string.action_close, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		return builder.create();
	}

	public static final AlertDialog displayWithRetry(final Context ctx, final Throwable t, final Retry retry) {
		final AlertDialog ad = display(ctx, t);
		ad.setButton(AlertDialog.BUTTON_POSITIVE, ad.getContext().getString(R.string.action_retry), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.dismiss();
				retry.run();
			}
		});
		return ad;
	}

	public abstract static class Retry {
		public abstract void run();
	}
}