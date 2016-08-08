package com.feragusper.scheduleanalyzer.domain.repository;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.Time;

import com.feragusper.scheduleanalyzer.ScheduleAnalyzerApplication;
import com.feragusper.scheduleanalyzer.common.Duration;
import com.feragusper.scheduleanalyzer.domain.model.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Fernando.Perez on 7/8/2016.
 */
public class EventRepository {

    public static final String[] INSTANCE_PROJECTION = new String[]{CalendarContract.Events._ID, // 0
            CalendarContract.Instances.TITLE, // 1
            CalendarContract.Instances.DURATION,
            CalendarContract.Instances.DTSTART,
            CalendarContract.Instances.SELF_ATTENDEE_STATUS,
            CalendarContract.Instances.DTEND
    };

    public Observable<List<Event>> getAll() {
        return Observable.create(new Observable.OnSubscribe<List<Event>>() {

            @Override
            public void call(Subscriber<? super List<Event>> subscriber) {
                Cursor cursor;
                ContentResolver contentResolver = ScheduleAnalyzerApplication.getInstance().getContentResolver();

                // Construct the query with the desired date range.
                Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();

                // Specify the date range you want to search for recurring
                // event instances
                long mExtraDateFrom = 0;
                if (mExtraDateFrom == 0) {
                    Calendar beginTime = Calendar.getInstance();
                    beginTime.set(2014, 9, 23, 8, 0);
                    mExtraDateFrom = beginTime.getTimeInMillis();
                }
                ContentUris.appendId(builder, mExtraDateFrom);

                long mExtraDateTo = 0;
                if (mExtraDateTo == 0) {
                    @SuppressWarnings("deprecation") Time time = new Time();
                    time.setToNow();
                    mExtraDateTo = time.toMillis(false);

                }
                ContentUris.appendId(builder, mExtraDateTo);

                // Submit the query
                cursor = contentResolver.query(builder.build(), INSTANCE_PROJECTION, CalendarContract.Instances.SELF_ATTENDEE_STATUS + "=1", null, CalendarContract.Instances.DTSTART);

                List<Event> events = new ArrayList<>();
                while (cursor != null && cursor.moveToNext()) {
                    boolean durationAdded = false;
                    Event event = new Event();
                    event.setTitle(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE)));

                    Long eventDtend = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTEND));
                    Long eventDtstart = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART));
                    String eventDuration = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DURATION));

                    long durationInMillis;
                    if (eventDuration != null) {
                        Duration duration = new Duration();
                        try {
                            duration.parse(eventDuration);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        durationInMillis = duration.getMillis();
                    } else {
                        durationInMillis = eventDtend - eventDtstart;
                    }

                    event.setDuration(durationInMillis);

                    for (Event eventInList : events) {
                        if (eventInList.getTitle().equals(event.getTitle())) {
                            eventInList.addDuration(durationInMillis);
                            durationAdded = true;
                            break;
                        }
                    }

                    if (!durationAdded) {
                        events.add(event);
                    }
                }


                subscriber.onNext(events);
                subscriber.onCompleted();
            }
        });
    }
}
