package com.feragusper.scheduleanalyzer.view.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;

import com.feragusper.scheduleanalyzer.domain.model.Event;

/**
 * View model for each item in the repositories RecyclerView
 */
public class ItemEventViewModel extends BaseObservable implements ViewModel {

    private Event event;
    private Context context;

    public ItemEventViewModel(Context context, Event event) {
        this.event = event;
        this.context = context;
    }

    public String getTitle() {
        return event.getTitle();
    }

    public String getWatchers() {
        return String.valueOf(event.getDuration());
    }

    public String getForks() {
        return String.valueOf(event.getDuration());
    }

    // Allows recycling ItemRepoViewModels within the recyclerview adapter
    public void setEvent(Event event) {
        this.event = event;
        notifyChange();
    }

    @Override
    public void destroy() {
        //In this case destroy doesn't need to do anything because there is not async calls
    }

}
