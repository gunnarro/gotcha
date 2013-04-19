package gunnarro.android.gotcha.mail;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class MailSenderTest extends TestCase {

//	public void testSendMail() {
//		MailSender sender = new MailSender("gunnar.ronneberg@gmail.com", "ABcd1986");
//		try {
//			sender.sendMail("This is Subject", "This is Body", "gunnar.ronneberg@gmail.com", "gunnar.ronneberg@gmail.com");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	
//	public void testWeekInYear() {
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy w");
//		System.out.println("Week: " + formatter.format(new Date()));
//	}
	
	public void testRegexp() {
		String[] numbers = new String[]{"45465500","+4745465500", "+4645465500"}; 
//		String line = "this is a regexp xtests line";
		String filter= "+47";
        if (filter.startsWith("+")) {
        	filter = "\\"+filter;
        }
		for (String nr : numbers) {
		if (nr.matches("^" + filter + ".*")) {
			System.out.println("match:" + nr);
		}else {
			System.out.println("no match: " + nr);
		}
		}
	}
}
