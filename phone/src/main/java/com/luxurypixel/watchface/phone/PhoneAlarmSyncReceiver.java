package com.luxurypixel.watchface.phone;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PhoneAlarmSyncReceiver extends BroadcastReceiver {
    public static final String ACTION_SYNC_ALARM = "com.luxurypixel.watchface.phone.SYNC_ALARM";
    private static final String TAG = "LuxuryPixelAlarmSync";

    @Override
    public void onReceive(Context context, Intent intent) {
        syncNextAlarm(context.getApplicationContext());
    }

    static void syncNextAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo alarm = alarmManager != null ? alarmManager.getNextAlarmClock() : null;

        PutDataMapRequest mapRequest = PutDataMapRequest.create(AlarmSyncContract.PATH_NEXT_ALARM);
        mapRequest.getDataMap().putLong(AlarmSyncContract.KEY_UPDATED_AT, System.currentTimeMillis());
        if (alarm == null) {
            mapRequest.getDataMap().putBoolean(AlarmSyncContract.KEY_HAS_ALARM, false);
            mapRequest.getDataMap().putLong(AlarmSyncContract.KEY_TRIGGER_TIME, 0L);
            mapRequest.getDataMap().putString(AlarmSyncContract.KEY_TEXT, "");
            Log.i(TAG, "sync no phone alarm");
        } else {
            long triggerTime = alarm.getTriggerTime();
            String text = formatAlarmTime(context, triggerTime);
            mapRequest.getDataMap().putBoolean(AlarmSyncContract.KEY_HAS_ALARM, true);
            mapRequest.getDataMap().putLong(AlarmSyncContract.KEY_TRIGGER_TIME, triggerTime);
            mapRequest.getDataMap().putString(AlarmSyncContract.KEY_TEXT, text);
            Log.i(TAG, "sync phone alarm=" + text);
        }

        PutDataRequest request = mapRequest.asPutDataRequest().setUrgent();
        Wearable.getDataClient(context).putDataItem(request);
    }

    private static String formatAlarmTime(Context context, long triggerTimeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(triggerTimeMillis);
        String pattern = DateFormat.is24HourFormat(context) ? "HH:mm" : "h:mm";
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(calendar.getTime());
    }
}
