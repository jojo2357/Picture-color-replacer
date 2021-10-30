package com.github.jojo2357.events.events;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.events.EventTypes;

public class KeyInputEvent extends EventBase {
    public final char KEY;

    public KeyInputEvent(char pressed) {
        super(EventTypes.KeyInputEvent);
        KEY = pressed;
    }

    @Override
    public EventBase copy() {
        return this;
    }
}
