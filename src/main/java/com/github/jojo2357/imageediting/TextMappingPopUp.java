package com.github.jojo2357.imageediting;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.events.EventManager;
import com.github.jojo2357.events.EventPriorities;
import com.github.jojo2357.events.EventTypes;
import com.github.jojo2357.events.GameTimes;
import com.github.jojo2357.events.events.KeyInputEvent;
import com.github.jojo2357.events.events.MouseInputEvent;
import com.github.jojo2357.menus.PictureEditorMenu;
import com.github.jojo2357.rendering.RenderableObject;
import com.github.jojo2357.rendering.ScreenManager;
import com.github.jojo2357.rendering.typeface.TextRenderer;
import com.github.jojo2357.util.Dimensions;
import com.github.jojo2357.util.Point;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import static com.github.jojo2357.rendering.typeface.Colors.GRAY;
import static com.github.jojo2357.rendering.typeface.Colors.WHITE;

public class TextMappingPopUp extends RenderableObject {
    private static final Dimensions size = new Dimensions(300, 75);
    public String starting, ending;
    private PictureEditorMenu parent;
    private boolean editingTop = true, editingBottom = false;
    private int topCursor, bottomCursor;
    private boolean burnedClick = false;

    public TextMappingPopUp(PictureEditorMenu parent, String starting, String ending) {
        registerListeners(EventTypes.MouseInputEvent, EventTypes.RenderEvent, EventTypes.KeyInputEvent);
        this.starting = starting;
        this.ending = ending;
        this.parent = parent;
        topCursor = starting.length();
        bottomCursor = ending.length();
    }

