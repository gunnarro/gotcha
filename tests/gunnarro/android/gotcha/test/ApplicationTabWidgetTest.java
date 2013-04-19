package gunnarro.android.gotcha.test;

import gunnarro.android.gotcha.R;
import gunnarro.android.gotcha.view.ApplicationTabWidget;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

public class ApplicationTabWidgetTest extends ActivityInstrumentationTestCase2<ApplicationTabWidget> {

	private ApplicationTabWidget activity;
	private TextView view;
	private String resourceString;

	public ApplicationTabWidgetTest() {
		super("Gotcha Tests", ApplicationTabWidget.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = this.getActivity();
		Object tag = activity.getTabHost().getTag(R.drawable.log_tab);
	}

	 public void testStartActivity(){
//	        setActivity(startActivity(new Intent(Intent.ACTION_MAIN), null, null));
	    }
	 
	/**
	 * Verifies that the activity under test can be launched.
	 */
	public void testActivityTestCaseSetUpProperly() {
		assertNotNull("activity should be launched successfully", getActivity());
	}
}
