package com.feragusper.scheduleanalyzer.view.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

import com.feragusper.scheduleanalyzer.JobExecutor;
import com.feragusper.scheduleanalyzer.R;
import com.feragusper.scheduleanalyzer.UIThread;
import com.feragusper.scheduleanalyzer.domain.interactor.DefaultSubscriber;
import com.feragusper.scheduleanalyzer.domain.interactor.GetAllEventsUseCase;
import com.feragusper.scheduleanalyzer.domain.model.Event;

import java.util.List;

/**
 * @author Fernando.Perez
 * @since 0.1
 */
public class EventListViewModel implements ViewModel {

    public ObservableInt infoMessageVisibility;
    public ObservableInt progressVisibility;
    public ObservableInt recyclerViewVisibility;
    public ObservableField<String> infoMessage;
    private Context context;
    private DataListener dataListener;
    private GetAllEventsUseCase getAllEventsUseCase;
    private List<Event> events;

    public EventListViewModel(Context context, DataListener dataListener) {
        this.context = context;
        this.dataListener = dataListener;

        infoMessageVisibility = new ObservableInt(View.VISIBLE);
        progressVisibility = new ObservableInt(View.INVISIBLE);
        recyclerViewVisibility = new ObservableInt(View.INVISIBLE);
        infoMessage = new ObservableField<>();
    }

    @Override
    public void destroy() {
        context = null;
        dataListener = null;
        getAllEventsUseCase.unsubscribe();
    }

    public void loadEvents(long mTimeInMillisFrom, long mTimeInMillisTo) {
        progressVisibility.set(View.VISIBLE);
        recyclerViewVisibility.set(View.INVISIBLE);

        getAllEventsUseCase = new GetAllEventsUseCase(new JobExecutor(), new UIThread(), mTimeInMillisFrom, mTimeInMillisTo);
        getAllEventsUseCase.execute(new GetAllEventsSubscriber());
    }

    public interface DataListener {
        void onEventsChanged(List<Event> events);
    }

    private final class GetAllEventsSubscriber extends DefaultSubscriber<List<Event>> {

        @Override
        public void onCompleted() {
            if (dataListener != null) dataListener.onEventsChanged(events);
            progressVisibility.set(View.INVISIBLE);
            if (!events.isEmpty()) {
                recyclerViewVisibility.set(View.VISIBLE);
            } else {
                infoMessage.set(context.getString(R.string.text_empty_events));
                infoMessageVisibility.set(View.VISIBLE);
            }
        }

        @Override
        public void onError(Throwable e) {
            progressVisibility.set(View.INVISIBLE);
            infoMessage.set(context.getString(R.string.error_loading_events));
            infoMessageVisibility.set(View.VISIBLE);
        }

        @Override
        public void onNext(List<Event> events) {
            EventListViewModel.this.events = events;
        }
    }
}