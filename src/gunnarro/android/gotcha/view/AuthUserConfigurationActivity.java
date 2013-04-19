package gunnarro.android.gotcha.view;

import gunnarro.android.gotcha.AppPreferences;
import gunnarro.android.gotcha.ListAppPreferencesImpl;
import gunnarro.android.gotcha.R;
import android.os.Bundle;

public class AuthUserConfigurationActivity extends ConfigurationActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setTitle(R.string.user_list_title);
		super.setAppPreferences(new ListAppPreferencesImpl(this, AppPreferences.AUTHENTICATED_USERS));
		super.setupEventHandlers();
	}

}