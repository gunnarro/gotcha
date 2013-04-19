package gunnarro.android.gotcha.view;

import gunnarro.android.gotcha.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class ApplicationTabWidget extends TabActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the main.xml layout file.
		setContentView(R.layout.application_layout);
		tabSetup();
	}

	private void tabSetup() {
		TabHost tabHost = getTabHost();
		// Initialize a TabSpec for each tab and add it to the TabHost
		tabHost.addTab(tabHost.newTabSpec("main").setIndicator("Main", getResources().getDrawable(R.drawable.main_tab)).setContent(new Intent().setClass(this, MainActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("BlkLst").setIndicator("BlkLst", getResources().getDrawable(R.drawable.filter_tab)).setContent(new Intent().setClass(this, BlackListConfigurationActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("Auth").setIndicator("Auth", getResources().getDrawable(R.drawable.filter_tab)).setContent(new Intent().setClass(this, AuthUserConfigurationActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("stat").setIndicator("Stat", getResources().getDrawable(R.drawable.statistic_tab)).setContent(new Intent().setClass(this, CallStatisticActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("SMS").setIndicator("SMS", getResources().getDrawable(R.drawable.sms_statistic_tab)).setContent(new Intent().setClass(this, SMSStatisticActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("log").setIndicator("Log", getResources().getDrawable(R.drawable.log_tab)).setContent(new Intent().setClass(this, LogActivity.class)));
		tabHost.setCurrentTab(0);
	}
}
