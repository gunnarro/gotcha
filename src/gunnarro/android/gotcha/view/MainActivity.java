package gunnarro.android.gotcha.view;

import gunnarro.android.gotcha.MyLocationManager;
import gunnarro.android.gotcha.R;
import gunnarro.android.gotcha.mail.MailSender;
import gunnarro.android.gotcha.service.GotchaIntentService;
import gunnarro.android.gotcha.sms.SMSHandler;
import gunnarro.android.gotcha.sms.SMSReader;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private String smsActionCmd = "trace";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the main.xml layout file.
		setContentView(R.layout.main_layout);
		Button buttonMyLocation = (Button) findViewById(R.id.show_my_location);
		Button buttonTestGotcha = (Button) findViewById(R.id.test_gotcha);
		Button buttonTestSendMail = (Button) findViewById(R.id.test_send_mail);
		Button buttonClose = (Button) findViewById(R.id.main_view_close);

		buttonMyLocation.setOnClickListener(this);
		buttonTestGotcha.setOnClickListener(this);
		buttonTestSendMail.setOnClickListener(this);
		buttonClose.setOnClickListener(this);

		Spinner spinner = (Spinner) findViewById(R.id.sms_actions_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sms_actions, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());

		TextView mainTextView = (TextView) findViewById(R.id.main_view);
		mainTextView.setText(R.string.information);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.test_gotcha :
				Intent intent = new Intent(this, GotchaIntentService.class);
				intent.putExtra(SMSHandler.KEY_MOBILE_NUMBER, "45465500");
				intent.putExtra(SMSHandler.KEY_SMS_MSG, smsActionCmd);
				startService(intent);
				break;
			case R.id.show_my_location :
				displayLocation();
				break;
			case R.id.test_send_mail :
				sendMail();
				break;
			case R.id.main_view_close :
				// Close the application
				finish();
				break;
		}
	}

	private void displayLocation() {
		// Toast.makeText(this, "view location", Toast.LENGTH_LONG);
		String locationAddress = "n/a";
		try {
			locationAddress = new MyLocationManager().getLocation(this);
		} catch (Exception e) {
			locationAddress = e.toString();
			e.printStackTrace();
		}
		Log.i("displayLocation()", locationAddress);
		// Toast.makeText(this, locationAddress, Toast.LENGTH_LONG);
	}

	private void sendMail() {
		MailSender sender = new MailSender("gunnar.ronneberg@gmail.com", "ABcd1986");
		try {
			SMSReader mailReader = new SMSReader((Context) this);
			sender.sendMail("Forwared SMS", mailReader.getSMSInbox(false).toString(), "gunnar.ronneberg@gmail.com", "gunnar.ronneberg@gmail.com");
			Toast.makeText(this, "Sent mail to gunnar.ronneberg", Toast.LENGTH_LONG);
			Log.i("Forwarded SMS", "Forwarede SMS to gunnar.ronneberg@gmail.com");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class MyOnItemSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			smsActionCmd = parent.getItemAtPosition(pos).toString();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing.
		}
	}
}