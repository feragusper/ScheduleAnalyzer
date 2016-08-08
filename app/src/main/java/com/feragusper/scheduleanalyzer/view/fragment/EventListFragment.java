package com.feragusper.scheduleanalyzer.view.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.feragusper.scheduleanalyzer.R;
import com.feragusper.scheduleanalyzer.common.Duration;
import com.feragusper.scheduleanalyzer.domain.model.Event;
import com.feragusper.scheduleanalyzer.presenter.EventListPresenter;
import com.feragusper.scheduleanalyzer.view.EventListView;
import com.feragusper.scheduleanalyzer.view.adapter.EventListAdapter;
import com.feragusper.scheduleanalyzer.view.dialog.EventFilterDialog;
import com.feragusper.scheduleanalyzer.view.viewmodel.EventListViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Fernando.Perez
 * @since 0.1
 */
public class EventListFragment extends Fragment implements EventListView, EventListViewModel.DataListener {

    public static final String[] INSTANCE_PROJECTION = new String[]{CalendarContract.Events._ID, // 0
            CalendarContract.Instances.TITLE, // 1
            CalendarContract.Instances.DURATION,
            CalendarContract.Instances.DTSTART,
            CalendarContract.Instances.SELF_ATTENDEE_STATUS,
            CalendarContract.Instances.DTEND
    };
    private static final int MY_PERMISSIONS_REQUEST_READ_WRITE_CALENDAR = 123;

    private EventListPresenter mPresenter;
    private EventListAdapter mEventListAdapter;
    private long mExtraDateFrom;
    private long mExtraDateTo;

    private EventListViewModel mainViewModel;
    private ViewDataBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new EventListPresenter(this);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_event_list,
                container,
                false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new EventListViewModel(getContext(), this);
//        mBinding.setViewModel(mainViewModel);

        @SuppressWarnings("ConstantConditions") RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.myList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        mEventListAdapter = new EventListAdapter();
        recyclerView.setAdapter(mEventListAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (checkPermission()) {
            displayEvents();
        }
    }

    private void displayEvents() {
        Cursor cursor;
        ContentResolver contentResolver = getActivity().getContentResolver();

        // Construct the query with the desired date range.
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();

        // Specify the date range you want to search for recurring
        // event instances
        if (mExtraDateFrom == 0) {
            Calendar beginTime = Calendar.getInstance();
            beginTime.set(2014, 9, 23, 8, 0);
            mExtraDateFrom = beginTime.getTimeInMillis();
        }
        ContentUris.appendId(builder, mExtraDateFrom);

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

        mEventListAdapter.updateData(events);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_event_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            FragmentManager fm = getFragmentManager();
            EventFilterDialog editNameDialog = EventFilterDialog.newInstance(mExtraDateFrom, mExtraDateTo);
            editNameDialog.show(fm, "fragment_event_filter");
            editNameDialog.setTargetFragment(this, 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (EventFilterDialog.SET_FILTER == requestCode) {
            mExtraDateFrom = data.getLongExtra(EventFilterDialog.EXTRA_DATE_FROM, 0);
            mExtraDateTo = data.getLongExtra(EventFilterDialog.EXTRA_DATE_TO, 0);
            displayEvents();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_CALENDAR)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write calendar permission is necessary to write event!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_READ_WRITE_CALENDAR);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_READ_WRITE_CALENDAR);
                }

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CALENDAR)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("Read calendar permission is necessary to write event!!!");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALENDAR}, MY_PERMISSIONS_REQUEST_READ_WRITE_CALENDAR);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALENDAR}, MY_PERMISSIONS_REQUEST_READ_WRITE_CALENDAR);
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_WRITE_CALENDAR:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayEvents();
                } else {
//code for deny
                }
                break;
        }
    }

    @Override
    public void onEventsChanged(List<Event> events) {

    }
}