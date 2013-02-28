package com.missionhub.android.util;

import android.content.Intent;
import android.net.Uri;
import com.missionhub.android.R;
import com.missionhub.android.application.Application;
import com.missionhub.android.model.Address;
import org.holoeverywhere.widget.Toast;

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
            Toast.makeText(Application.getContext(), getString(R.string.intent_helper_no_browser), Toast.LENGTH_LONG).show();
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
            Toast.makeText(Application.getContext(), getString(R.string.intent_helper_no_calling), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * "Views" a telephone number
     *
     * @param number
     */
    public static void viewNumber(final String number) {
        try {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            addTaskFlags(intent);
            intent.setData(Uri.parse("tel:" + number));
            Application.getContext().startActivity(intent);
        } catch (final Exception e) {
            Toast.makeText(Application.getContext(), getString(R.string.intent_helper_no_calling), Toast.LENGTH_LONG).show();
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
            Toast.makeText(Application.getContext(), getString(R.string.intent_helper_no_sms), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Sends an email
     *
     * @param address
     */
    public static void sendEmail(final String address) {
        sendEmail(address, null);
    }

    /**
     * Sends an email
     *
     * @param address
     * @param subject
     */
    public static void sendEmail(final String address, final String subject) {
        sendEmail(address, subject, null);
    }

    /**
     * Sends an email
     *
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
                intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{address});
            }
            if (!U.isNullEmpty(subject)) {
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            }
            if (!U.isNullEmpty(body)) {
                intent.putExtra(android.content.Intent.EXTRA_TEXT, body);
            }

            final Intent chooser = Intent.createChooser(intent, getString(R.string.intent_helper_send_email));
            addTaskFlags(chooser);

            Application.getContext().startActivity(chooser);
        } catch (final Exception e) {
            Toast.makeText(Application.getContext(), getString(R.string.intent_helper_no_browser), Toast.LENGTH_LONG).show();
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

    private static String getString(final int resId) {
        return Application.getContext().getString(resId);
    }

    /**
     * Opens a Facebook profile in the Facebook app if available, otherwise in a web browser.
     *
     * @param fbId
     */
    public static void openFacebookProfile(final long fbId) {
        try {
            Application.getContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + fbId));
            addTaskFlags(intent);
            Application.getContext().startActivity(intent);
        } catch (final Exception e) {
            try {
                openUrl("http://www.facebook.com/profile.php?id=" + fbId);
            } catch (final Exception f) {
                Toast.makeText(Application.getContext(), R.string.intent_helper_no_facebook, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Opens a map to an address
     *
     * @param adress
     */
    public static void openMap(final Address address) {
        final String query = U.concatinate(", ", true, address.getAddress1(), address.getAddress2(), address.getCity(), address.getState(), address.getZip(), address.getCountry());

        try {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + query));
            addTaskFlags(intent);
            Application.getContext().startActivity(intent);
        } catch (final Exception e) {
            Toast.makeText(Application.getContext(), getString(R.string.intent_helper_no_map), Toast.LENGTH_LONG).show();
        }
    }

}