package com.luxurypixel.watchface.phone;

import android.app.Activity;
import android.os.Bundle;

public class SyncActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PhoneAlarmSyncReceiver.syncNextAlarm(getApplicationContext());
        finish();
    }
}
