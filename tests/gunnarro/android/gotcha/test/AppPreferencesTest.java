package gunnarro.android.gotcha.test;

import gunnarro.android.gotcha.AppPreferences;
import junit.framework.TestCase;

public class AppPreferencesTest extends TestCase {

	public void testAuthenticatedUsers() {
		AppPreferences appPreferences = new AppPreferences(null);
		assertNotNull(appPreferences.getAuthenticatedUsers());
	}
}
