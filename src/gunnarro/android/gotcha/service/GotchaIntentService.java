package gunnarro.android.gotcha.service;

import gunnarro.android.gotcha.AppPreferences;
import gunnarro.android.gotcha.ListAppPreferencesImpl;
import gunnarro.android.gotcha.MyLocationManager;
import gunnarro.android.gotcha.calls.CallRegister;
import gunnarro.android.gotcha.mail.MailSender;
import gunnarro.android.gotcha.sms.SMS;
import gunnarro.android.gotcha.sms.SMSHandler;
import gunnarro.android.gotcha.sms.SMSMsg;
import gunnarro.android.gotcha.sms.SMSReader;
import gunnarro.android.gotcha.sms.SMSSender;

import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * To create a application to run in the background of other current activities,
 * one needs to create a Service. The Service can run indefinitely (unbounded)
 * or can run at the lifespan of the calling activity(bounded).
 * 
 * @author gunnarro
 */
public class GotchaIntentService extends IntentService {

	private static final String TAG = GotchaIntentService.class.getName();
	private MediaPlayer player;
	private Handler handler;

	/**
	 * Default constructor
	 */
	public GotchaIntentService() {
		super(TAG);
	}

	private void log(final String txt, boolean isToast) {
		Log.i("", txt);
		if (isToast) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(GotchaIntentService.this, txt, Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		log(TAG + " onHandleIntent", false);
		if (intent.getExtras() != null) {
			if (intent.getExtras().getString(SMSHandler.KEY_MOBILE_NUMBER) == null) {
				log(TAG + " missing mobile number in extras", false);
				return;
			}
			handleReceivedSMS(new SMSMsg(intent.getExtras().getString(SMSHandler.KEY_MOBILE_NUMBER), intent.getExtras().getString(SMSHandler.KEY_SMS_MSG)),
					this);
		} else {
			log(TAG + " extras is null !", false);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		handler = new Handler();
		log(TAG + "Created", false);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		log(TAG + " Stopped", false);
		handler = null;
		if (player != null) {
			player.stop();
			player = null;
		}
	}

	private void alertUser(String msg) {
		log(msg, true);
		// player = MediaPlayer.create(this, R.raw.starwars);
		// player.setLooping(false); // Set looping
		// player.start();
	}

	private void handleReceivedSMS(SMSMsg receivedSMSMsg, Context context) {
		if (receivedSMSMsg.isTraceSMS()) {
			handleTraceSMS(receivedSMSMsg, context);
		} else if (receivedSMSMsg.isForwardSMS()) {
			handleForwardSMS(receivedSMSMsg, context);
		} else {
			log("Ordinary sms: " + receivedSMSMsg.toString(), false);
			return;
		}
	}

	private void handleTraceSMS(SMSMsg receivedSMSMsg, Context context) {
		String from = null;
		if (new ListAppPreferencesImpl(context, AppPreferences.AUTHENTICATED_USERS).listContains(receivedSMSMsg.getToMobilePhoneNumber())) {
			log(receivedSMSMsg.getToMobilePhoneNumber() + " is authenticated", false);
			// give the mobile owner a signal that he has been traced.
			// get the mobile location if available
			String locationAddress = null;
			try {
				locationAddress = new MyLocationManager().getLocation(context);
			} catch (Exception e) {
				locationAddress = "ERROR getting address: " + e.toString();
				e.printStackTrace();
			}
			from = lookupContactName(receivedSMSMsg.getToMobilePhoneNumber());
			if (from == null) {
				from = receivedSMSMsg.getToMobilePhoneNumber();
			}
			alertUser("Sent trace sms to: " + from + " " + locationAddress);
			new SMSSender().sendSMS(receivedSMSMsg.getToMobilePhoneNumber(), locationAddress);
		} else {
			log("Ingnore this request, the requester was not authenticated to use the sms trace service, mobile number: "
					+ receivedSMSMsg.getToMobilePhoneNumber(), true);
		}
	}

	private void handleForwardSMS(SMSMsg receivedSMSMsg, Context context) {
		boolean onlyUnread = true;
		if (new ListAppPreferencesImpl(context, AppPreferences.AUTHENTICATED_USERS).listContains(receivedSMSMsg.getToMobilePhoneNumber())) {
			MailSender mailSender = new MailSender("gunnar.ronneberg@gmail.com", "ABcd1986");
			try {
				SMSReader smsReader = new SMSReader(context);
				List<SMS> unreadSms = smsReader.getSMSInbox(onlyUnread);
				String missesCalls = CallRegister.getMissedCalls(context);
				StringBuffer msg = new StringBuffer();
				msg.append("-------------------------------------------\n");
				msg.append("Call and sms log for " + getDevicePhoneNumber() + "\n");
				msg.append("-------------------------------------------\n");
				msg.append("Misses calls:\n");
				msg.append("-------------------------------------------\n");
				msg.append(missesCalls.toString()).append("\n\n");
				msg.append("Unread sms:\n");
				msg.append("-------------------------------------------\n");
				for (SMS sms : unreadSms) {
					msg.append(sms.toString()).append("\n");
				}
				mailSender.sendMail("Call and SMS log", msg.toString(), "gunnar.ronneberg@gmail.com", "gunnar.ronneberg@gmail.com");
				alertUser("Forwarded call log and sms inbox to email address gunnar.ronneberg");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			log("Ingnore this request, the requester was not authenticated to use the sms forward service, mobile number: "
					+ receivedSMSMsg.getToMobilePhoneNumber(), true);
		}
	}

	private String lookupContactName(String mobilePhonenumber) {
		String contactDisplayName = null;
		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(mobilePhonenumber));
		String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
		// Query the filter URI
		Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				contactDisplayName = cursor.getString(0);
			}
			cursor.close();
		}
		return contactDisplayName;
	}

	private boolean isAuthenticated(String mobilePhoneNumber) {
		if (mobilePhoneNumber.equals(getDevicePhoneNumber())) {
			return true;
		}
		return false;
	}

	private String getDevicePhoneNumber() {
		TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getLine1Number();
	}
}
