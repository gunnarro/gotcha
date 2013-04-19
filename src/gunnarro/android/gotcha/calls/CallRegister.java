package gunnarro.android.gotcha.calls;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.CallLog;
import android.util.Log;

public class CallRegister {

	public static String getMissedCalls(Context context) {
		StringBuffer calls = new StringBuffer();
		String[] projection = new String[]{BaseColumns._ID, CallLog.Calls.DATE, CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DURATION};
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = null;
		// TYPE: The type of the call (incoming, outgoing or missed).
		// Select missed calls only
		String selection = CallLog.Calls.TYPE + " LIKE ?";
		String[] selectionArgs = new String[]{Integer.toString(CallLog.Calls.MISSED_TYPE)};
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");
		try {
			// default sort is date desc
			cursor = resolver.query(CallLog.Calls.CONTENT_URI, projection, selection, selectionArgs, CallLog.Calls.DEFAULT_SORT_ORDER);
			if (cursor != null && cursor.moveToFirst()) {
				do {
					String date = formatter.format(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE)))));
					String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
					String type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
					// String duration =
					// cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
					calls.append(date).append(" ").append(number).append(", type: ").append(type);
				} while (cursor.moveToNext());
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return calls.toString();
	}

	public static List<Statistic> statistic(Context context) {
		Map<String, Statistic> statMap = new HashMap<String, Statistic>();
		ContentResolver resolver = context.getContentResolver();
		String[] projection = new String[]{BaseColumns._ID, CallLog.Calls.DATE, CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DURATION};
		// String selection = "COUNT(DISTINCT " + CallLog.Calls.NUMBER + ")";
		// String[] selectionArgs = new
		// String[]{Integer.toString(CallLog.Calls.MISSED_TYPE)};
		Cursor cursor = null;
		try {
			cursor = resolver.query(CallLog.Calls.CONTENT_URI, projection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
			Log.i("", "count: " + cursor.getCount());
			if (cursor != null && cursor.moveToFirst()) {
				do {
					Date callDate = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE))));
					PhoneNumber phoneNumber = new PhoneNumber(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
					Statistic statistic = statMap.get(phoneNumber.getKey());
					if (statistic == null) {
						statistic = new Statistic(callDate, phoneNumber);
						statMap.put(statistic.getPhoneNumber().getKey(), statistic);
					}
					int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
					String type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
					switch (Integer.valueOf(type)) {
						case CallLog.Calls.MISSED_TYPE :
							statistic.increaseMissedCalls();
							break;
						case CallLog.Calls.INCOMING_TYPE :
							statistic.increaseIncomingCalls();
							statistic.updateIncomingDuration(duration);
							break;
						case CallLog.Calls.OUTGOING_TYPE :
							statistic.increaseOutgoingCalls();
							statistic.updateOutgoingDuration(duration);
							break;
					}
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("ERROR", e.toString());
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return new ArrayList<Statistic>(statMap.values());
//		java.util.Random rand = new java.util.Random();
//		return Arrays
//				.asList(new Statistic(new Date(System.currentTimeMillis() - 1000000), "004745465500", rand.nextInt(1000), rand.nextInt(1000), rand
//						.nextInt(1000), 2323, 4567),
//						new Statistic(new Date(System.currentTimeMillis() - 10000000), "+4745465500", rand.nextInt(1000), rand.nextInt(1000), rand
//								.nextInt(1000), 3456, 46345), new Statistic(new Date(System.currentTimeMillis() - 8000000), "45465500", rand.nextInt(1000),
//								rand.nextInt(1000), rand.nextInt(1000), 34, 6345), new Statistic(new Date(System.currentTimeMillis() - 60000000), "92671304",
//								rand.nextInt(1000), rand.nextInt(1000), rand.nextInt(1000), 34, 6345), new Statistic(new Date(
//								System.currentTimeMillis() - 20000000), "004500000000", rand.nextInt(1000), rand.nextInt(1000), rand.nextInt(1000), 34, 6345),
//						new Statistic(new Date(System.currentTimeMillis() - 50000000), "00000000", rand.nextInt(1000), rand.nextInt(1000), rand.nextInt(1000),
//								34, 6345),
//								new Statistic(new Date(System.currentTimeMillis() - 50000000), "00000000", rand.nextInt(1000), rand.nextInt(1000), rand.nextInt(1000),
//										34, 6345));
	}

}