package com.luxurypixel.watchface;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.AlarmClock;
import android.os.RemoteException;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.wear.watchface.complications.data.ComplicationData;
import androidx.wear.watchface.complications.data.ComplicationType;
import androidx.wear.watchface.complications.data.NoDataComplicationData;
import androidx.wear.watchface.complications.data.PlainComplicationText;
import androidx.wear.watchface.complications.data.ShortTextComplicationData;
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService;
import androidx.wear.watchface.complications.datasource.ComplicationRequest;

import java.util.Calendar;
import java.util.Locale;

public class NextAlarmComplicationDataSourceService extends ComplicationDataSourceService {
    private static final String TAG = "NextAlarmComplication";

    @Override
    public void onComplicationRequest(
            @NonNull ComplicationRequest request,
            @NonNull ComplicationRequestListener listener
    ) {
        Log.i(TAG, "request type=" + request.getComplicationType()
                + " id=" + request.getComplicationInstanceId()
                + " provider=" + getClass().getSimpleName()
                + " wideSlot=" + usesWideSlot());
        if (request.getComplicationType() != ComplicationType.SHORT_TEXT) {
            Log.i(TAG, "unsupported type, sending no data");
            sendComplicationData(listener, new NoDataComplicationData());
            return;
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo watchAlarm = alarmManager != null ? alarmManager.getNextAlarmClock() : null;

        SyncedAlarm syncedAlarm = getSyncedPhoneAlarm();
        if (syncedAlarm != null) {
            AlarmDisplay display = formatAlarmDisplay(syncedAlarm.triggerTimeMillis);
            if (display.needsWideSlot != usesWideSlot()) {
                Log.i(TAG, "synced phone alarm skipped for wideSlot=" + usesWideSlot());
                sendComplicationData(listener, new NoDataComplicationData());
                return;
            }
            Log.i(TAG, "synced phone alarm=" + display.text);
            sendComplicationData(listener, buildData(
                    display.text,
                    buildAlarmTapAction(watchAlarm, syncedAlarm.triggerTimeMillis)
            ));
            return;
        }

        if (watchAlarm == null) {
            Log.i(TAG, "no next alarm, sending no data");
            sendComplicationData(listener, new NoDataComplicationData());
            return;
        }

        AlarmDisplay display = formatAlarmDisplay(watchAlarm.getTriggerTime());
        if (display.needsWideSlot != usesWideSlot()) {
            Log.i(TAG, "next alarm skipped for wideSlot=" + usesWideSlot());
            sendComplicationData(listener, new NoDataComplicationData());
            return;
        }

        Log.i(TAG, "next alarm=" + display.text);
        sendComplicationData(listener, buildData(display.text, buildAlarmTapAction(watchAlarm, watchAlarm.getTriggerTime())));
    }

    @Nullable
    @Override
    public ComplicationData getPreviewData(@NonNull ComplicationType type) {
        if (type != ComplicationType.SHORT_TEXT) {
            return new NoDataComplicationData();
        }
        return buildData(usesWideSlot() ? "Sun 17:00" : "07:30", buildGenericAlarmTapAction());
    }

    protected boolean usesWideSlot() {
        return false;
    }

    private ComplicationData buildData(String text, PendingIntent tapAction) {
        PlainComplicationText complicationText = new PlainComplicationText.Builder(text).build();
        return new ShortTextComplicationData.Builder(complicationText, complicationText)
                .setTapAction(tapAction)
                .build();
    }

    private PendingIntent buildAlarmTapAction(
            @Nullable AlarmManager.AlarmClockInfo watchAlarm,
            long displayedTriggerTimeMillis
    ) {
        Log.i(TAG, "using generic alarm list for tap");
        return buildGenericAlarmTapAction();
    }

    private PendingIntent buildGenericAlarmTapAction() {
        Intent intent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
        intent.setPackage("com.google.android.deskclock");
        return PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private void sendComplicationData(ComplicationRequestListener listener, ComplicationData data) {
        try {
            listener.onComplicationData(data);
        } catch (RemoteException ignored) {
            // The system process requesting the data went away.
        }
    }

    @Nullable
    private SyncedAlarm getSyncedPhoneAlarm() {
        SharedPreferences prefs = getSharedPreferences(AlarmSyncContract.PREFS_NAME, MODE_PRIVATE);
        if (!prefs.getBoolean(AlarmSyncContract.KEY_HAS_ALARM, false)) {
            return null;
        }
        long triggerTime = prefs.getLong(AlarmSyncContract.KEY_TRIGGER_TIME, 0L);
        if (triggerTime <= System.currentTimeMillis()) {
            return null;
        }
        return new SyncedAlarm(
                formatAlarmTime(triggerTime),
                triggerTime
        );
    }

    private AlarmDisplay formatAlarmDisplay(long triggerTimeMillis) {
        Calendar trigger = Calendar.getInstance();
        trigger.setTimeInMillis(triggerTimeMillis);

        Calendar today = Calendar.getInstance();
        Calendar tomorrow = (Calendar) today.clone();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);

        if (isSameDay(trigger, today) || isSameDay(trigger, tomorrow)) {
            return new AlarmDisplay(formatAlarmTime(triggerTimeMillis), false);
        }

        String timePattern = DateFormat.is24HourFormat(this) ? "HH:mm" : "h:mm";
        String pattern = daysBetween(today, trigger) <= 7 ? "EEE " + timePattern : "MMM d";
        return new AlarmDisplay(
                new java.text.SimpleDateFormat(pattern, Locale.getDefault()).format(trigger.getTime()),
                true
        );
    }

    private String formatAlarmTime(long triggerTimeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(triggerTimeMillis);
        String pattern = DateFormat.is24HourFormat(this) ? "HH:mm" : "h:mm";
        return new java.text.SimpleDateFormat(pattern, Locale.getDefault()).format(calendar.getTime());
    }

    private static boolean isSameDay(Calendar first, Calendar second) {
        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR)
                && first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR);
    }

    private static int daysBetween(Calendar start, Calendar end) {
        Calendar startDay = (Calendar) start.clone();
        startDay.set(Calendar.HOUR_OF_DAY, 0);
        startDay.set(Calendar.MINUTE, 0);
        startDay.set(Calendar.SECOND, 0);
        startDay.set(Calendar.MILLISECOND, 0);

        Calendar endDay = (Calendar) end.clone();
        endDay.set(Calendar.HOUR_OF_DAY, 0);
        endDay.set(Calendar.MINUTE, 0);
        endDay.set(Calendar.SECOND, 0);
        endDay.set(Calendar.MILLISECOND, 0);

        return Math.max(0, (int) ((endDay.getTimeInMillis() - startDay.getTimeInMillis()) / 86400000L));
    }

    private static final class SyncedAlarm {
        final String text;
        final long triggerTimeMillis;

        SyncedAlarm(String text, long triggerTimeMillis) {
            this.text = text;
            this.triggerTimeMillis = triggerTimeMillis;
        }
    }

    private static final class AlarmDisplay {
        final String text;
        final boolean needsWideSlot;

        AlarmDisplay(String text, boolean needsWideSlot) {
            this.text = text;
            this.needsWideSlot = needsWideSlot;
        }
    }
}
