package com.feragusper.scheduleanalyzer.view.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.feragusper.scheduleanalyzer.R;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author fernando.perez
 * @since 0.1
 */
public class EventFilterDialog extends DialogFragment {

    public static final String EXTRA_DATE_FROM = "extra.date.from";
    public static final String EXTRA_DATE_TO = "extra.date.to";
    public static final int SET_FILTER = 1;

    @Bind(R.id.et_date_from)
    TextView vDateFrom;

    @Bind(R.id.et_date_to)
    TextView vDateTo;
    private long mTimeInMillisFrom;
    private long mTimeInMillisTo;
    private EventFilterListener eventFilterListener;

    public EventFilterDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container);
        getDialog().setTitle(R.string.filter);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTimeInMillisFrom = getArguments().getLong(EXTRA_DATE_FROM);
        mTimeInMillisTo = getArguments().getLong(EXTRA_DATE_TO);

        vDateFrom.setText(DateUtils.formatDateTime(getContext(), mTimeInMillisFrom, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR));
        vDateTo.setText(DateUtils.formatDateTime(getContext(), mTimeInMillisTo, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.eventFilterListener = (EventFilterListener) activity;
    }

    @OnClick({R.id.et_date_from, R.id.et_date_to})
    public void displayDatePicker(final View view) {
        CalendarDatePickerDialogFragment dialog = new CalendarDatePickerDialogFragment();
        Calendar calendar = Calendar.getInstance();
        if (R.id.et_date_from == view.getId()) {
            calendar.setTimeInMillis(mTimeInMillisFrom);
        } else {
            calendar.setTimeInMillis(mTimeInMillisTo);
        }

        dialog.initialize(new CalendarDatePickerDialogFragment.OnDateSetListener() {
            @Override
            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                long timeInMillis = calendar.getTimeInMillis();
                String dateString = DateUtils.formatDateTime(getContext(), timeInMillis, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
                if (R.id.et_date_from == view.getId()) {
                    mTimeInMillisFrom = timeInMillis;
                    vDateFrom.setText(dateString);
                } else {
                    mTimeInMillisTo = timeInMillis;
                    vDateTo.setText(dateString);
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.setThemeDark(false);
        dialog.show(getFragmentManager(), "DATE_PICKER_TAG");
    }

    @OnClick(R.id.btn_apply)
    public void applyFilter(View view) {
        eventFilterListener.onApply(mTimeInMillisFrom, mTimeInMillisTo);
        dismiss();
    }

    public static EventFilterDialog newInstance(long extraDateFrom, long extraDateTo) {
        EventFilterDialog fragment = new EventFilterDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_DATE_FROM, extraDateFrom);
        bundle.putLong(EXTRA_DATE_TO, extraDateTo);
        fragment.setArguments(bundle);
        return fragment;
    }
}