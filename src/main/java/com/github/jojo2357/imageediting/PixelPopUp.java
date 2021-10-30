package com.github.jojo2357.imageediting;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.events.EventManager;
import com.github.jojo2357.events.EventPriorities;
import com.github.jojo2357.events.EventTypes;
import com.github.jojo2357.events.GameTimes;
import com.github.jojo2357.events.events.MouseInputEvent;
import com.github.jojo2357.menus.PictureEditorManager;
import com.github.jojo2357.menus.PictureEditorMenu;
import com.github.jojo2357.rendering.OpenScreen;
import com.github.jojo2357.rendering.RenderableObject;
import com.github.jojo2357.rendering.ScreenManager;
import com.github.jojo2357.rendering.typeface.Colors;
import com.github.jojo2357.rendering.typeface.TextRenderer;
import com.github.jojo2357.util.Dimensions;
import com.github.jojo2357.util.PixelData;
import com.github.jojo2357.util.Point;
import com.github.jojo2357.util.Texture;

import static com.github.jojo2357.events.EventTypes.RenderEvent;
import static com.github.jojo2357.events.events.MouseInputEvent.MouseButtons.LEFT;
import static org.lwjgl.opengl.GL11.glColor4f;

public class PixelPopUp extends RenderableObject {
    private final PixelData startingData;
    private final PictureEditorMenu parent;
    private final Point location = new Point(ScreenManager.windowSize.getWidth() / 2, 100);
    private final Dimensions size = new Dimensions(90, 60);
    private PixelData finalData;
    private int hoveredArrow = -1;

    public PixelPopUp(PixelData data, PictureEditorMenu parent) {
        this(data, data.copy(), parent);
    }

    public PixelPopUp(PixelData start, PixelData finalData, PictureEditorMenu parent) {
        registerListeners(RenderEvent, EventTypes.MouseInputEvent);
        this.startingData = start;
        this.finalData = finalData;
        this.parent = parent;
    }

    @Override
    public <T extends EventBase> boolean notify(T event) {
        if (PictureEditorManager.activeMenu == parent) {
            switch (event.getEventType()) {
                case RenderEvent:
                    if (EventManager.currentScreen == OpenScreen.PICTURE_EDITING) {
                        if (EventManager.currentPhase == GameTimes.THIRD_RENDER) {
                            drawMyself();
                            drawMyMirror();
                        } else if (EventManager.currentPhase == GameTimes.FIRST_RENDER)
                            drawMyFirstSelf();
                    }
                    break;
                case MouseInputEvent:
                    if (EventManager.currentScreen == OpenScreen.PICTURE_EDITING) {
                        //if (((MouseInputEvent) event).getPosition().isInBoundingBox(location, size, 1f)) {
                        return handleMouseInput((MouseInputEvent) event);
                        //}
                    }
                    break;
            }
        }
        return false;
    }

    private void drawMyself() {
        ScreenManager.drawBoxFilled(location, location.add(new Point(size)), 128, 128, 128);
        ScreenManager.drawBox(location, location.add(new Point(size)), 0, 0, 0);
        ScreenManager.drawBoxFilled(location.copy().step(2, 2), location.copy().step(17, size.getHeight() - 3), finalData.getR(), finalData.getG(), finalData.getB());
        ScreenManager.drawBox(location.copy().step(2, 2), location.copy().step(17, size.getHeight() - 3), 0, 0, 0);
        TextRenderer.render("R\b " + " ".repeat(3 - ("" + finalData.getR()).length()) + finalData.getR(), location.add(new Point(25, 10)), 1000, Colors.RED, Colors.WHITE);
        TextRenderer.render("G\b " + " ".repeat(3 - ("" + finalData.getG()).length()) + finalData.getG(), location.add(new Point(25, 30)), 1000, Colors.GREEN, Colors.WHITE);
        TextRenderer.render("B\b " + " ".repeat(3 - ("" + finalData.getB()).length()) + finalData.getB(), location.add(new Point(25, 50)), 1000, Colors.BLUE, Colors.WHITE);

        for (int i = 1; i <= 6; i++)
            renderArrow(i);
        /*ScreenManager.renderTexture(Texture.arrow, new Point((int)location.getX() + 80,(int)location.getY() + 5), 0.5f);
        ScreenManager.renderTexture(Texture.arrow, new Point((int)location.getX() + 80,(int)location.getY() + 12), 0.5f, 180, Texture.arrow.dimensions);
        ScreenManager.renderTexture(Texture.arrow, new Point((int)location.getX() + 80,(int)location.getY() + 25), 0.5f);
        ScreenManager.renderTexture(Texture.arrow, new Point((int)location.getX() + 80,(int)location.getY() + 32), 0.5f, 180, Texture.arrow.dimensions);
        ScreenManager.renderTexture(Texture.arrow, new Point((int)location.getX() + 80,(int)location.getY() + 45), 0.5f);
        ScreenManager.renderTexture(Texture.arrow, new Point((int)location.getX() + 80,(int)location.getY() + 52), 0.5f, 180, Texture.arrow.dimensions);
        */
    }

