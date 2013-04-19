package gunnarro.android.gotcha;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public abstract class AppPreferences {

	public static final String SMS_BLACK_LIST = "sms_blacklist";
	public static final String AUTHENTICATED_USERS = "autenticated_users";

	private static final String APP_SHARED_PREFS = "user_preferences";
	public static final String DEFAULT_VALUE = "";
	public static final String SEPARATOR = ",";

	private SharedPreferences appSharedPrefs;
	private Editor prefsEditor;

	public AppPreferences(Context context) {
		this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
		this.prefsEditor = appSharedPrefs.edit();
	}

	public abstract List<String> getList();

	public abstract String getListAsString();

	public abstract boolean removeAllList();

	public abstract boolean updateList(String item);

	public abstract boolean removeList(String item);

	public abstract boolean listContains(String item);

	public SharedPreferences getAppSharedPrefs() {
		return appSharedPrefs;
	}

	public Editor getPrefsEditor() {
		return prefsEditor;
	}
}
