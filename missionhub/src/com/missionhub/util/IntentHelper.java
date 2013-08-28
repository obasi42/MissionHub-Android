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
import com.missionhub.model.EmailAddress;
import com.missionhub.model.EmailAddressDao;
import com.missionhub.model.PhoneNumber;
import com.missionhub.model.PhoneNumberDao;

import org.apache.commons.lang3.StringUtils;
import org.holoeverywhere.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of methods for creating and starting intents.
 */
public class IntentHelper {

    /**
     * Checks if a device is capable of sending an email through an intent
     *
     * @return
     */
    public static boolean canSendEmail() {
        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        addTaskFlags(intent);
        intent.setType("plain/text");
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"example@example.com"});
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Test Subject");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "Test Body");
        return hasIntentHandler(intent);
    }

    /**
     * Creates and starts an intent chooser for sending an email
     *
     * @param addresses
     * @param subject
     * @param body
     */
    public static void sendEmail(final String[] addresses, final String subject, final String body) {
        try {
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            addTaskFlags(intent);
            intent.setType("message/rfc822");
            if (addresses != null && addresses.length > 0) {
                intent.putExtra(android.content.Intent.EXTRA_EMAIL, addresses);
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

    /**
     * Checks if the devices is capable of opening a Facebook profile in either the app or browser as a fallback.
     *
     * @return
     */
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

    /**
     * Creates and starts an intent that opens a person's Facebook profile in the Facebook app. As a fallback,
     * the user's profile will be opened in a web browser.
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
            openUrl("http://www.facebook.com/profile.php?id=" + fbId);
        }
    }

    /**
     * Checks if the device is capable of opening a Google map.
     *
     * @return
     */
    public static boolean canOpenMap() {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + "1600 Pennsylvania Avenue NW, Washington, D.C."));
        addTaskFlags(intent);
        return hasIntentHandler(intent);
    }

    /**
     * Creates and starts an intent for opening a Google map.
     *
     * @param address
     */
    public static void openMap(final Address address) {
        try {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + address.toString()));
            addTaskFlags(intent);
            Application.getContext().startActivity(intent);
        } catch (final Exception e) {
            Toast.makeText(Application.getContext(), getString(R.string.intent_helper_no_map), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Checks if the device is capable of dialing a number.
     *
     * @return
     */
    public static boolean canDialNumber() {
        final Intent intent = new Intent(Intent.ACTION_DIAL);
        addTaskFlags(intent);
        intent.setData(Uri.parse("tel:15555555555"));
        return hasIntentHandler(intent);
    }

    /**
     * Creates and starts an intent to dial a phone number.
     *
     * @param number
     */
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

    /**
     * Checks if the device is cable of sending SMS messages.
     *
     * @return
     */
    public static boolean canSendSms() {
        final Intent intent = new Intent(Intent.ACTION_SENDTO);
        addTaskFlags(intent);
        intent.setType("vnd.android-dir/mms-sms");
        intent.setData(Uri.parse("sms:1-555-555-5555"));
        return hasIntentHandler(intent);
    }

    /**
     * Creates and starts an intent to send an SMS.
     *
     * @param numbers
     */
    public static void sendSms(final List<Phonenumber.PhoneNumber> numbers) {
        try {
            final Intent intent = new Intent(Intent.ACTION_SENDTO);
            addTaskFlags(intent);
            intent.setType("vnd.android-dir/mms-sms");

            String delimeter = ";";
            if (android.os.Build.MANUFACTURER != null && android.os.Build.MANUFACTURER.toLowerCase().contains("samsung")) {
                delimeter = ",";
            }
            List<String> nums = new ArrayList<String>();
            for (Phonenumber.PhoneNumber number : numbers) {
                nums.add(PhoneNumberUtil.getInstance().format(number, PhoneNumberUtil.PhoneNumberFormat.RFC3966).replace("tel:", ""));
            }
            intent.setData(Uri.parse("smsto:" + StringUtils.join(nums, delimeter)));

            startChooser(intent, R.string.intent_helper_send_sms);
        } catch (final Exception e) {
            Toast.makeText(Application.getContext(), getString(R.string.intent_helper_no_sms), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Checks if the device is capable of opening a url in a web browser.
     *
     * @return
     */
    public static boolean canOpenUrl() {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"));
        addTaskFlags(intent);
        return hasIntentHandler(intent);
    }

    /**
     * Creates and starts an intent to open a url in a web browser.
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
     * Adds the FLAG_ACTIVITY_NEW_TASK and FLAG_ACTIVITY_MULTIPLE_TASK flags to an intent.
     *
     * @param intent
     */
    private static void addTaskFlags(final Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    }

    /**
     * Creates and starts an intent chooser from an intent.
     *
     * @param intent
     * @param title
     * @throws ActivityNotFoundException
     */
    private static void startChooser(Intent intent, int title) throws ActivityNotFoundException {
        final Intent chooser = Intent.createChooser(intent, getString(title));
        addTaskFlags(chooser);
        Application.getContext().startActivity(chooser);
    }

    /**
     * Convenience method for retrieving a translated string
     *
     * @param resId
     * @return
     */
    private static String getString(final int resId) {
        return Application.getContext().getString(resId);
    }

    /**
     * Checks if the device has an intent handler for the given intent
     *
     * @param intent
     * @return
     */
    private static boolean hasIntentHandler(Intent intent) {
        ResolveInfo info = Application.getContext().getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return info != null;
    }

    public static void sendEmail(long[] personIds) {
        List<Long> ids = new ArrayList<Long>();
        for (long id : personIds) {
            ids.add(id);
        }

        List<String> emails = new ArrayList<String>();
        List<EmailAddress> addresses = Application.getDb().getEmailAddressDao().queryBuilder().where(EmailAddressDao.Properties.Person_id.in(ids), EmailAddressDao.Properties.Primary.eq(true)).list();
        for (EmailAddress address : addresses) {
            if (address == null || StringUtils.isEmpty(address.getEmail())) continue;
            emails.add(address.getEmail().trim());
        }

        sendEmail(emails.toArray(new String[emails.size()]), null, null);
    }

    public static void sendSms(long[] personIds) {
        List<Long> ids = new ArrayList<Long>();
        for (long id : personIds) {
            ids.add(id);
        }
        List<Phonenumber.PhoneNumber> phoneNumbers = new ArrayList<Phonenumber.PhoneNumber>();
        List<PhoneNumber> numbers = Application.getDb().getPhoneNumberDao().queryBuilder().where(PhoneNumberDao.Properties.Person_id.in(ids), PhoneNumberDao.Properties.Primary.eq(true)).list();
        for (PhoneNumber number : numbers) {
            if (number == null) continue;
            phoneNumbers.add(number.getPhoneNumber());
        }
        sendSms(phoneNumbers);
    }
}