package com.feragusper.scheduleanalyzer.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feragusper.scheduleanalyzer.R;
import com.feragusper.scheduleanalyzer.domain.model.Event;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private List<Event> items;

    public EventListAdapter(List<Event> items) {
        if (items == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }
        this.items = items;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_event, parent, false));
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        final Event event = items.get(position);
        holder.title.setText(event.getTitle());
        holder.duration.setText(
                String.format("%d hour, %d min",
                        TimeUnit.MILLISECONDS.toHours(event.getDuration()),
                        TimeUnit.MILLISECONDS.toMinutes(event.getDuration()) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(event.getDuration()))
                ));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public final static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView duration;

        public EventViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            duration = (TextView) itemView.findViewById(R.id.date);
        }
    }
}
