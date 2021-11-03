package com.github.jojo2357.imageediting;

import com.github.jojo2357.events.events.KeyInputEvent;
import com.github.jojo2357.events.events.MouseInputEvent;
import com.github.jojo2357.menus.PictureEditorManager;
import com.github.jojo2357.rendering.ScreenManager;
import com.github.jojo2357.util.Point;
import com.github.jojo2357.util.fileutilis.ImageObject;

import java.util.ArrayList;
import java.util.List;

import static com.github.jojo2357.events.events.MouseInputEvent.MouseButtons.*;

public class ImageMenu {
    private static float imageMenuIndex, mouseDragOffset, startingIndex;
    private static boolean isDragging = false, hasActuallyMoved = false;

    public static final List<ImageObject> highlightedObjects = new ArrayList<>();

    public static void renderOtherImages(List<ImageObject> images) {
        ScreenManager.drawBoxFilled(new Point(1, 1), new Point(ScreenManager.windowSize.getWidth(), 100), 128, 128, 128);
        int i = (int) Math.floor(imageMenuIndex);
        float renders = -(imageMenuIndex % 1);
        for (; i < images.size() && renders < ScreenManager.windowSize.getWidth() / 100f; i++, renders++) {
            if (highlightedObjects.contains(images.get(i)))
                ScreenManager.drawBoxFilled(new Point(100 * renders, 0), new Point(100 * (renders + 1), 100), 0, 255, 0);
            if (PictureEditorManager.activeMenu != null && images.get(i) == PictureEditorManager.activeMenu.getImage())
                ScreenManager.drawBoxFilled(new Point(100 * renders, 0), new Point(100 * (renders + 1), 100), 0, 0, 255, 50);
            ScreenManager.renderTexture(images.get(i), new Point(100 * renders + 50, 50), 80f / Math.max(images.get(i).getWidth(), images.get(i).getHeight()));
        }
        ScreenManager.drawBox(new Point(1, 1), new Point(ScreenManager.windowSize.getWidth(), 100), 0, 0, 0);
    }

    public static boolean handleMouse(MouseInputEvent event) {
        if (!isDragging && event.getPosition().getY() > 100)
            return false;
        if (event.getClick(LEFT)) {
            if (!isDragging) {
                mouseDragOffset = -event.getPosition().getX() / 100 - imageMenuIndex;
                startingIndex = event.getPosition().getX();
            } else {
                //System.out.println(this.adjustedDims.getWidth() - (-translation.getX() + ScreenManager.windowSize.getWidth() * 2));
                imageMenuIndex = Math.max(Math.min(-event.getPosition().getX() / 100 - mouseDragOffset, PictureEditorManager.getNumFiles() - ScreenManager.windowSize.getWidth() / 100f), 0);
                //this.forceOnScreen();
            }
            isDragging = true;
            hasActuallyMoved |= Math.abs(event.getPosition().getX() - startingIndex) > 2;
        } else {
            if (!hasActuallyMoved && event.getPosition().getY() >= 10 && event.getPosition().getY() <= 90 && event.justReleased(LEFT)) {
                if ((event.getPosition().getX() / 100 + imageMenuIndex) % 1 >= 0.1 && (event.getPosition().getX() / 100 + imageMenuIndex) % 1 <= 0.9 && (int) (event.getPosition().getX() / 100 + imageMenuIndex) < PictureEditorManager.openFiles.size()) {
                    if (KeyInputEvent.hasStoredModification(KeyInputEvent.MODIFICATIONS.CTRL)){
                        PictureEditorManager.unloadObject((int) (event.getPosition().getX() / 100 + imageMenuIndex));
                    } else
                        PictureEditorManager.changeSelection((int) (event.getPosition().getX() / 100 + imageMenuIndex));
                }
                //System.out.println("you clicked " + whatYouClicked + " (" + imageMenuIndex + ") " + event.getPosition());
            }
            isDragging = hasActuallyMoved = false;
            if (event.wheelClicks() != 0) {
                //System.out.println("Moving " + event.wheelClicks());
                float change = Math.max(Math.min(0.5f * event.wheelClicks() + imageMenuIndex, PictureEditorManager.getNumFiles() - ScreenManager.windowSize.getWidth() / 100f), 0f) - imageMenuIndex;
                imageMenuIndex += change;
            }
        }
        if (event.justReleased(RIGHT)){
            if ((event.getPosition().getX() / 100 + imageMenuIndex) % 1 >= 0.1 && (event.getPosition().getX() / 100 + imageMenuIndex) % 1 <= 0.9 && (int) (event.getPosition().getX() / 100 + imageMenuIndex) < PictureEditorManager.openFiles.size())
                if (highlightedObjects.contains(PictureEditorManager.openFiles.get((int) (event.getPosition().getX() / 100 + imageMenuIndex))))
                    highlightedObjects.remove(PictureEditorManager.openFiles.get((int) (event.getPosition().getX() / 100 + imageMenuIndex)));
                else
                    highlightedObjects.add(PictureEditorManager.openFiles.get((int) (event.getPosition().getX() / 100 + imageMenuIndex)));
        }
        return isDragging;
    }
}
