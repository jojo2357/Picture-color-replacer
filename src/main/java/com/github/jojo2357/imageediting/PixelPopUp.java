package com.github.jojo2357.imageediting;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.events.EventManager;
import com.github.jojo2357.events.EventPriorities;
import com.github.jojo2357.events.EventTypes;
import com.github.jojo2357.events.GameTimes;
import com.github.jojo2357.events.events.MouseInputEvent;
import com.github.jojo2357.menus.PictureEditorMenu;
import com.github.jojo2357.rendering.OpenScreen;
import com.github.jojo2357.rendering.RenderableObject;
import com.github.jojo2357.rendering.ScreenManager;
import com.github.jojo2357.rendering.typeface.Colors;
import com.github.jojo2357.rendering.typeface.TextRenderer;
import com.github.jojo2357.util.Dimensions;
import com.github.jojo2357.util.PixelData;
import com.github.jojo2357.util.Point;

import static com.github.jojo2357.events.EventTypes.RenderEvent;

public class PixelPopUp extends RenderableObject {
    private final PixelData data;
    private PixelData finalData;
    private Point location = new Point(200, 200);
    private Dimensions size = new Dimensions(80, 60);

    public PixelPopUp(PixelData data) {
        registerListeners(RenderEvent, EventTypes.MouseInputEvent);
        this.data = data;
    }

    @Override
    public <T extends EventBase> boolean notify(T event) {
        switch (event.getEventType()) {
            case RenderEvent:
                if (EventManager.currentScreen == OpenScreen.PICTURE_EDITING) {
                    if (EventManager.currentPhase == GameTimes.SECOND_RENDER) {
                        drawMyself();
                        drawMyMirror();
                    }else if (EventManager.currentPhase == GameTimes.FIRST_RENDER)
                        drawMyFirstSelf();
                }
                break;
            case MouseInputEvent:
                if (EventManager.currentScreen == OpenScreen.PICTURE_EDITING) {
                    if (((MouseInputEvent) event).getPosition().isInBoundingBox(location, size, 1f)) {
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    private void drawMyFirstSelf() {
        PictureEditorMenu.highLightPixel(data.imageLocation);
        ScreenManager.drawLine(PictureEditorMenu.getPixelCenter(data.imageLocation), PictureEditorMenu.getPixelCenter(data.imageLocation).getClosest(location, location.copy().stepX(size.getWidth()), location.copy().stepY(size.getHeight()), location.copy().add(new Point(size))), 0, 0, 0);
        ScreenManager.drawLine(PictureEditorMenu.getPixelCenter(data.imageLocation).step(1, 1), PictureEditorMenu.getPixelCenter(data.imageLocation).getClosest(location, location.copy().stepX(size.getWidth()), location.copy().stepY(size.getHeight()), location.copy().add(new Point(size))).step(1, 1), 255, 255, 255);
    }

    private void drawMyself() {
        ScreenManager.drawBoxFilled(location, location.add(new Point(size)), 128, 128, 128);
        ScreenManager.drawBox(location, location.add(new Point(size)), 0, 0, 0);
        ScreenManager.drawBoxFilled(location.copy().step(size.getWidth() - 2, 2), location.copy().step(size.getWidth() - 17, size.getHeight() - 3), data.getR(), data.getG(), data.getB());
        ScreenManager.drawBox(location.copy().step(size.getWidth() - 2, 2), location.copy().step(size.getWidth() - 17, size.getHeight() - 3), 0, 0, 0);
        TextRenderer.render("R\b " + " ".repeat(3 - ("" + data.getR()).length()) + data.getR(), location.add(new Point(10,10)), 1000, Colors.RED, Colors.WHITE);
        TextRenderer.render("G\b " + " ".repeat(3 - ("" + data.getG()).length()) + data.getG(), location.add(new Point(10,30)), 1000, Colors.GREEN, Colors.WHITE);
        TextRenderer.render("B\b " + " ".repeat(3 - ("" + data.getB()).length()) + data.getB(), location.add(new Point(10,50)), 1000, Colors.BLUE, Colors.WHITE);
    }

    private void drawMyMirror() {
        ScreenManager.drawBoxFilled(location.copy().stepX(size.getWidth() * 2), location.add(new Point(size)), 128, 128, 128);
        ScreenManager.drawBox(location.copy().stepX(size.getWidth() * 2), location.add(new Point(size)), 0, 0, 0);
        ScreenManager.drawBoxFilled(location.copy().stepX(size.getWidth()).step(2, 2), location.copy().stepX(size.getWidth()).step(17, size.getHeight() - 3), data.getR(), data.getG(), data.getB());
        ScreenManager.drawBox(location.copy().stepX(size.getWidth()).copy().step(2, 2), location.copy().stepX(size.getWidth()).copy().step(17, size.getHeight() - 3), 0, 0, 0);
        TextRenderer.render("R\b " + " ".repeat(3 - ("" + data.getR()).length()) + data.getR(), location.copy().stepX(size.getWidth()).add(new Point(25,10)), 1000, Colors.RED, Colors.WHITE);
        TextRenderer.render("G\b " + " ".repeat(3 - ("" + data.getG()).length()) + data.getG(), location.copy().stepX(size.getWidth()).add(new Point(25,30)), 1000, Colors.GREEN, Colors.WHITE);
        TextRenderer.render("B\b " + " ".repeat(3 - ("" + data.getB()).length()) + data.getB(), location.copy().stepX(size.getWidth()).add(new Point(25,50)), 1000, Colors.BLUE, Colors.WHITE);
    }

    @Override
    public void reloadTextures() {

    }

    @Override
    public EventPriorities getPrio(EventTypes event) {
        return super.getPrio(event);
    }

    public void destroy() {
        EventManager.disposeListener(this);
    }
}
