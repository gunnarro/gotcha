package gunnarro.android.gotcha.view;

import gunnarro.android.gotcha.R;
import gunnarro.android.gotcha.mail.MailSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LogActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_layout);
		setupEventHandlers();
		TextView textView = (TextView) findViewById(R.id.log);
		textView.setMovementMethod(ScrollingMovementMethod.getInstance());
	}

	private void setupEventHandlers() {
		findViewById(R.id.view_log).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				displayLog();
			}
		});

		findViewById(R.id.blacklist).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText filterText = (EditText) findViewById(R.id.input_field);
				filterText.setText("blacklisted");
				displayLog();
			}
		});

		findViewById(R.id.clear_log).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearLog();
			}
		});
	}

	private void clearLog() {
		try {
			Runtime.getRuntime().exec("logcat -c");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void displayLog() {
		TextView logTextView = (TextView) findViewById(R.id.log);
		logTextView.setText(getLogContent(getLogFilter()));
	}

	private String getLogFilter() {
		EditText filterText = (EditText) findViewById(R.id.input_field);
		String filter = filterText.getText().toString().trim();
		if (filter != null && filter.length() > 1) {
			filter = ".*" + filter + ".*";
		} else {
			// Match all
			filter = ".*";
		}
		return filter;
	}

	private void sendLogToEmail() {
		try {
			MailSender sender = new MailSender("gunnar.ronneberg@gmail.com", "ABcd1986");
			sender.sendMail("Samsung Galaxy S2 System log", getLogContent(getLogFilter()), "gunnar.ronneberg@gmail.com", "gunnar.ronneberg@gmail.com");
			Toast.makeText(this, "Sent mail to gunnar.ronneberg", Toast.LENGTH_LONG);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getLogContent(String filter) {
		Process process;
		try {
			process = Runtime.getRuntime().exec("logcat -d");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuffer log = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.matches(filter)) {
					log.append(line).append("\n");
				}
			}
			return log.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}