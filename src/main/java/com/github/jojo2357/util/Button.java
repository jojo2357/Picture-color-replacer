package com.github.jojo2357.util;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.events.EventTypes;
import com.github.jojo2357.events.events.MouseInputEvent;
import com.github.jojo2357.events.events.RenderEvent;
import com.github.jojo2357.rendering.ScreenManager;

import java.util.function.Consumer;
import java.util.function.Function;

public class Button extends InteractableObject {
    private final Consumer<RenderEvent> RENDER_CALLBACK;

    public Button(Point center, Dimensions size,
                  Function<MouseInputEvent, Boolean> filter,
                  Function<MouseInputEvent, Boolean> callback,
                  Consumer<RenderEvent> renderCallback) {
        super(center, size, filter, callback);
        RENDER_CALLBACK = renderCallback;
        registerListeners(EventTypes.RenderEvent, EventTypes.MouseInputEvent);
    }

    public Button(Point center, Dimensions size,
                  Function<MouseInputEvent, Boolean> filter,
                  Function<MouseInputEvent, Boolean> callback,
                  Consumer<RenderEvent> renderCallback, String filename) {
        super(center, size, filter, callback, filename);
        RENDER_CALLBACK = renderCallback;
        registerListeners(EventTypes.RenderEvent, EventTypes.MouseInputEvent);
    }

    public Button(Point center,
                  Function<MouseInputEvent, Boolean> filter,
                  Function<MouseInputEvent, Boolean> callback,
                  Consumer<RenderEvent> renderCallback, String filename) {
        super(center, filter, callback, filename);
        if (renderCallback == null)
            RENDER_CALLBACK = this::renderWhenNull;
        else
            RENDER_CALLBACK = renderCallback;
        registerListeners(EventTypes.RenderEvent, EventTypes.MouseInputEvent);
        super.loadImage(filename);
    }

    private void renderWhenNull(RenderEvent event) {
        ScreenManager.renderTexture(this.image, super.CENTER, 1, super.SIZE);
    }

    public Button(Point center,
                  Dimensions size,
                  Function<MouseInputEvent, Boolean> filter,
                  Function<MouseInputEvent, Boolean> callback,
                  Texture image) {
        super(center, filter, callback);
        RENDER_CALLBACK = this::renderWhenNull;
        registerListeners(EventTypes.RenderEvent, EventTypes.MouseInputEvent);
        super.image = image;
    }

    public void render(RenderEvent event) {
        RENDER_CALLBACK.accept(event);
    }

    public void renderWithHitbox() {

    }

    @Override
    public void reloadTextures() {

    }

    @Override
    public <T extends EventBase> boolean notify(T event) {
        if (event instanceof MouseInputEvent)
            return super.notify(event);
        if (event instanceof RenderEvent)
            RENDER_CALLBACK.accept((RenderEvent) event);
        return false;
    }
}
