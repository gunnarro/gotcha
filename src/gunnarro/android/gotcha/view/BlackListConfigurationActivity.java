package gunnarro.android.gotcha.view;

import gunnarro.android.gotcha.AppPreferences;
import gunnarro.android.gotcha.ListAppPreferencesImpl;
import gunnarro.android.gotcha.R;
import android.os.Bundle;

public class BlackListConfigurationActivity extends ConfigurationActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setTitle(R.string.blacklist_title);
		super.setAppPreferences(new ListAppPreferencesImpl(this, AppPreferences.SMS_BLACK_LIST));
		super.setupEventHandlers();
	}

}