package com.luxurypixel.watchface;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class PhoneAlarmDataListenerService extends WearableListenerService {
    private static final String TAG = "PhoneAlarmDataListener";

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            Uri uri = event.getDataItem().getUri();
            if (!AlarmSyncContract.PATH_NEXT_ALARM.equals(uri.getPath())) {
                continue;
            }

            boolean hasAlarm = false;
            long triggerTime = 0L;
            String text = "";
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                hasAlarm = dataMap.getBoolean(AlarmSyncContract.KEY_HAS_ALARM, false);
                triggerTime = dataMap.getLong(AlarmSyncContract.KEY_TRIGGER_TIME, 0L);
                text = dataMap.getString(AlarmSyncContract.KEY_TEXT, "");
            }

            SharedPreferences prefs = getSharedPreferences(AlarmSyncContract.PREFS_NAME, MODE_PRIVATE);
            prefs.edit()
                    .putBoolean(AlarmSyncContract.KEY_HAS_ALARM, hasAlarm)
                    .putLong(AlarmSyncContract.KEY_TRIGGER_TIME, triggerTime)
                    .putString(AlarmSyncContract.KEY_TEXT, text)
                    .apply();

            Log.i(TAG, "phone alarm changed hasAlarm=" + hasAlarm + " text=" + text);
            requestComplicationUpdate();
        }
    }

    private void requestComplicationUpdate() {
        ComponentName component = new ComponentName(
                this,
                NextAlarmComplicationDataSourceService.class
        );
        ComplicationDataSourceUpdateRequester.create(this, component).requestUpdateAll();
        ComponentName wideComponent = new ComponentName(
                this,
                WideNextAlarmComplicationDataSourceService.class
        );
        ComplicationDataSourceUpdateRequester.create(this, wideComponent).requestUpdateAll();
    }
}
