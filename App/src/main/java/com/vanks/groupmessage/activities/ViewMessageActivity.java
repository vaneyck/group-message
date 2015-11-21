package com.vanks.groupmessage.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vanks.groupmessage.R;

/**
 * Created by vaneyck on 11/21/15.
 */
public class ViewMessageActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_message);
	}

	//>LoaderManager.LoaderCallbacks<Cursor> interface methods
	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
		Uri uri = ContactsContract.Groups.CONTENT_SUMMARY_URI;
		String[] projection = null;
		String selection = ContactsContract.Groups.ACCOUNT_TYPE + " NOT NULL AND " +
				ContactsContract.Groups.ACCOUNT_NAME + " NOT NULL AND " + ContactsContract.Groups.DELETED + "=0";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			selection += " AND " + ContactsContract.Groups.AUTO_ADD + "=0 AND " + ContactsContract.Groups.FAVORITES + "=0";
		}

		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Groups.TITLE + " ASC";
		Loader<Cursor> loader = new CursorLoader(getApplicationContext(), uri, projection, selection, selectionArgs, sortOrder);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		if(cursor == null || cursor.getCount() == 0) { return; }
		cursor.moveToFirst();
		do {
			String groupName = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE));
			Long groupId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Groups._ID));
			Log.i("MainActivity", groupId + " : " + groupName);
		} while(cursor.moveToNext());
		cursor.close();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {

	}
}
