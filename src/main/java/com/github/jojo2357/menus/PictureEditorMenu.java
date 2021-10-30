package com.github.jojo2357.menus;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.events.EventManager;
import com.github.jojo2357.events.EventPriorities;
import com.github.jojo2357.events.EventTypes;
import com.github.jojo2357.events.events.MouseInputEvent;
import com.github.jojo2357.imageediting.PixelPopUp;
import com.github.jojo2357.rendering.OpenScreen;
import com.github.jojo2357.rendering.RenderableObject;
import com.github.jojo2357.rendering.ScreenManager;
import com.github.jojo2357.util.Dimensions;
import com.github.jojo2357.util.PixelData;
import com.github.jojo2357.util.Point;
import com.github.jojo2357.util.fileutilis.ImageObject;

import java.util.HashMap;

import static com.github.jojo2357.events.events.MouseInputEvent.MouseButtons.LEFT;

public class PictureEditorMenu extends RenderableObject {
    private final ImageObject currentImage;
    private Point translation = new Point(ScreenManager.windowSize.getWidth() / 4, ScreenManager.windowSize.getHeight() / 2);
    private float zoom = 1;
    private boolean isSliding;
    private Point mouseDragOffset;
    private Dimensions adjustedDims;
    private PixelPopUp pop;
    private boolean hasActuallyMoved;
    private Point startingPos;

    HashMap<PixelData, PixelData> transformations = new HashMap<>();

    public PictureEditorMenu(ImageObject image) {
        currentImage = image;
        registerAllListeners();
    }

    public Point getPixelCenter(Point imageLocation) {
        return new Point(translation.getX() + zoom * (imageLocation.getX() + 0.5), translation.getY() + zoom * (imageLocation.getY() + 0.5));
    }

    public void highLightPixel(Point imageLocation) {
        ScreenManager.drawBox(new Point(translation.getX() + zoom * imageLocation.getX(), translation.getY() + zoom * (imageLocation.getY())),
                new Point(translation.getX() + zoom * (imageLocation.getX() + 1), translation.getY() + zoom * (imageLocation.getY() + 1)),
                0, 0, 0);
    }

    @Override
    public <T extends EventBase> boolean notify(T event) {
        if (PictureEditorManager.activeMenu == this) {
            switch (event.getEventType()) {
                case RenderEvent:
                    if (EventManager.currentScreen == OpenScreen.PICTURE_EDITING) {
                        switch (EventManager.currentPhase) {
                            case FIRST_RENDER:
                                ScreenManager.renderTextureLimited(currentImage, translation.add(new Point(currentImage.width / 2 * zoom, currentImage.heigth / 2 * zoom)), 1, this.rotation, this.adjustedDims, new Point(0, 150), new Dimensions(ScreenManager.windowSize.getWidth() / 2, ScreenManager.windowSize.getHeight() - 150));
                                ScreenManager.renderByteArray(currentImage.otherText, currentImage.startingData, currentImage.width, currentImage.heigth, translation.add(new Point(currentImage.width / 2 * zoom + ScreenManager.windowSize.getWidth() / 2, currentImage.heigth / 2 * zoom)), 1, this.rotation, this.adjustedDims);//, new Point(ScreenManager.windowSize.getWidth() / 2, 150), new Dimensions(ScreenManager.windowSize.getWidth() / 2, ScreenManager.windowSize.getHeight() - 150));
                                //ScreenManager.renderTextureLimited(currentImage.otherText, translation.add(new Point(currentImage.width / 2 * zoom + ScreenManager.windowSize.getWidth() / 2, currentImage.heigth / 2 * zoom)), 1, this.rotation, this.adjustedDims, new Point(ScreenManager.windowSize.getWidth() / 2, 150), new Dimensions(ScreenManager.windowSize.getWidth() / 2, ScreenManager.windowSize.getHeight() - 150));
                                break;
                        }
                    }
                    break;
                case MouseInputEvent:
                    if (EventManager.currentScreen == OpenScreen.PICTURE_EDITING)
                        return handleMouseInput((MouseInputEvent) event);
                    break;
            }
        }
        return false;
    }

    private boolean handleMouseInput(MouseInputEvent event) {
        if ((isSliding || event.getPosition().getY() > 100)) {
            if (event.wheelClicks() != 0) {
                //System.out.println("Moving " + event.wheelClicks());
                float change = Math.max(Math.min((zoom + 1) / PictureEditorManager.MAX_ZOOM * event.wheelClicks() + zoom, PictureEditorManager.MAX_ZOOM), 1.0f) - zoom;
                zoom += change;
                translation = new Point((translation.getX() * (zoom / (zoom - change)) - (event.getPosition().copy().getX() * (zoom / (zoom - change)) - event.getPosition().copy().getX())), (translation.getY() * (zoom / (zoom - change)) - (event.getPosition().copy().getY() * (zoom / (zoom - change)) - event.getPosition().copy().getY())));

                calculateDims();
                //this.forceOnScreen();
            }
            if (event.getClick(LEFT)) {
                if (!isSliding) {
                    mouseDragOffset = event.getPosition().subtract(translation);
                    startingPos = event.getPosition();
                } else {
                    //System.out.println(this.adjustedDims.getWidth() - (-translation.getX() + ScreenManager.windowSize.getWidth() * 2));
                    translation = event.getPosition().subtract(mouseDragOffset);
                    //this.forceOnScreen();
                }
                isSliding = true;
                hasActuallyMoved |= startingPos.distanceFrom(event.getPosition()) > 2;
            } else {
                if (event.justReleased(LEFT) && !hasActuallyMoved && event.getPosition().isInBoundingBox(translation.add(new Point(adjustedDims).multiply(0.5f)), adjustedDims, 1)) {
                    if (pop != null)
                        pop.destroy();
                    pop = new PixelPopUp(new PixelData(currentImage.getPixel(getPixelFromScreenCoord(event.getPosition())), getPixelFromScreenCoord(event.getPosition())), new PixelData(currentImage.getFinalPixel(getPixelFromScreenCoord(event.getPosition())), getPixelFromScreenCoord(event.getPosition())), this);
                }
                isSliding = hasActuallyMoved = false;
            }
            return isSliding;
        } else {
            return false;
        }
    }

    public void calculateDims() {
        adjustedDims = new Dimensions((int) (currentImage.getWidth() * zoom), (int) (currentImage.getHeight() * zoom));
    }

    private Point getPixelFromScreenCoord(Point coord) {
        return new Point((int) ((coord.getX() - translation.getX()) / zoom), (int) ((coord.getY() - translation.getY()) / zoom));
    }

    @Override
    public EventPriorities getPrio(EventTypes event) {
        if (event == EventTypes.RenderEvent)
            return EventPriorities.MIDDLE;
        return super.getPrio(event);
    }

    @Override
    public void reloadTextures() {

    }

    public void updateReplacement(PixelData startingData, PixelData finalData) {
        currentImage.updateCopy(startingData, finalData);
        if (!startingData.equals(finalData))
            transformations.put(startingData, finalData);
        else transformations.remove(startingData);
    }

    public void updateReplacement() {
        for (PixelData pd : transformations.keySet()) {
            updateReplacement(pd, transformations.get(pd));
        }
    }
}
