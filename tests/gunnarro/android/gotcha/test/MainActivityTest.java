package gunnarro.android.gotcha.test;

import gunnarro.android.gotcha.R;
import gunnarro.android.gotcha.view.MainActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.TextView;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity activity;
	private TextView view;
	private String resourceString;

	public MainActivityTest() {
		super("Gotcha Tests", MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = this.getActivity();
		Log.i("GOTCHA - INFO", activity.getTitle().toString());
		view = (TextView) activity.findViewById(R.id.main_view);
		resourceString = activity.getString(R.string.app_name);
	}

	/**
     * Verifies that the activity under test can be launched.
     */
    public void testActivityTestCaseSetUpProperly() {
        assertNotNull("activity should be launched successfully", getActivity());
    }
    
	public void testPreconditions() throws Exception {
		assertNotNull(activity);
		assertNotNull(view);
		assertNotNull(resourceString);
	}

	public void testText() {
		assertNotNull(view.getText());
	}
}
