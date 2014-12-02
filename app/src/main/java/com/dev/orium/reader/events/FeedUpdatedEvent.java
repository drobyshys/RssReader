package com.dev.orium.reader.events;

/**
 * Created by y.drobysh on 01.12.2014.
 */
public class FeedUpdatedEvent {

    public long getId() {
        return id;
    }

    private long id;

    public FeedUpdatedEvent(long id) {
        this.id = id;
    }

}
