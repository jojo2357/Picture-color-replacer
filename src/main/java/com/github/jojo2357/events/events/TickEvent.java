package com.github.jojo2357.events.events;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.events.EventTypes;

public class TickEvent extends EventBase {
    private static int tickNumber = 0;
    private final int myTickNumber;

    public TickEvent(){
        super(EventTypes.TickEvent);
        this.myTickNumber = ++tickNumber;
    }

    protected TickEvent(int tnum){
        super(EventTypes.TickEvent);
        this.myTickNumber = tnum;
    }

    @Override
    public EventBase copy() {
        return new TickEvent(myTickNumber);
    }
}
