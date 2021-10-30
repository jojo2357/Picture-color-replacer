package com.github.jojo2357.events.events;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.events.EventTypes;
import com.github.jojo2357.rendering.ScreenManager;
import com.github.jojo2357.util.Point;

public class MouseInputEvent extends EventBase {
    private final Point mouseLocation;
    private final byte buttonsData;
    private final int mouseKlicks;

    public MouseInputEvent() {
        this(new Point(0, 0), (byte) 0, 0);
    }

    public MouseInputEvent(Point point, byte mouseButtonData, int klicks) {
        super(EventTypes.MouseInputEvent);
        this.mouseLocation = point;
        this.buttonsData = mouseButtonData;
        this.mouseKlicks = klicks;
        if (mouseButtonData != -1 && ((mouseButtonData & 1) == 1)) {
            try {
                //System.out.println(point.copy().multiply(2).subtract(EventManager.map.getMapLocation()).multiply(1f/EventManager.map.getZoomFactor()));
            } catch (Exception e) {

            }
        }
    }

    public byte getRawData() {
        return this.buttonsData;
    }

    public Point getPosition() {
        return this.mouseLocation;
    }

    public int wheelClicks() {
        return this.mouseKlicks;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MouseInputEvent))
            return false;
        return ((MouseInputEvent) other).mouseLocation.equals(this.mouseLocation) && ((MouseInputEvent) other).buttonsData == this.buttonsData && ((MouseInputEvent) other).mouseKlicks == this.mouseKlicks;
    }

    @Override
    public EventBase copy() {
        return new MouseInputEvent(mouseLocation, buttonsData, mouseKlicks);
    }

    public boolean bottomRightClicked() {
        return justReleased(MouseButtons.LEFT) && mouseLocation.getX() >= ScreenManager.windowSize.getWidth() - 100 && mouseLocation.getY() >= ScreenManager.windowSize.getHeight() - 100;
    }

    public boolean justReleased(MouseButtons button) {
        return ScreenManager.lastMouseEvent.getClick(button) && !getClick(button);
    }

    public boolean getClick(MouseButtons button) {
        return (buttonsData >> button.ID & 0x1) == 1;
    }

    public boolean bottomLeftClicked() {
        return justReleased(MouseButtons.LEFT) && mouseLocation.getX() <= 100 && mouseLocation.getY() >= ScreenManager.windowSize.getHeight() - 100;
    }

    public enum MouseButtons {
        LEFT(0),
        MIDDLE(1),
        RIGHT(2);

        private final int ID;

        MouseButtons(int id) {
            this.ID = id;
        }
    }
}
