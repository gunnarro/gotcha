package gunnarro.android.gotcha.test;

import gunnarro.android.gotcha.old.SMSReceiver;
import gunnarro.android.gotcha.sms.SMSMsg;
import junit.framework.TestCase;
import android.os.Bundle;

public class SMSReceiverTest extends TestCase {

	public void testParseSms() {
		SMSReceiver smsReceiver = new SMSReceiver();
		SMSMsg receivedSms = smsReceiver.getReceivedSms(new Bundle());
		assertNotNull(receivedSms);
		assertNull(receivedSms.getToMobilePhoneNumber());
		assertNull(receivedSms.getMsg());
	}
}
