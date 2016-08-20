package com.feragusper.scheduleanalyzer.view.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.feragusper.scheduleanalyzer.R;
import com.feragusper.scheduleanalyzer.databinding.ActivityEventListBinding;
import com.feragusper.scheduleanalyzer.domain.model.Event;
import com.feragusper.scheduleanalyzer.view.adapter.EventListAdapter;
import com.feragusper.scheduleanalyzer.view.dialog.EventFilterDialog;
import com.feragusper.scheduleanalyzer.view.dialog.EventFilterListener;
import com.feragusper.scheduleanalyzer.view.viewmodel.EventListViewModel;

import java.util.Calendar;
import java.util.List;

/**
 * @author Fernando.Perez
 * @since 0.1
 */
public class EventListActivity extends AppCompatActivity implements EventListViewModel.DataListener, EventFilterListener {

    //region Properties
    private static final int MY_PERMISSIONS_REQUEST_READ_WRITE_CALENDAR = 123;
    private ActivityEventListBinding binding;
    private EventListViewModel eventListViewModel;
    private long mTimeInMillisFrom;
    private long mTimeInMillisTo;
    //endregion

    //region Activity Implementation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_list);
        eventListViewModel = new EventListViewModel(this, this);
        binding.setViewModel(eventListViewModel);
        setSupportActionBar(binding.toolbar);
        setupRecyclerView(binding.reposRecyclerView);

        if (mTimeInMillisFrom == 0) {
            Calendar beginTime = Calendar.getInstance();
            beginTime.set(2014, 9, 23, 8, 0);
            mTimeInMillisFrom = beginTime.getTimeInMillis();
        }

        if (mTimeInMillisTo == 0) {
            @SuppressWarnings("deprecation") Time time = new Time();
            time.setToNow();
            mTimeInMillisTo = time.toMillis(false);
        }

        if (checkPermission()) {
            eventListViewModel.loadEvents(mTimeInMillisFrom, mTimeInMillisTo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_event_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            EventFilterDialog editNameDialog = EventFilterDialog.newInstance(mTimeInMillisFrom, mTimeInMillisTo);
            editNameDialog.show(fragmentManager, "fragment_event_filter");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventListViewModel.destroy();
    }
    //endregion

    //region EventListViewModel.DataListener Implementation
    @Override
    public void onEventsChanged(List<Event> events) {
        EventListAdapter adapter = (EventListAdapter) binding.reposRecyclerView.getAdapter();
        adapter.setEvents(events);
        adapter.notifyDataSetChanged();
    }
    //endregion

    //region OnRequestPermissionResultCallback Implementation
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_WRITE_CALENDAR:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    eventListViewModel.loadEvents(mTimeInMillisFrom, mTimeInMillisTo);
                } else {
//code for deny
                }
                break;
        }
    }
    //endregion

    //region EventFilterListener Implementation
    @Override
    public void onApply(long timeInMillisFrom, long timeInMillisTo) {
        mTimeInMillisFrom = timeInMillisFrom;
        mTimeInMillisTo = timeInMillisTo;

        eventListViewModel.loadEvents(mTimeInMillisFrom, mTimeInMillisTo);
    }
    //endregion

    //region Private Implementation
    private void setupRecyclerView(RecyclerView recyclerView) {
        EventListAdapter adapter = new EventListAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CALENDAR)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write calendar permission is necessary to write event!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(EventListActivity.this, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_READ_WRITE_CALENDAR);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_READ_WRITE_CALENDAR);
                }

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALENDAR)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("Read calendar permission is necessary to write event!!!");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(EventListActivity.this, new String[]{Manifest.permission.READ_CALENDAR}, MY_PERMISSIONS_REQUEST_READ_WRITE_CALENDAR);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, MY_PERMISSIONS_REQUEST_READ_WRITE_CALENDAR);
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
    //endregion

}
