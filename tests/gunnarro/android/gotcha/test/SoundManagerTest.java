package gunnarro.android.gotcha.test;

import gunnarro.android.gotcha.SoundManager;
import junit.framework.TestCase;

public class SoundManagerTest extends TestCase {

	public void testSoundManager() {
		assertNotNull(SoundManager.getInstance());
		SoundManager.cleanup();
	}
}
