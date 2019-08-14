package com.vanks.groupmessage.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.vanks.groupmessage.R;

public class PermissionsActivity extends AppCompatActivity {
    final static int REQUEST_PERMISSIONS_ID = 1;
    Button grantPermissionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_permission);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (allPermissionsAreGranted()) {
            startMainActivity();
        } else {
            grantPermissionsButton = (Button)findViewById(R.id.grant_permission_button);
            grantPermissionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestForNeededAppPermissions();
                }
            });
        }
    }

    private void requestForNeededAppPermissions () {
        if (atLeastOnePermissionNotGranted()) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.SEND_SMS
                    },
                    REQUEST_PERMISSIONS_ID);
        }
    }

    private boolean allPermissionsAreGranted () {
        return permissionGranted(Manifest.permission.READ_CONTACTS) && permissionGranted(Manifest.permission.SEND_SMS);
    }

    private boolean atLeastOnePermissionNotGranted () {
        return !permissionGranted(Manifest.permission.READ_CONTACTS) || !permissionGranted(Manifest.permission.SEND_SMS);
    }

    private boolean permissionGranted (String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_ID: {
                if (grantResults.length == 0) {
                    break;
                }
                boolean goToSettingsToAppovePermission = false;
                boolean allPermissionsAccepted = false;
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    Log.i("PermissionActivity", permissions[i] + " : " + grantResults[i]);
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        allPermissionsAccepted = false;
                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(PermissionsActivity.this, permission);
                        if (!showRationale) {
                            Log.i("PermissionActivity","Denied + Do Not Ask : " + permissions[i]);
                            goToSettingsToAppovePermission = true;
                        }
                    }
                }

                if (allPermissionsAccepted) {
                    startMainActivity();
                }
                break;
            }
        }

    }

    private void startMainActivity () {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
