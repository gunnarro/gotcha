package gunnarro.android.gotcha.view;

import gunnarro.android.gotcha.AppPreferences;
import gunnarro.android.gotcha.R;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public abstract class ConfigurationActivity extends ListActivity {

	private AppPreferences appPreferences;

	/** Items entered by the user is stored in this ArrayList variable */
	private List<String> localList;

	/** Declaring an ArrayAdapter to set items to ListView */
	private ArrayAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the main.xml layout file.
		setContentView(R.layout.list_layout);
	}

	protected void setupEventHandlers() {
		/** Defining the ArrayAdapter to set items to ListView */
		adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, localList);
		/** Setting the adapter to the ListView */
		setListAdapter(adapter);

		findViewById(R.id.btnAdd).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText inputField = (EditText) findViewById(R.id.input_field);
				String item = inputField.getText().toString();
				if (!item.isEmpty()) {
					if (addLocalList(item)) {
						adapter.notifyDataSetChanged();
						appPreferences.updateList(item);
					}
					inputField.setText("");
				}
			}
		});

		findViewById(R.id.btnDel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Getting the checked items from the listview
				SparseBooleanArray checkedItemPositions = getListView().getCheckedItemPositions();
				int itemCount = getListView().getCount();
				for (int i = itemCount - 1; i >= 0; i--) {
					if (checkedItemPositions.get(i)) {
						String nr = localList.get(i);
						adapter.remove(nr);
						appPreferences.removeList(nr);
					}
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	private boolean addLocalList(String item) {
		for (String s : localList) {
			if (s.equalsIgnoreCase(item)) {
				return false;
			}
		}
		return localList.add(item);
	}

	public void setAppPreferences(AppPreferences appPreferences) {
		this.appPreferences = appPreferences;
		this.localList = appPreferences.getList();
	}

	public void setListTitle(String title) {
		TextView titleField = (TextView) findViewById(R.id.title);
		titleField.setText(this.getTitle());
	}
}