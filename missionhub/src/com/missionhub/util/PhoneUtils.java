package com.missionhub.util;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.missionhub.model.PhoneNumber;

public class PhoneUtils {

    public static Phonenumber.PhoneNumber parsePhoneNumber(final String phoneNumber) {
        try {
            return PhoneNumberUtil.getInstance().parseAndKeepRawInput(phoneNumber, "US");
        } catch (NumberParseException e) {
            /* ignore */
        }
        return null;
    }

    public static Phonenumber.PhoneNumber parsePhoneNumber(final PhoneNumber number) {
        if (number != null) {
            return parsePhoneNumber(number.getNumber());
        }
        return null;
    }

    public static boolean hasPhoneAbility(final Context ctx) {
        final TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

}
