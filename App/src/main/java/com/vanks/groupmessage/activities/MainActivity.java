package com.vanks.groupmessage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vanks.groupmessage.R;
import com.vanks.groupmessage.arrayadapters.main.MessageListItemArrayAdapter;
import com.vanks.groupmessage.models.Message;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView messageListView;
    MessageListItemArrayAdapter messageListItemArrayAdapter;
    ArrayList<Message> messageArrayList;

    private static final int URL_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ViewMessageActivity.class));
            }
        });
        initialiseUi();
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


    private void initialiseUi () {
        messageArrayList = new ArrayList<Message>();
        for(int x=0; x< 20;x++) {
            Message testMessage = new Message("Lorem ipsum dolor sit amet, his sint aperiam id, has agam justo offendit ea. Ancillae perpetua repudiandae ut vis " + x, x + 0L);
            messageArrayList.add(testMessage);
        }
        messageListView =  (ListView) findViewById(R.id.messageListView);
        messageListItemArrayAdapter = new MessageListItemArrayAdapter(this, R.layout.landing_page_message_list_item, messageArrayList);
        messageListView.setAdapter(messageListItemArrayAdapter);
        messageListItemArrayAdapter.notifyDataSetChanged();
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
}
