package com.missionhub.util;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.Html;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.model.Address;
import org.apache.commons.lang3.StringUtils;
import org.holoeverywhere.widget.Toast;

public class IntentHelper {

    public static boolean canSendEmail() {
        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        addTaskFlags(intent);
        intent.setType("plain/text");
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"example@example.com"});
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Test Subject");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "Test Body");
        return hasIntentHandler(intent);
    }

    public static void sendEmail(final String address, final String subject, final String body) {
        try {
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            addTaskFlags(intent);
            intent.setType("message/rfc822");
            if (StringUtils.isNotBlank(address)) {
                intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{address});
            }
            if (StringUtils.isNotEmpty(subject)) {
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            }
            if (StringUtils.isNotEmpty(body)) {
                intent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(body));
            }

            startChooser(intent, R.string.intent_helper_send_email);
        } catch (final Exception e) {
            Toast.makeText(Application.getContext(), getString(R.string.intent_helper_no_browser), Toast.LENGTH_LONG).show();
        }
    }

    public static boolean canOpenFacebookProfile() {
        try {
            Application.getContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + 711070597));
        addTaskFlags(intent);
        return hasIntentHandler(intent);
    }

    public static void openFacebookProfile(final long fbId) {
        try {
            Application.getContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + fbId));
            addTaskFlags(intent);
            Application.getContext().startActivity(intent);
        } catch (final Exception e) {
            openUrl("http://www.facebook.com/profile.php?id=" + fbId);
        }
    }

    public static boolean canOpenMap() {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + "1600 Pennsylvania Avenue NW, Washington, D.C."));
        addTaskFlags(intent);
        return hasIntentHandler(intent);
    }

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

    public static boolean canDialNumber() {
        final Intent intent = new Intent(Intent.ACTION_DIAL);
        addTaskFlags(intent);
        intent.setData(Uri.parse("tel:15555555555"));
        return hasIntentHandler(intent);
    }

    public static void dialNumber(final Phonenumber.PhoneNumber number) {
        try {
            final Intent intent = new Intent(Intent.ACTION_DIAL);
            addTaskFlags(intent);
            intent.setData(Uri.parse(PhoneNumberUtil.getInstance().format(number, PhoneNumberUtil.PhoneNumberFormat.RFC3966)));

            startChooser(intent, R.string.intent_helper_dial_number);
        } catch (final Exception e) {
            Toast.makeText(Application.getContext(), getString(R.string.intent_helper_no_calling), Toast.LENGTH_LONG).show();
        }
    }

    public static boolean canSendSms() {
        final Intent intent = new Intent(Intent.ACTION_SENDTO);
        addTaskFlags(intent);
        intent.setType("vnd.android-dir/mms-sms");
        intent.setData(Uri.parse("sms:1-555-555-5555"));
        return hasIntentHandler(intent);
    }

    public static void sendSms(final Phonenumber.PhoneNumber number) {
        try {
            final Intent intent = new Intent(Intent.ACTION_SENDTO);
            addTaskFlags(intent);
            intent.setType("vnd.android-dir/mms-sms");
            intent.setData(Uri.parse(PhoneNumberUtil.getInstance().format(number, PhoneNumberUtil.PhoneNumberFormat.RFC3966).replace("tel:", "sms:")));

            startChooser(intent, R.string.intent_helper_send_sms);
        } catch (final Exception e) {
            Toast.makeText(Application.getContext(), getString(R.string.intent_helper_no_sms), Toast.LENGTH_LONG).show();
        }
    }

    public static boolean canOpenUrl() {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"));
        addTaskFlags(intent);
        return hasIntentHandler(intent);
    }

    public static void openUrl(final String url) {
        try {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            addTaskFlags(intent);
            Application.getContext().startActivity(intent);
        } catch (final Exception e) {
            Toast.makeText(Application.getContext(), getString(R.string.intent_helper_no_browser), Toast.LENGTH_LONG).show();
        }
    }

    private static void addTaskFlags(final Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    }

    private static void startChooser(Intent intent, int title) throws ActivityNotFoundException {
        final Intent chooser = Intent.createChooser(intent, getString(title));
        addTaskFlags(chooser);
        Application.getContext().startActivity(chooser);
    }

    private static String getString(final int resId) {
        return Application.getContext().getString(resId);
    }

    private static boolean hasIntentHandler(Intent intent) {
        ResolveInfo info = Application.getContext().getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (info != null) {
            return true;
        } else {
            return false;
        }
    }


}