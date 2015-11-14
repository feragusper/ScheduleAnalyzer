package com.feragusper.scheduleanalyzer.domain.model;

/**
 * @author Fernando.Perez
 * @since 0.1
 */
public class Event {

    private String title;
    private long duration;

    public String getTitle() {
        return title;
    }

    public long getDuration() {
        return duration;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void addDuration(long duration) {
        this.duration += duration;
    }
}
