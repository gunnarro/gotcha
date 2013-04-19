package gunnarro.android.gotcha;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;

public class ListAppPreferencesImpl extends AppPreferences {

	private String type;

	public ListAppPreferencesImpl(Context context, String type) {
		super(context);
		this.type = type;
	}

	public List<String> getList() {
		String list = getAppSharedPrefs().getString(type, DEFAULT_VALUE);
		if (list != null) {
			return new ArrayList<String>(Arrays.asList(list.split(SEPARATOR)));
		}
		return new ArrayList<String>();
	}

	public String getListAsString() {
		return getAppSharedPrefs().getString(type, DEFAULT_VALUE);
	}

	public boolean removeAllList() {
		getPrefsEditor().putString(type, DEFAULT_VALUE);
		return getPrefsEditor().commit();
	}

	public boolean updateList(String item) {
		StringBuffer blackListedNumbers = new StringBuffer(getListAsString());
		if (blackListedNumbers.length() == 0) {
			blackListedNumbers.append(item);
		} else if (!blackListedNumbers.toString().contains(item) && item.length() > 1) {
			blackListedNumbers.append(SEPARATOR).append(item);
		}
		getPrefsEditor().putString(type, blackListedNumbers.toString());
		return getPrefsEditor().commit();
	}

	public boolean removeList(String item) {
		List<String> smsBlackList = getList();
		if (!smsBlackList.isEmpty() && smsBlackList.contains(item)) {
			StringBuffer blackListedNumbers = new StringBuffer();
			int i = smsBlackList.size();
			for (String blackListedNumber : smsBlackList) {
				i--;
				if (!blackListedNumber.equals(item)) {
					blackListedNumbers.append(blackListedNumber);
					if (i > 0) {
						blackListedNumbers.append(SEPARATOR);
					}
				}
			}
			getPrefsEditor().putString(type, blackListedNumbers.toString());
			return getPrefsEditor().commit();
		}
		return false;
	}

	public boolean listContains(String item) {
		for (String blackListedPhoneNumber : getList()) {
			if (blackListedPhoneNumber.equals(item)) {
				return true;
			}
		}
		return false;
	}
}
