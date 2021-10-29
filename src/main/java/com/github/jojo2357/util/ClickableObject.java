package com.github.jojo2357.util;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.rendering.RenderableObject;

public class ClickableObject extends RenderableObject {
    @Override
    public <T extends EventBase> boolean notify(T event) {
        return false;
    }

    @Override
    public void reloadTextures() {

    }
}
