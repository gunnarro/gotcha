package gunnarro.android.gotcha.view;

import gunnarro.android.gotcha.AppConstants;
import gunnarro.android.gotcha.R;
import gunnarro.android.gotcha.calls.CallRegister;
import gunnarro.android.gotcha.calls.Statistic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CallStatisticActivity extends Activity implements OnClickListener {

	private String sortBy = "incoming";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the main.xml layout file.
		setContentView(R.layout.call_statistic_layout);
		ImageButton refreshButton = (ImageButton) findViewById(R.id.refresh_statistic_btn);
		refreshButton.setOnClickListener(this);
		ImageButton clearButton = (ImageButton) findViewById(R.id.clear_statistic_btn);
		clearButton.setOnClickListener(this);
		ImageButton emailButton = (ImageButton) findViewById(R.id.send_stat_to_email_btn);
		emailButton.setOnClickListener(this);

		Spinner spinner = (Spinner) findViewById(R.id.sort_by_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.call_statistic_column_names, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new SortByOnItemSelectedListener());
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.refresh_statistic_btn :
				updateCallStatistic();
				break;
			case R.id.clear_statistic_btn :
				clearStatistic();
				break;
			case R.id.send_stat_to_email_btn :
				sendStatisticToEmail();
				break;
		}
	}

	private void clearStatistic() {
		TableLayout table = (TableLayout) findViewById(R.id.tableLayout1);
		Log.i("updateCallStatistic", "...child: " + table.getChildCount());
		if (table.getChildCount() > 3) {
			table.removeViews(3, table.getChildCount() - 3);
			Log.i("", "Removed rows: " + (table.getChildCount() - 3));
		}
	}

	private void updateCallStatistic() {
		Statistic summaryStatistic = new Statistic(new Date(), "Summary");
		TableLayout table = (TableLayout) findViewById(R.id.tableLayout1);
		// Remove all rows before updating the table, except for the table
		// header rows.
		clearStatistic();
		List<Statistic> statisticList = CallRegister.statistic(this);
		Log.i("updateCallStatistic", "...child: " + table.getChildCount() + ", stat: " + statisticList.size() + ", sort by: " + sortBy);
		Date startDate = Calendar.getInstance().getTime();
		Date endDate = startDate;
		if (statisticList.size() > 1) {
			startDate = statisticList.get(0).getCallDate();
			endDate = statisticList.get(statisticList.size() - 1).getCallDate();
		}

		Collections.sort(statisticList, new Comparator<Statistic>() {
			public int compare(Statistic stat1, Statistic stat2) {
				if (sortBy.equals("duration incoming")) {
					return stat1.sortByDurationIncoming(stat2);
				} else if (sortBy.equals("duration outgoing")) {
					return stat1.sortByDurationOutgoing(stat2);
				} else if (sortBy.equals("incoming")) {
					return stat1.sortByNumberOfIncoming(stat2);
				} else if (sortBy.equals("outgoing")) {
					return stat1.sortByNumberOfOutgoing(stat2);
				} else if (sortBy.equals("missed")) {
					return stat1.sortByNumberOfMissed(stat2);
				}
				// default sort by number of incoming
				return stat1.sortByNumberOfIncoming(stat2);
			};
		});
		// Show the top ten numbers only...
		List<Statistic> topTenList = statisticList;
		if (statisticList.size() > 10) {
			topTenList = statisticList.subList(0, 10);
		}
		for (Statistic statistic : topTenList) {
			startDate = startDate.before(statistic.getCallDate()) ? startDate : statistic.getCallDate();
			endDate = endDate.after(statistic.getCallDate()) ? endDate : statistic.getCallDate();
			table.addView(createTableRow(statistic, table.getChildCount()));
			summaryStatistic.updateIncomingCalls(statistic.getNumberOfIncoming());
			summaryStatistic.updateOutgoingCalls(statistic.getNumberOfOutgoing());
			summaryStatistic.updateMissedCalls(statistic.getNumberOfMissed());
			summaryStatistic.updateIncomingDuration(statistic.getDurationIncoming());
			summaryStatistic.updateOutgoingDuration(statistic.getDurationOutgoing());
			Log.i("", statistic.toString());
		}
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		String periode = formatter.format(startDate) + " - " + formatter.format(endDate);
		TextView tableHeaderTxt = (TextView) findViewById(R.id.tableHeaderTxt);
		tableHeaderTxt.setText("Top 10 Calls for " + periode);
		tableHeaderTxt.setTextColor(Color.WHITE);
		// Add row with totals at the end of the table
		TableRow row = new TableRow(this);
		row.setBackgroundColor(Color.BLACK);
		row.setPadding(1, 1, 1, 1);
		row.setMinimumHeight(2);
		table.addView(row);
		table.addView(createTableRow(summaryStatistic, 1));
		Log.i("updateCallStatistic", " distinct numbers: " + statisticList.size());
	}

	private TableRow createTableRow(Statistic statistic, int rowNumber) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		TableRow row = new TableRow(this);
		int bgColor = Color.WHITE;
		if (rowNumber % 2 == 0) {
			bgColor = Color.LTGRAY;
		}
		row.addView(createTextView(lookupPhoneNumber(statistic.getPhoneNumber().getLocalNumber()), bgColor, Color.BLACK, Gravity.LEFT));
		row.addView(createTextView(Integer.toString(statistic.getNumberOfIncoming()), bgColor, Color.parseColor(AppConstants.NAVY_HEX), Gravity.RIGHT));
		row.addView(createTextView(Integer.toString(statistic.getNumberOfOutgoing()), bgColor, Color.parseColor(AppConstants.DARK_GREEN_HEX), Gravity.RIGHT));
		row.addView(createTextView(Integer.toString(statistic.getNumberOfMissed()), bgColor, Color.RED, Gravity.RIGHT));
		row.addView(createTextView(formatter.format(new Date(statistic.getDurationIncoming() * 1000)), bgColor, Color.parseColor(AppConstants.NAVY_HEX), Gravity.LEFT));
		row.addView(createTextView(formatter.format(new Date(statistic.getDurationOutgoing() * 1000)), bgColor, Color.parseColor(AppConstants.DARK_GREEN_HEX), Gravity.LEFT));

		row.setBackgroundColor(bgColor);
		row.setPadding(1, 1, 1, 1);
		return row;
	}

	private TextView createTextView(String value, int bgColor, int txtColor, int gravity) {
		TextView txtView = new TextView(this);
		txtView.setText(value);
		txtView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
		txtView.setTextSize(12);
		txtView.setLineSpacing(1, 1);
		// txtView.setPadding(1, 1, 1, 1);
		txtView.setGravity(gravity);
		txtView.setBackgroundColor(bgColor);
		txtView.setTextColor(txtColor);
		return txtView;
	}

	private void sendStatisticToEmail() {

	}

	private String lookupPhoneNumber(String phoneNumber) {
		String name = phoneNumber;
		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
		// Query the filter URI
		Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				name = cursor.getString(0);
			}
			cursor.close();
		}
		return name;
	}

	public void getDataTraffic() {
		// Monitor network connections (Wi-Fi, GPRS, UMTS, etc.)
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	}

	public class SortByOnItemSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			sortBy = parent.getItemAtPosition(pos).toString();
			Log.i("", "Sort by: " + sortBy);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing.
		}
	}
}