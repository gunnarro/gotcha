package gunnarro.android.gotcha.view;

import gunnarro.android.gotcha.AppConstants;
import gunnarro.android.gotcha.R;
import gunnarro.android.gotcha.sms.SMS;
import gunnarro.android.gotcha.sms.SMSReader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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

public class SMSStatisticActivity extends Activity implements OnClickListener {

	private String viewBy = "Year";
	private String sortBy = "period";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the main.xml layout file.
		setContentView(R.layout.sms_statistic_layout);
		ImageButton refreshButton = (ImageButton) findViewById(R.id.refresh_statistic_btn);
		refreshButton.setOnClickListener(this);

		Spinner viewBySpinner = (Spinner) findViewById(R.id.view_by_spinner);
		ArrayAdapter<CharSequence> viewByAdapter = ArrayAdapter.createFromResource(this, R.array.sms_view_by_options, android.R.layout.simple_spinner_item);
		viewByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		viewBySpinner.setAdapter(viewByAdapter);
		viewBySpinner.setOnItemSelectedListener(new ViewByOnItemSelectedListener());

		Spinner sortBySpinner = (Spinner) findViewById(R.id.sort_by_spinner);
		ArrayAdapter<CharSequence> sortByAdapter = ArrayAdapter.createFromResource(this, R.array.sms_sort_by_options, android.R.layout.simple_spinner_item);
		sortByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sortBySpinner.setAdapter(sortByAdapter);
		sortBySpinner.setOnItemSelectedListener(new SortByOnItemSelectedListener());
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.refresh_statistic_btn :
				updateSMSStatistic();
				break;
		}
	}

	private void clearStatistic() {
		TableLayout table = (TableLayout) findViewById(R.id.tableLayout1);
		Log.i("clearStatistic", "...child: " + table.getChildCount());
		if (table.getChildCount() > 3) {
			table.removeViews(3, table.getChildCount() - 3);
			Log.i("clearStatistic", "Removed rows: " + (table.getChildCount() - 3));
		}
	}

	private void updateSMSStatistic() {
		TableLayout table = (TableLayout) findViewById(R.id.tableLayout1);
		// Remove all rows before updating the table, except for the table
		// header rows.
		clearStatistic();
		SMSReader smsReader = new SMSReader(this);
		boolean isGroupByAddress = viewBy.equalsIgnoreCase("number");
		List<SMS> smsInbox = smsReader.getSMSOutboxGroupBy(viewBy, isGroupByAddress);

		Collections.sort(smsInbox, new Comparator<SMS>() {
			public int compare(SMS sms1, SMS sms2) {
				if (sortBy.equalsIgnoreCase("periode")) {
					return sms2.getPeriod().compareTo(sms1.getPeriod());
				} else if (sortBy.equalsIgnoreCase("sent")) {
					return sms1.getNumberOfSent() > sms2.getNumberOfReceived() ? 0 : 1;
				} else if (sortBy.equalsIgnoreCase("received")) {
					return sms1.getNumberOfReceived() > sms2.getNumberOfReceived() ? 0 : 1;
				}
				// default sort by period descending
				return sms2.getPeriod().compareTo(sms1.getPeriod());
			};
		});

		Date startDate = Calendar.getInstance().getTime();
		Date endDate = startDate;
		// if (smsInbox.size() > 1) {
		// startDate = smsInbox.get(0).getDate();
		// endDate = smsInbox.get(smsInbox.size() - 1).getDate();
		// }
		SMS summarySMS = new SMS("Total", 0);
		for (SMS sms : smsInbox) {
			summarySMS.increaseNumberOfReceived(sms.getNumberOfReceived());
			summarySMS.increaseNumberOfSent(sms.getNumberOfSent());
			table.addView(createTableRow(sms, table.getChildCount()));
		}
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		String periode = formatter.format(startDate) + " - " + formatter.format(endDate);
		TextView tableHeaderTxt = (TextView) findViewById(R.id.tableHeaderTxt);
		tableHeaderTxt.setText("SMS usage for: " + periode);
		tableHeaderTxt.setTextColor(Color.WHITE);
		// Add row with totals at the end of the table
		TableRow row = new TableRow(this);
		row.setBackgroundColor(Color.BLACK);
		row.setPadding(1, 1, 1, 1);
		row.setMinimumHeight(2);
		table.addView(row);
		table.addView(createTableRow(summarySMS, 1));
	}

	private TableRow createTableRow(SMS sms, int rowNumber) {
		TableRow row = new TableRow(this);
		int bgColor = Color.WHITE;
		if (rowNumber % 2 == 0) {
			bgColor = Color.LTGRAY;
		}
		row.addView(createTextView(sms.getPeriod(), bgColor, Color.BLACK, Gravity.CENTER));
		row.addView(createTextView(Integer.toString(sms.getNumberOfReceived()), bgColor, Color.parseColor(AppConstants.NAVY_HEX), Gravity.RIGHT));
		row.addView(createTextView(Integer.toString(sms.getNumberOfSent()), bgColor, Color.parseColor(AppConstants.DARK_GREEN_HEX), Gravity.RIGHT));

		row.setBackgroundColor(bgColor);
		row.setPadding(1, 1, 1, 1);
		return row;
	}

	private TextView createTextView(String value, int bgColor, int txtColor, int gravity) {
		TextView txtView = new TextView(this);
		txtView.setText(value);
		txtView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
		txtView.setTextSize(12);
		txtView.setLineSpacing(1, 1);
		// txtView.setPadding(1, 1, 1, 1);
		txtView.setGravity(gravity);
		txtView.setBackgroundColor(bgColor);
		txtView.setTextColor(txtColor);
		return txtView;
	}

	public class ViewByOnItemSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			viewBy = parent.getItemAtPosition(pos).toString();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing.
		}
	}

	public class SortByOnItemSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			sortBy = parent.getItemAtPosition(pos).toString();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing.
		}
	}
}