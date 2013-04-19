package gunnarro.android.gotcha.sms;

import gunnarro.android.gotcha.AppPreferences;
import gunnarro.android.gotcha.ListAppPreferencesImpl;
import gunnarro.android.gotcha.service.GotchaIntentService;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSHandler extends BroadcastReceiver {

	public final static String KEY_SMS_MSG = "message";
	public final static String KEY_MOBILE_NUMBER = "mobilenumber";
	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	/**
	 * A PDU is a "protocol description unit", which is the industry format for
	 * an SMS message. because SMSMessage reads/writes them you shouldn't need
	 * to disect them. A large message might be broken into many, which is why
	 * it is an array of objects.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.i(createLogTag(this.getClass()), "Handle incomming sms...");
		if (bundle != null) {
			if (intent.getAction().equals(SMS_RECEIVED)) {
				handleSMS(context, bundle);
			} else {
				Log.i(createLogTag(this.getClass()), "This was not an sms: " + intent.getAction());
			}
		}
	}

	private void handleSMS(Context context, Bundle bundle) {
		Object[] pdus = (Object[]) bundle.get("pdus");
		SmsMessage[] msgs = new SmsMessage[pdus.length];
		for (int i = 0; i < msgs.length; i++) {
			msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
			try {
				if (isBlackListed(context, msgs[i].getOriginatingAddress())) {
					Log.i(createLogTag(this.getClass()), "blacklisted, rejected sms from: " + msgs[i].getOriginatingAddress());
					super.abortBroadcast();
				}
				Intent intent = new Intent(context, GotchaIntentService.class);
				intent.putExtra(KEY_MOBILE_NUMBER, msgs[i].getOriginatingAddress());
				intent.putExtra(KEY_SMS_MSG, msgs[i].getMessageBody().toString());
				context.startService(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isBlackListed(Context context, String phoneNumber) {
		if (new ListAppPreferencesImpl(context, AppPreferences.SMS_BLACK_LIST).listContains(phoneNumber)) {
			return true;
		}
		return false;
	}

	public static String createLogTag(Class clazz) {
		return DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH).format(new Date()) + " " + clazz.getSimpleName();
	}
}
