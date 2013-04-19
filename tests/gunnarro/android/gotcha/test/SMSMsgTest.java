package gunnarro.android.gotcha.test;

import gunnarro.android.gotcha.sms.SMSMsg;
import junit.framework.TestCase;

public class SMSMsgTest extends TestCase {

	public void testOrdinarySms() {
		SMSMsg smsMsg = new SMSMsg("9219486", "This is an ordinary sms.");
		assertFalse(smsMsg.isTraceSMS());
	}

	public void testIsTraceSMS() {
		SMSMsg smsMsg = new SMSMsg("9219486", "TRACE");
		assertTrue(smsMsg.isTraceSMS());

		smsMsg = new SMSMsg("9219486", "trace");
		assertTrue(smsMsg.isTraceSMS());
	}

}
