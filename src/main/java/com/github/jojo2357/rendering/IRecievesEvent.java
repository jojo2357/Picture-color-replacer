package com.github.jojo2357.rendering;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.events.EventManager;
import com.github.jojo2357.events.EventPriorities;
import com.github.jojo2357.events.EventTypes;

import java.util.ArrayList;

public interface IRecievesEvent {
    <T extends EventBase> boolean notify(T event);

    default void registerAllListeners() {
        for (EventTypes event : EventTypes.values()) {
            registerListener(event);
        }
    }

    default void registerListener(EventTypes event) {
        EventManager.addListeningObject(this, event);
    }

    default void registerListeners(EventBase...events) {
        for (EventBase event : events) {
            registerListener(event.getEventType());
        }
    }

    default void registerListeners(EventTypes...events) {
        for (EventTypes event : events) {
            registerListener(event);
        }
    }

    default EventPriorities getPrio(EventTypes event){
        return EventPriorities.LOWEST;
    }
}
