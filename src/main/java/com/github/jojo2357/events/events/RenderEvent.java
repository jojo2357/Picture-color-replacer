package com.github.jojo2357.events.events;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.events.EventTypes;

public class RenderEvent extends EventBase {
    public RenderEvent(){
        super(EventTypes.RenderEvent);
    }

    @Override
    public EventBase copy() {
        return new RenderEvent();
    }
}
