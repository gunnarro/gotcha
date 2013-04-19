package gunnarro.android.gotcha.db;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class UsersDbAdapter {

	// Database fields
	public static final String KEY_ID = "id";
	public static final String KEY_CREATED_DATE = "created_date";
	public static final String KEY_FIRSTNAME = "firstname";
	public static final String KEY_LASTNAME = "lastname";
	public static final String KEY_MOBILE_PHONE_NUMBER = "mobile_phone_number";
	public static final String KEY_EMAIL_ADDRESS = "email_address";
	private static final String DATABASE_TABLE_NAME = "Users";
	private Context context;
	private SQLiteDatabase database;
	private UsersDatabaseHelper dbHelper;

	public UsersDbAdapter(Context context) {
		this.context = context;
	}

	public UsersDbAdapter open() throws SQLException {
		dbHelper = new UsersDatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Create a new todo If the todo is successfully created return the new
	 * rowId for that note, otherwise return a -1 to indicate failure.
	 */
	public long createUser(String firstName, String lastName, String mobilePhoneNumber, String emailAddress) {
		ContentValues initialValues = createContentValues(firstName, lastName, mobilePhoneNumber, emailAddress);
		return database.insert(DATABASE_TABLE_NAME, null, initialValues);
	}

	/**
	 * Update the users table
	 */
	public boolean updateUser(long rowId, String firstName, String lastName, String mobilePhoneNumber, String emailAddress) {
		ContentValues updateValues = createContentValues(firstName, lastName, mobilePhoneNumber, emailAddress);
		return database.update(DATABASE_TABLE_NAME, updateValues, KEY_ID + "=" + rowId, null) > 0;
	}

	/**
	 * Deletes users table
	 */
	public boolean deleteUser(long rowId) {
		return database.delete(DATABASE_TABLE_NAME, KEY_ID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all users in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllUsers() {
		return database.query(DATABASE_TABLE_NAME, new String[]{KEY_ID, KEY_CREATED_DATE, KEY_FIRSTNAME, KEY_LASTNAME, KEY_MOBILE_PHONE_NUMBER,KEY_EMAIL_ADDRESS}, null, null,
				null, null, null);
	}

	/**
	 * Return a Cursor positioned at the defined users table
	 */
	public Cursor fetchUser(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, DATABASE_TABLE_NAME,
				new String[]{KEY_ID, KEY_CREATED_DATE, KEY_FIRSTNAME, KEY_LASTNAME, KEY_MOBILE_PHONE_NUMBER, KEY_EMAIL_ADDRESS}, KEY_ID + "=" + rowId, null, null, null, null,
				null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	private ContentValues createContentValues(String firstName, String lastName, String mobilePhoneNumber, String emailAddress) {
		ContentValues values = new ContentValues();
		values.put(KEY_CREATED_DATE, System.currentTimeMillis());
		values.put(KEY_FIRSTNAME, firstName);
		values.put(KEY_LASTNAME, lastName);
		values.put(KEY_MOBILE_PHONE_NUMBER, mobilePhoneNumber);
		values.put(KEY_EMAIL_ADDRESS, emailAddress);
		return values;
	}
}
