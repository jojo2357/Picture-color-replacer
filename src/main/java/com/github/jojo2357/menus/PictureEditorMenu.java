package com.github.jojo2357.menus;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.events.EventManager;
import com.github.jojo2357.events.EventPriorities;
import com.github.jojo2357.events.EventTypes;
import com.github.jojo2357.events.events.MouseInputEvent;
import com.github.jojo2357.imageediting.ImageMenu;
import com.github.jojo2357.imageediting.PixelPopUp;
import com.github.jojo2357.rendering.OpenScreen;
import com.github.jojo2357.rendering.ScreenManager;
import com.github.jojo2357.util.Dimensions;
import com.github.jojo2357.util.PixelData;
import com.github.jojo2357.util.fileutilis.FileObject;
import com.github.jojo2357.util.fileutilis.ImageObject;
import com.github.jojo2357.util.Point;

import java.util.ArrayList;
import java.util.List;

import static com.github.jojo2357.events.events.MouseInputEvent.MouseButtons.LEFT;
import static com.github.jojo2357.events.events.MouseInputEvent.MouseButtons.MIDDLE;

public class PictureEditorMenu extends BasicMenu {
    private static final ArrayList<ImageObject> openFiles = new ArrayList<>();
    private static final float MAX_ZOOM = 32f;
    private static int imageIndex = 0;
    private static Point translation = new Point(ScreenManager.windowSize.getWidth() / 5, ScreenManager.windowSize.getHeight() / 2);
    private static float zoom = 1;
    private static PictureEditorMenu PICTURE_EDITOR;
    private static boolean isSliding;
    private static Point mouseDragOffset;
    private static Dimensions adjustedDims;
    private static ImageObject currentImage;
    private static PixelPopUp pop;
    private static boolean hasActuallyMoved;
    private static Point startingPos;

    public static PictureEditorMenu createMainMenu() {
        if (PICTURE_EDITOR == null)
            PICTURE_EDITOR = new PictureEditorMenu();
        return PICTURE_EDITOR;
    }

    public static int getNumFiles () {
        return openFiles.size();
    }

    public static void addFiles(FileObject[] files) {
        for (FileObject file : files) {
            openFiles.add(new ImageObject(file.file));
        }
    }

    public static void addFiles(List<FileObject> files) {
        for (FileObject file : files) {
            openFiles.add(new ImageObject(file.file));
            if (currentImage == null && openFiles.size() > 0) {
                currentImage = openFiles.get(0);
                calculateDims();
                translation = new Point(ScreenManager.windowSize.getWidth() / 4, ScreenManager.windowSize.getHeight() / 2);
            }
        }
    }

    public static void changeSelection(int newIndex) {
        if (imageIndex == newIndex)
            return;
        imageIndex = newIndex;
        currentImage = openFiles.get(imageIndex);
        calculateDims();
        translation = (new Point(ScreenManager.windowSize).multiply(0.5f)).subtract(new Point(currentImage.width / 2f * zoom, currentImage.heigth / 2f * zoom));
    }

    public static Point getPixelCenter(Point imageLocation) {
        return new Point(translation.getX() + zoom * (imageLocation.getX() + 0.5), translation.getY() + zoom * (imageLocation.getY() + 0.5));
    }

    public static void highLightPixel(Point imageLocation) {
        ScreenManager.drawBox(new Point(translation.getX() + zoom * imageLocation.getX(), translation.getY() + zoom * (imageLocation.getY())),
            new Point(translation.getX() + zoom * (imageLocation.getX() + 1), translation.getY() + zoom * (imageLocation.getY() + 1)),
                0, 0, 0);
    }

    private static void calculateDims() {
        adjustedDims = new Dimensions((int) (currentImage.getWidth() * zoom), (int) (currentImage.getHeight() * zoom));
    }

    private PictureEditorMenu() {
        registerAllListeners();
    }

    @Override
    protected void drawMenu() {

    }

    @Override
    public <T extends EventBase> boolean notify(T event) {
        switch (event.getEventType()) {
            case RenderEvent:
                if (EventManager.currentScreen == OpenScreen.PICTURE_EDITING) {
                    switch (EventManager.currentPhase) {
                        case FIRST_RENDER:
                            if (openFiles.size() > 0)
                                ScreenManager.renderTextureLimited(openFiles.get(imageIndex), translation.add(new Point(currentImage.width / 2 * zoom, currentImage.heigth / 2 * zoom)), 1, this.rotation, this.adjustedDims, new Point(0, 100), new Dimensions(ScreenManager.windowSize.getWidth()/2, ScreenManager.windowSize.getHeight() - 100));
                            break;
                        case SECOND_RENDER:
                            ImageMenu.renderOtherImages(openFiles);
                            break;
                    }
                }
                break;
            case MouseInputEvent:
                if (EventManager.currentScreen == OpenScreen.PICTURE_EDITING)
                    handleMouseInput((MouseInputEvent) event);
                break;
            case TickEvent:
                if (currentImage == null && openFiles.size() > 0) {
                    currentImage = openFiles.get(0);
                    calculateDims();
                    translation = new Point(ScreenManager.windowSize.getWidth() / 4, ScreenManager.windowSize.getHeight() / 2);
                }
                break;
        }
        return false;
    }

    private boolean handleMouseInput(MouseInputEvent event) {
        if (!ImageMenu.wantMouseControl() && (isSliding || event.getPosition().getY() > 100)) {
            if (event.wheelClicks() != 0) {
                //System.out.println("Moving " + event.wheelClicks());
                float change = Math.max(Math.min((zoom + 1) / MAX_ZOOM * event.wheelClicks() + zoom, MAX_ZOOM), 1.0f) - zoom;
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
                    pop = new PixelPopUp(new PixelData(currentImage.getPixel(getPixelFromScreenCoord(event.getPosition())), getPixelFromScreenCoord(event.getPosition())));
                }
                isSliding = hasActuallyMoved = false;
            }
            return isSliding;
        } else {
            return ImageMenu.handleMouse(event);
        }
    }

    private Point getPixelFromScreenCoord(Point coord) {
        return new Point((int)((coord.getX() - translation.getX()) / zoom), (int)((coord.getY() - translation.getY()) / zoom));
    }

    @Override
    public void reloadTextures() {

    }

    @Override
    public EventPriorities getPrio(EventTypes event) {
        if (event == EventTypes.RenderEvent)
            return EventPriorities.MIDDLE;
        return super.getPrio(event);
    }
}