    private void drawMyMirror() {
        ScreenManager.drawBoxFilled(location, location.copy().step(-size.getWidth(), size.getHeight()), 128, 128, 128);
        ScreenManager.drawBox(location, location.copy().step(-size.getWidth(), size.getHeight()), 0, 0, 0);
        ScreenManager.drawBoxFilled(location.copy().step(-2, 2), location.copy().step(-17, size.getHeight() - 3), startingData.getR(), startingData.getG(), startingData.getB());
        ScreenManager.drawBox(location.copy().step(-2, 2), location.copy().step(-17, size.getHeight() - 3), 0, 0, 0);
        TextRenderer.render("R\b " + " ".repeat(3 - ("" + startingData.getR()).length()) + startingData.getR(), location.copy().add(new Point(10 - size.getWidth(), 10)), 1000, Colors.RED, Colors.WHITE);
        TextRenderer.render("G\b " + " ".repeat(3 - ("" + startingData.getG()).length()) + startingData.getG(), location.copy().add(new Point(10 - size.getWidth(), 30)), 1000, Colors.GREEN, Colors.WHITE);
        TextRenderer.render("B\b " + " ".repeat(3 - ("" + startingData.getB()).length()) + startingData.getB(), location.copy().add(new Point(10 - size.getWidth(), 50)), 1000, Colors.BLUE, Colors.WHITE);
    }

    private void drawMyFirstSelf() {
        parent.highLightPixel(startingData.imageLocation);
        ScreenManager.drawLine(parent.getPixelCenter(startingData.imageLocation), parent.getPixelCenter(startingData.imageLocation).getClosest(location, location.copy().stepX(size.getWidth()), location.copy().stepY(size.getHeight()), location.copy().add(new Point(size))), 0, 0, 0);
        ScreenManager.drawLine(parent.getPixelCenter(startingData.imageLocation).step(1, 1), parent.getPixelCenter(startingData.imageLocation).getClosest(location, location.copy().stepX(size.getWidth()), location.copy().stepY(size.getHeight()), location.copy().add(new Point(size))).step(1, 1), 255, 255, 255);
    }

    private boolean handleMouseInput(MouseInputEvent event) {
        if (event.getPosition().getX() < (int) location.getX() + 88 && event.getPosition().getX() > (int) location.getX() + 73) {
            hoveredArrow = (int) (event.getPosition().getY() - (int) location.getY() + 8) / 10;
            //System.out.println(hoveredArrow);
        } else hoveredArrow = -1;
        if (event.justReleased(LEFT)) {
            if (hoveredArrow > 0 && hoveredArrow <= 6) {
                switch (hoveredArrow - 1) {
                    case 0:
                        finalData.stepR(1);
                        parent.updateReplacement(startingData, finalData);
                        break;
                    case 1:
                        finalData.stepR(-1);
                        parent.updateReplacement(startingData, finalData);
                        break;
                    case 2:
                        finalData.stepG(1);
                        parent.updateReplacement(startingData, finalData);
                        break;
                    case 3:
                        finalData.stepG(-1);
                        parent.updateReplacement(startingData, finalData);
                        break;
                    case 4:
                        finalData.stepB(1);
                        parent.updateReplacement(startingData, finalData);
                        break;
                    case 5:
                        finalData.stepB(-1);
                        parent.updateReplacement(startingData, finalData);
                        break;
                }
            }
        }
        return false;
    }

    private void renderArrow(int windex) {
        if (windex == hoveredArrow) {
            glColor4f(0 / 255f, 255 / 255f, 0 / 255f, 1f);
        }
        ScreenManager.renderTexture(Texture.arrow, new Point((int) location.getX() + 80, location.getY() + 5 + 10 * (windex - 1)), 0.5f, windex % 2 == 0 ? 180 : 0, Texture.arrow.dimensions);
        glColor4f(1f, 1f, 1f, 1f);
    }

    @Override
    public EventPriorities getPrio(EventTypes event) {
        if (event == EventTypes.MouseInputEvent) {
            return EventPriorities.MIDDLE;
        }
        return super.getPrio(event);
    }

    @Override
    public void reloadTextures() {

    }

    public void destroy() {
        EventManager.disposeListener(this);
    }
}