    @Override
    public <T extends EventBase> boolean notify(T event) {
        switch (event.getEventType()) {
            case RenderEvent:
                if (EventManager.currentPhase == GameTimes.THIRD_RENDER) {
                    ScreenManager.drawBoxFilled(new Point(ScreenManager.windowSize.getWidth() / 2 - size.getWidth() / 2, ScreenManager.windowSize.getHeight() / 2 - size.getHeight() / 2), new Point(ScreenManager.windowSize.getWidth() / 2 + size.getWidth() / 2, ScreenManager.windowSize.getHeight() / 2 + size.getHeight() / 2), 64, 64, 64, 128);

                    TextRenderer.render("Find: ", new Point(ScreenManager.windowSize.getWidth() / 2 - size.getWidth() / 2 + 5, ScreenManager.windowSize.getHeight() / 2), 1000, WHITE);
                    TextRenderer.render(starting, new Point(ScreenManager.windowSize.getWidth() / 2 - size.getWidth() / 2 + 5 + 9 * 6, ScreenManager.windowSize.getHeight() / 2), 1000, editingBottom ? GRAY : WHITE);
                    if (editingTop)
                        ScreenManager.drawLine(new Point(ScreenManager.windowSize.getWidth() / 2 - size.getWidth() / 2 + 9 * 6 + 9 * (topCursor) + 1, ScreenManager.windowSize.getHeight() / 2 - 10), new Point(ScreenManager.windowSize.getWidth() / 2 - size.getWidth() / 2 + 9 * 6 + 9 * (topCursor) + 1, ScreenManager.windowSize.getHeight() / 2 + 10), 0, 0, 0);

                    TextRenderer.render("Replace: ", new Point(ScreenManager.windowSize.getWidth() / 2 - size.getWidth() / 2 + 5, ScreenManager.windowSize.getHeight() / 2 + 20), 1000, WHITE);
                    TextRenderer.render(ending, new Point(ScreenManager.windowSize.getWidth() / 2 - size.getWidth() / 2 + 5 + 9 * 9, ScreenManager.windowSize.getHeight() / 2 + 20), 1000, editingTop ? GRAY : WHITE);
                    if (editingBottom)
                        ScreenManager.drawLine(new Point(ScreenManager.windowSize.getWidth() / 2 - size.getWidth() / 2 + 9 * 9 + 9 * (bottomCursor) + 1, ScreenManager.windowSize.getHeight() / 2 + 30), new Point(ScreenManager.windowSize.getWidth() / 2 - size.getWidth() / 2 + 9 * 9 + 9 * (bottomCursor) + 1, ScreenManager.windowSize.getHeight() / 2 + 10), 0, 0, 0);

                    return false;
                }
                break;
            case MouseInputEvent:
                return handleMouseInput((MouseInputEvent) event);
            case KeyInputEvent:
                if ((((KeyInputEvent) event).KEY >= 'A' && ((KeyInputEvent) event).KEY <= 'Z') ||
                        (((KeyInputEvent) event).KEY >= '0' && ((KeyInputEvent) event).KEY <= '9') ||
                        (((KeyInputEvent) event).KEY >= ' ' && ((KeyInputEvent) event).KEY <= 'Z'))
                    return false;
                System.out.println((int) ((KeyInputEvent) event).KEY);
                String beginning = editingTop ? starting : ending;
                final String beginningImmuted = beginning;
                //System.out.println("1. " + starting + " | " + ending + " | " + beginning);
                switch(((KeyInputEvent) event).KEY){
                    case 259:
                        if (beginning.length() > 0) {
                            beginning = beginning.substring(0, (editingTop ? topCursor : bottomCursor) - 1) + beginning.substring((editingTop ? topCursor : bottomCursor));
                            if (editingTop)
                                topCursor = Math.max(topCursor - 1, 0);
                            else if (editingBottom)
                                bottomCursor = Math.max(bottomCursor-1, 0);
                        }
                        break;
                    case 263:
                        if (editingTop)
                            topCursor = Math.max(topCursor - 1, 0);
                        else if (editingBottom)
                            bottomCursor = Math.max(bottomCursor - 1, 0);
                        break;
                    case 262:
                        if (editingTop)
                            topCursor = Math.min(topCursor + 1, starting.length());
                        else if (editingBottom)
                            bottomCursor = Math.min(bottomCursor + 1, ending.length());
                        break;
                    case 264:
                        if (editingTop)
                            editingBottom = !(editingTop = false);
                        break;
                    case 265:
                        if (editingBottom)
                            editingBottom = !(editingTop = true);
                        break;
                    case 335:
                        parent.closeTextPopup();
                        return true;
                    case 342:
                        break;
                    default:
                        if (editingTop)
                            topCursor++;
                        else if (editingBottom)
                            bottomCursor++;
                        beginning = beginning + ((KeyInputEvent) event).KEY;
                }
                //System.out.println("2. " + starting + " | " + ending + " | " + beginning);
                if (!beginningImmuted.equals(beginning) && isValidPath(parent.getImage().getName(editingTop ? beginning : starting, editingBottom ? beginning : ending))) {
                    if (editingTop) {
                        starting = beginning;
                        PictureEditorMenu.startingFind = starting;
                    } else if (editingBottom) {
                        ending = beginning;
                        PictureEditorMenu.endingFind = ending;
                    } else throw new IllegalStateException("Careful witht those flag rags");
                }
                //System.out.println("3. " + starting + " | " + ending + " | " + beginning);
                return true;
        }
        return false;
    }

    private boolean handleMouseInput(MouseInputEvent event) {
        if (burnedClick == (burnedClick = true) && event.justReleased() && !event.getPosition().isInBoundingBox(new Point(ScreenManager.windowSize.multiply(0.5f)), size, 1)) {
            parent.closeTextPopup();
            return false;
        } else {
            if (event.getPosition().isInBoundingBox(new Point(ScreenManager.windowSize.multiply(0.5f)), size, 1)) {

                return true;
            }
        }
        return false;
    }

    public static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
        return true;
    }

    @Override
    public EventPriorities getPrio(EventTypes event) {
        return EventPriorities.HIGHEST;
    }

    @Override
    public void reloadTextures() {

    }
}
