package com.luxurypixel.watchface;

public class WideNextAlarmComplicationDataSourceService extends NextAlarmComplicationDataSourceService {
    @Override
    protected boolean usesWideSlot() {
        return true;
    }
}
