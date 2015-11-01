package com.feragusper.scheduleanalyzer.view.fragment;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.feragusper.scheduleanalyzer.R;
import com.feragusper.scheduleanalyzer.common.Duration;
import com.feragusper.scheduleanalyzer.domain.model.Event;
import com.feragusper.scheduleanalyzer.view.adapter.EventListAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static final String[] INSTANCE_PROJECTION = new String[]{CalendarContract.Events._ID, // 0
            CalendarContract.Instances.TITLE, // 1
            CalendarContract.Instances.DURATION,
            CalendarContract.Instances.DTSTART,
            CalendarContract.Instances.SELF_ATTENDEE_STATUS,
            CalendarContract.Instances.DTEND
    };


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Cursor cursor = null;
        ContentResolver contentResolver = getActivity().getContentResolver();

        // Construct the query with the desired date range.
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();

        // Specify the date range you want to search for recurring
        // event instances
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2014, 9, 23, 8, 0);
        long startMillis = beginTime.getTimeInMillis();
        ContentUris.appendId(builder, startMillis);
        Time time = new Time();
        time.setToNow();
        ContentUris.appendId(builder, time.toMillis(false));

        // Submit the query
        cursor = contentResolver.query(builder.build(), INSTANCE_PROJECTION, CalendarContract.Instances.SELF_ATTENDEE_STATUS + "=1", null, CalendarContract.Instances.DTSTART);

        List<Event> events = new ArrayList<>();
        while (cursor.moveToNext()) {
            boolean durationAdded = false;
            Event event = new Event();
            event.setTitle(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE)));

            Long eventDtend = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTEND));
            Long eventDtstart = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART));
            String eventDuration = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DURATION));
            int self = cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.SELF_ATTENDEE_STATUS));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            long durationInMillis = 0;
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

            if (eventDtend != null && eventDtstart != null) {
//                ((TextView) view.findViewById(scheduleanalyzer.feragusper.com.scheduleanalyzer.R.id.date)).setText(dateFormat.format(eventDtstart) + " - " + (durationInMillis / 60000));
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
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.myList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        recyclerView.setAdapter(new EventListAdapter(events));
    }
}
