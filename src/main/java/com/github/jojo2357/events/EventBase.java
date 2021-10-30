package com.github.jojo2357.events;

public abstract class EventBase {
    private final EventTypes eventType;

    public abstract EventBase copy();

    public EventBase() {
        this(EventTypes.EmptyEvent);
    }

    public EventBase(EventTypes eventType) {
        this.eventType = eventType;
    }

    public EventTypes getEventType() {
        return this.eventType;
    }
}
