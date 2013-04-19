package gunnarro.android.gotcha.test;

import gunnarro.android.gotcha.mail.MailSender;
import junit.framework.TestCase;

public class MailSenderTest extends TestCase {

	public void testSendMail() {
		MailSender sender = new MailSender("gunnar.ronneberg@gmail.com", "password");
		try {
			sender.sendMail("This is Subject", "This is Body", "gunnar.ronneberg@gmail.com", "gunnar.ronneberg@gmail.com");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
