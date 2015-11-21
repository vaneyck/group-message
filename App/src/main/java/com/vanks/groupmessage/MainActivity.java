package com.vanks.groupmessage;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int URL_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            getLoaderManager().initLoader(URL_LOADER, null, (LoaderManager.LoaderCallbacks<Object>) getActivity());
            return rootView;
        }
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
