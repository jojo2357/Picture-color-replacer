package com.github.jojo2357.events;

public enum EventTypes {
    EmptyEvent("empty", false),
    MouseInputEvent("mouse_input", true),
    TickEvent("tick", false),
    RenderEvent("render",true),
    KeyInputEvent("key", true);

    public final boolean canBeConsumed;
    private final String name;

    EventTypes(String name, boolean consumable) {
        this.name = name;
        canBeConsumed = consumable;
    }

    public String getName() {
        return this.name;
    }
}
