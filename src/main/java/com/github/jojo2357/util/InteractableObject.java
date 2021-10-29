package com.github.jojo2357.util;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.events.events.MouseInputEvent;
import com.github.jojo2357.rendering.RenderableObject;
import com.github.jojo2357.rendering.ScreenManager;

import java.util.function.Function;

public abstract class InteractableObject extends RenderableObject {
    private final Point CENTER;
    private final Dimensions SIZE;
    private final Function<MouseInputEvent, Boolean> FILTER;
    private final Function<MouseInputEvent, Boolean> CALLBACK;

    public InteractableObject(Point center, Dimensions size, Function<MouseInputEvent, Boolean> filter, Function<MouseInputEvent, Boolean> callback) {
        CENTER = center;
        SIZE = size;
        FILTER = filter;
        CALLBACK = callback;
    }

    public InteractableObject(Point center, Dimensions size, Function<MouseInputEvent, Boolean> filter, Function<MouseInputEvent, Boolean> callback, String texture) {
        super(texture);
        CENTER = center;
        SIZE = size;
        FILTER = filter;
        CALLBACK = callback;
    }

    public boolean wasHovered() {
        return ScreenManager.lastMouseEvent.getPosition().isInBoundingBox(CENTER, SIZE, 1);
    }

    @Override
    public <T extends EventBase> boolean notify(T event) {
        if (event instanceof MouseInputEvent)
            return checkAndDoCallback((MouseInputEvent) event);
        return false;
    }

    protected boolean checkAndDoCallback(MouseInputEvent event) {
        if (FILTER.apply(event)) {
            return CALLBACK.apply(event);
        }
        return false;
    }
}
