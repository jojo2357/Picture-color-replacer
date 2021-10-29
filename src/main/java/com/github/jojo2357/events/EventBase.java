package com.github.jojo2357.events;

import com.github.jojo2357.events.events.MouseInputEvent;

public abstract class EventBase {
    private final EventTypes eventType;

    public EventBase(EventTypes eventType) {
        this.eventType = eventType;
    }

    public EventBase() {
        this(EventTypes.EmptyEvent);
    }

    public EventTypes getEventType() {
        return this.eventType;
    }

    public abstract EventBase copy();
}
