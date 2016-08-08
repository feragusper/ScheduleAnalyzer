package com.feragusper.scheduleanalyzer;

import com.feragusper.scheduleanalyzer.domain.executor.PostExecutionThread;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

/**
 * @author Fernando.Perez
 * @since 0.1
 * <p>
 * MainThread (UI Thread) implementation based on a {@link Scheduler}
 * which will execute actions on the Android UI thread
 */
public class UIThread implements PostExecutionThread {

    @Override
    public Scheduler getScheduler() {
        return AndroidSchedulers.mainThread();
    }
}
