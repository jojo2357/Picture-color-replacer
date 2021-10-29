package com.github.jojo2357.imageediting;

import com.github.jojo2357.events.events.MouseInputEvent;
import com.github.jojo2357.menus.PictureEditorMenu;
import com.github.jojo2357.rendering.RenderableObject;
import com.github.jojo2357.rendering.ScreenManager;
import com.github.jojo2357.util.fileutilis.ImageObject;
import com.github.jojo2357.util.Point;

import java.util.List;

import static com.github.jojo2357.events.events.MouseInputEvent.MouseButtons.LEFT;

public class ImageMenu {
    private static float imageMenuIndex, mouseDragOffset, startingIndex;
    private static boolean isDragging = false, hasActuallyMoved = false;

    public static void renderOtherImages(List<ImageObject> images) {
        ScreenManager.drawBoxFilled(new Point(1, 1), new Point(ScreenManager.windowSize.getWidth(), 100), 128, 128, 128);
        int i = (int)Math.floor(imageMenuIndex); float renders = - (imageMenuIndex % 1);
        for (; i < images.size() && renders < ScreenManager.windowSize.getWidth() / 100f; i++, renders++) {
            ScreenManager.renderTexture(images.get(i), new Point(100 * renders + 50, 50), 80f / Math.max(images.get(i).getWidth(), images.get(i).getHeight()));
        }
        ScreenManager.drawBox(new Point(1, 1), new Point(ScreenManager.windowSize.getWidth(), 100), 0, 0, 0);
    }

    public static boolean handleMouse(MouseInputEvent event) {
        if (event.getClick(LEFT)) {
            if (!isDragging) {
                mouseDragOffset = -event.getPosition().getX() / 100 - imageMenuIndex;
                startingIndex = event.getPosition().getX();
            } else {
                //System.out.println(this.adjustedDims.getWidth() - (-translation.getX() + ScreenManager.windowSize.getWidth() * 2));
                imageMenuIndex = Math.max(Math.min(-event.getPosition().getX() / 100 - mouseDragOffset, PictureEditorMenu.getNumFiles() - ScreenManager.windowSize.getWidth() / 100f), 0);
                //this.forceOnScreen();
            }
            isDragging = true;
            hasActuallyMoved |= Math.abs(event.getPosition().getX() - startingIndex) > 2;
        } else {
            if (!hasActuallyMoved && event.getPosition().getY() >= 10 && event.getPosition().getY() <= 90 && event.justReleased(LEFT)){
                if ((event.getPosition().getX() / 100 + imageMenuIndex) % 1 >= 0.1 && (event.getPosition().getX() / 100 + imageMenuIndex) % 1 <= 0.9)
                PictureEditorMenu.changeSelection((int)(event.getPosition().getX() / 100 + imageMenuIndex));
                //System.out.println("you clicked " + whatYouClicked + " (" + imageMenuIndex + ") " + event.getPosition());
            }
            isDragging = hasActuallyMoved = false;
            if (event.wheelClicks() != 0) {
                //System.out.println("Moving " + event.wheelClicks());
                float change = Math.max(Math.min(0.5f * event.wheelClicks() + imageMenuIndex, PictureEditorMenu.getNumFiles() - ScreenManager.windowSize.getWidth() / 100f), 0f) - imageMenuIndex;
                imageMenuIndex += change;
            }
        }
        return isDragging;
    }

    public static boolean wantMouseControl() {
        return isDragging;
    }
}
