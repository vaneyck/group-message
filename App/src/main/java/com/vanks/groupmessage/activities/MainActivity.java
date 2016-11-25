package com.vanks.groupmessage.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.orm.query.Select;
import com.vanks.groupmessage.R;
import com.vanks.groupmessage.arrayadapters.main.MessageListItemArrayAdapter;
import com.vanks.groupmessage.models.persisted.Message;
import com.vanks.groupmessage.utils.PreferenceUtil;
import com.vanks.groupmessage.utils.ScheduleUtil;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static int REQUEST_PERMISSIONS_ID = 1;
    ListView messageListView;
    LinearLayout ctaGroupMessage;
    TextView nextDispatchPickupTimeTextView;
    MessageListItemArrayAdapter messageListItemArrayAdapter;
    ArrayList<Message> messageArrayList;
    public static final String REFRESH_UI_INTENT_FILTER = "com.vanks.groupmessage.main_activity.refresh_ui";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateMessageActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseUi();
        requestForNeededAppPermissions();
        registerReceiver(refreshUiBroadcastReceiver, refreshUiIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(refreshUiBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }
        if (id == R.id.action_delete_message) {

        }
        return super.onOptionsItemSelected(item);
    }

    AdapterView.OnItemClickListener messageItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String messageIdAsString = ((TextView) view.findViewById(R.id.messageIdTextView)).getText().toString();
            Long messageId = Long.parseLong(messageIdAsString);
            Intent intent = new Intent(getApplicationContext(), ViewMessageActivity.class);
            intent.putExtra("messageId", messageId);
            startActivity(intent);
        }
    };

    private void initialiseUi () {
        messageArrayList = (ArrayList<Message>) Select.from(Message.class).orderBy("id desc").list();
        messageListView =  (ListView) findViewById(R.id.messageListView);
        nextDispatchPickupTimeTextView = (TextView) findViewById(R.id.next_dispatch_pickup_time);
        setNextDispatchPickupTimeTextView();
        ctaGroupMessage = (LinearLayout) findViewById(R.id.cta_group_message);
        if(messageArrayList.size() > 0) {
            messageListItemArrayAdapter = new MessageListItemArrayAdapter(this, R.layout.landing_page_message_list_item, messageArrayList);
            messageListView.setAdapter(messageListItemArrayAdapter);
            messageListItemArrayAdapter.notifyDataSetChanged();
            messageListView.setOnItemClickListener(messageItemListener);
        } else {
            ctaGroupMessage.setVisibility(View.VISIBLE);
        }
        ScheduleUtil.scheduleMessageSendService(getApplicationContext());
    }

    private void setNextDispatchPickupTimeTextView () {
        Date date = PreferenceUtil.getNextDispatchRunTime(getApplicationContext());
        if(PreferenceUtil.isAppOn(getApplicationContext())) {
            nextDispatchPickupTimeTextView.setText(date.toString());
            nextDispatchPickupTimeTextView.setTextColor(Color.BLACK);
        } else {
            nextDispatchPickupTimeTextView.setText(R.string.app_off_label);
            nextDispatchPickupTimeTextView.setTextColor(Color.RED);
        }
    }

    private BroadcastReceiver refreshUiBroadcastReceiver = new BroadcastReceiver () {

        @Override
        public void onReceive(Context context, Intent intent) {
            initialiseUi();
        }
    };

    private IntentFilter refreshUiIntentFilter = new IntentFilter(REFRESH_UI_INTENT_FILTER);

    private void requestForNeededAppPermissions () {
        if (atLeastOnePermissionNotGranted()) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.READ_PHONE_STATE
                    },
                    REQUEST_PERMISSIONS_ID);
        }
    }

    private boolean atLeastOnePermissionNotGranted () {
        return  permissionGranted(Manifest.permission.READ_CONTACTS)&&
                permissionGranted(Manifest.permission.SEND_SMS) &&
                permissionGranted(Manifest.permission.READ_PHONE_STATE);
    }

    private boolean permissionGranted (String permission) {
        return ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED;
    }
}
