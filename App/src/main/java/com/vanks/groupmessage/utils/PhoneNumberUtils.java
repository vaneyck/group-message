package com.vanks.groupmessage.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Created by vaneyck on 12/3/15.
 */
public class PhoneNumberUtils {
	static PhoneNumberUtil phoneNumberUtil =  PhoneNumberUtil.getInstance();

	public static String getInternationalPhoneNumber(Context context, String src, boolean prettify) {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String simCountryIso = tm.getSimCountryIso().toUpperCase();
		Phonenumber.PhoneNumber phoneNumber;

		try {
			phoneNumber = phoneNumberUtil.parse(src, simCountryIso);
		} catch (NumberParseException e) {
			return src;
		}
		return formatPhoneNumber(phoneNumber, prettify);
	}

	private static String formatPhoneNumber(Phonenumber.PhoneNumber phoneNumber, boolean prettify) {
		if(prettify) {
			return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
		}
		else {
			return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
		}
	}
}
