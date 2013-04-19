package gunnarro.android.gotcha.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class UtilityTest extends TestCase {

	public void testUtility() {
		assertFalse(isUserAuthenticated("45454545", new ArrayList<String>()));

	}
	
	private boolean isUserAuthenticated(String user, List<String> list) {
		for (String autehnticatedUser : list) {
			if (autehnticatedUser.equals(user)) {
				return true;
			}
		}
		return false;
	}
}
