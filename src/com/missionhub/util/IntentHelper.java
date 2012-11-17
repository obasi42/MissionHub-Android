package com.missionhub.util;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.missionhub.application.Application;

public class IntentHelper {

	/**
	 * Opens a url in system web browser
	 * 
	 * @param url
	 */
	public static void openUrl(final String url) {
		try {
			final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			addTaskFlags(intent);
			Application.getContext().startActivity(intent);
		} catch (final Exception e) {
			Toast.makeText(Application.getContext(), "No web browser available.", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Dials a number
	 * 
	 * @param number
	 */
	public static void dialNumber(final String number) {
		try {
			final Intent intent = new Intent(Intent.ACTION_DIAL);
			addTaskFlags(intent);
			intent.setData(Uri.parse("tel:" + number));
			Application.getContext().startActivity(intent);
		} catch (final Exception e) {
			Toast.makeText(Application.getContext(), "No calling client available.", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Sends an sms
	 * 
	 * @param number
	 */
	public static void sendSms(final String number) {
		try {
			final Intent intent = new Intent(Intent.ACTION_VIEW);
			addTaskFlags(intent);
			intent.putExtra("address", number);
			intent.setType("vnd.android-dir/mms-sms");
			Application.getContext().startActivity(intent);
		} catch (final Exception e) {
			Toast.makeText(Application.getContext(), "No sms client available.", Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * Sends an email
	 * @param address
	 */
	public static void sendEmail(final String address) {
		sendEmail(address, null);
	}
	
	/**
	 * Sends an email
	 * @param address
	 * @param subject
	 */
	public static void sendEmail(final String address, final String subject) {
		sendEmail(address, subject, null);
	}
	
	/**
	 * Sends an email
	 * @param address
	 * @param subject
	 * @param body
	 */
	public static void sendEmail(final String address, final String subject, final String body) {
		try {
			final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
			addTaskFlags(intent);
			intent.setType("plain/text");
			if (!U.isNullEmpty(address)) {
				intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { address });
			}
			if (!U.isNullEmpty(subject)) {
				intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
			}
			if (!U.isNullEmpty(body)) {
				intent.putExtra(android.content.Intent.EXTRA_TEXT, body);  
			}
			
			final Intent chooser = Intent.createChooser(intent, "Send Email");
			addTaskFlags(chooser);
			
			Application.getContext().startActivity(chooser);
		} catch (final Exception e) {
			Log.e("IntentHelper", e.getMessage(), e);
			Toast.makeText(Application.getContext(), "No email client available.", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Adds flags required to start the task outside of an activity context
	 * 
	 * @param intent
	 */
	private static void addTaskFlags(final Intent intent) {
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
	}
}