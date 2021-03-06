package com.feragusper.scheduleanalyzer.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feragusper.scheduleanalyzer.R;
import com.feragusper.scheduleanalyzer.domain.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Fernando.Perez
 * @since 0.1
 */
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private List<Event> items = new ArrayList<>();

    public void setEvents(List<Event> events) {
        this.items = events;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false));
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

    public void updateData(List<Event> events) {
        items = events;
        notifyDataSetChanged();
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
