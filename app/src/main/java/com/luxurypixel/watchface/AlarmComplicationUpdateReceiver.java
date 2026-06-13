package com.luxurypixel.watchface;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester;

public class AlarmComplicationUpdateReceiver extends BroadcastReceiver {
    public static final String ACTION_REQUEST_UPDATE =
            "com.luxurypixel.watchface.REQUEST_ALARM_COMPLICATION_UPDATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        if (AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED.equals(action)
                || Intent.ACTION_BOOT_COMPLETED.equals(action)
                || ACTION_REQUEST_UPDATE.equals(action)) {
            ComponentName component = new ComponentName(
                    context,
                    NextAlarmComplicationDataSourceService.class
            );
            ComplicationDataSourceUpdateRequester.create(context, component).requestUpdateAll();
            ComponentName wideComponent = new ComponentName(
                    context,
                    WideNextAlarmComplicationDataSourceService.class
            );
            ComplicationDataSourceUpdateRequester.create(context, wideComponent).requestUpdateAll();
        }
    }
}
