package com.feragusper.scheduleanalyzer;

import android.app.Application;

/**
 * Created by Fernando.Perez on 7/8/2016.
 */
public class ScheduleAnalyzerApplication extends Application {
    private static ScheduleAnalyzerApplication INSTANCE;

    public static ScheduleAnalyzerApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
}
