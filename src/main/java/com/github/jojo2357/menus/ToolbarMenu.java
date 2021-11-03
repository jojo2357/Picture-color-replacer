package com.github.jojo2357.menus;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.events.EventManager;
import com.github.jojo2357.events.EventPriorities;
import com.github.jojo2357.events.EventTypes;
import com.github.jojo2357.events.GameTimes;
import com.github.jojo2357.events.events.MouseInputEvent;
import com.github.jojo2357.imageediting.ImageMenu;
import com.github.jojo2357.rendering.OpenScreen;
import com.github.jojo2357.rendering.ScreenManager;
import com.github.jojo2357.util.PixelData;
import com.github.jojo2357.util.Point;
import com.github.jojo2357.util.fileutilis.ImageObject;

import java.util.HashMap;
import java.util.List;

import static com.github.jojo2357.events.events.MouseInputEvent.MouseButtons.LEFT;
import static com.github.jojo2357.util.Texture.*;

public class ToolbarMenu extends BasicMenu {
    private static final Point buttonSteps = new Point(12, 5);
    private static ToolbarMenu TOOLBAR_MENU;

    public static ToolbarMenu createMainMenu() {
        if (TOOLBAR_MENU == null)
            return TOOLBAR_MENU = new ToolbarMenu();
        return TOOLBAR_MENU;
    }

    private ToolbarMenu() {
        registerAllListeners();
    }

    @Override
    public <T extends EventBase> boolean notify(T event) {
        if (EventManager.currentScreen == OpenScreen.PICTURE_EDITING) {
            switch (event.getEventType()) {
                case RenderEvent:
                    if (EventManager.currentPhase == GameTimes.SECOND_RENDER)
                        renderMyself();
                    break;
                case MouseInputEvent:
                    handleMouseInput((MouseInputEvent) event);
                    break;
            }
        }
        return false;
    }

    private void renderMyself() {
        ScreenManager.drawBoxFilled(new Point(1, 100), new Point(ScreenManager.windowSize.getWidth(), 150), 128, 128, 128);
        ScreenManager.drawBox(new Point(1, 100), new Point(ScreenManager.windowSize.getWidth(), 150), 0, 0, 0);

        ScreenManager.drawBoxFilled(new Point(4, 104), new Point(22, 122), 64, 64, 64);
        ScreenManager.drawBox(new Point(4, 104), new Point(22, 122), 0, 0, 0);
        ScreenManager.renderTexture(panHand, new Point(13, 113), 16f / Math.max(panHand.getHeight(), panHand.getWidth()));

        ScreenManager.drawBoxFilled(new Point(4, 146), new Point(22, 128), 64, 64, 64);
        if (PictureEditorManager.hasCopyableTransformations())
            ScreenManager.drawBox(new Point(4, 146), new Point(22, 128), 0, 0, 0);
        else
            ScreenManager.drawBox(new Point(4, 146), new Point(22, 128), 255, 0, 0);
        ScreenManager.renderTexture(clipboard, new Point(13, 137), 16f / Math.max(clipboard.getHeight(), clipboard.getWidth()));

        ScreenManager.drawBoxFilled(new Point(34, 104), new Point(52, 122), 64, 64, 64);
        ScreenManager.drawBox(new Point(34, 104), new Point(52, 122), 0, 0, 0);
        ScreenManager.renderTexture(save, new Point(43, 113), 16f / Math.max(save.getHeight(), save.getWidth()));

        ScreenManager.drawBoxFilled(new Point(34, 146), new Point(52, 128), 64, 64, 64);
        if (PictureEditorManager.hasPasteableTransformations())
            ScreenManager.drawBox(new Point(34, 146), new Point(52, 128), 0, 0, 0);
        else
            ScreenManager.drawBox(new Point(34, 146), new Point(52, 128), 255, 0, 0);
        ScreenManager.renderTexture(paste, new Point(43, 137), 16f / Math.max(paste.getHeight(), paste.getWidth()));

        ScreenManager.drawBoxFilled(new Point(64, 104), new Point(82, 122), 64, 64, 64);
        if (PictureEditorManager.hasCopyableTransformations())
            ScreenManager.drawBox(new Point(64, 104), new Point(82, 122), 0, 0, 0);
        else
            ScreenManager.drawBox(new Point(64, 104), new Point(82, 122), 255, 0, 0);
        ScreenManager.renderTexture(trashCan, new Point(73, 113), 16f / Math.max(trashCan.getHeight(), trashCan.getWidth()));

        ScreenManager.drawBoxFilled(new Point(94, 104), new Point(112, 122), 64, 64, 64);
        ScreenManager.drawBox(new Point(94, 104), new Point(112, 122), 0, 0, 0);
        ScreenManager.renderTexture(textT, new Point(103, 113), 16f / Math.max(textT.getHeight(), textT.getWidth()));

        ScreenManager.drawBoxFilled(new Point(64, 146), new Point(82, 128), 64, 64, 64);
        ScreenManager.drawBox(new Point(64, 146), new Point(82, 128), 0, 0, 0);
        ScreenManager.renderTexture(makeMapping, new Point(73, 137), 16f / Math.max(makeMapping.getHeight(), makeMapping.getWidth()));

    }

    private boolean handleMouseInput(MouseInputEvent event) {
        if (PictureEditorManager.activeMenu != null && PictureEditorManager.activeMenu.wantsMouseControl())
            return false;
        if (event.justReleased(LEFT) && event.getPosition().getY() > 100 && event.getPosition().getY() < 150) {
            if ((event.getPosition().getX() - 4) % (20 + buttonSteps.getX()) < 20 && ((event.getPosition().getY() - 104) % (20 + buttonSteps.getY()) < 20)) {
                Point buttonClicked = new Point((int) ((event.getPosition().getX() - 4) / (20 + buttonSteps.getX())), (int) ((event.getPosition().getY() - 104) / (20 + buttonSteps.getY())));
                //System.out.println(buttonClicked + " | " + event.getPosition());
                switch ((int) buttonClicked.getY()) {
                    case 0:
                        switch ((int) buttonClicked.getX()) {
                            case 0:
                                //pan
                                break;
                            case 1: {
                                //save
                                if (PictureEditorManager.activeMenu == null)
                                    break;
                                if (ImageMenu.highlightedObjects.contains(PictureEditorManager.activeMenu.getImage()))
                                    for (ImageObject image : ImageMenu.highlightedObjects) {
                                        image.saveToFile(PictureEditorMenu.startingFind, PictureEditorMenu.endingFind);
                                    }
                                else
                                    PictureEditorManager.activeMenu.getImage().saveToFile(PictureEditorMenu.startingFind, PictureEditorMenu.endingFind);
                                break;
                            }
                            case 2: {
                                //trash
                                if (PictureEditorManager.activeMenu == null)
                                    break;
                                if (ImageMenu.highlightedObjects.contains(PictureEditorManager.activeMenu.getImage()))
                                    for (ImageObject image : ImageMenu.highlightedObjects) {
                                        image.resetChanges();
                                    }
                                else {
                                    PictureEditorManager.activeMenu.getImage().resetChanges();
                                }
                                PictureEditorManager.activeMenu.resetChanges();
                                break;
                            }
                            case 3:
                                if (PictureEditorManager.activeMenu != null) {
                                    PictureEditorManager.activeMenu.openTextPopup();
                                }
                                break;
                            default:
                                return false;
                        }
                        break;
                    case 1:
                        switch ((int) buttonClicked.getX()) {
                            case 0:
                                if (PictureEditorManager.hasCopyableTransformations()) {
                                    PictureEditorManager.copyTransformations();
                                    //System.out.println("Copy");
                                } //else System.out.println("No Copy");
                                break;
                            case 1:
                                if (PictureEditorManager.hasPasteableTransformations()) {
                                    PictureEditorManager.pasteTransformations();
                                    //System.out.println("Paste");
                                } //else System.out.println("No paste");
                                //paste
                                break;
                            case 2:
                                if (PictureEditorManager.activeMenu == null)
                                    break;
                                PictureEditorManager.scalpMapping();
                            default:
                                return false;
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            } //else System.out.println(event.getPosition());
            return true;
        }
        return false;
    }

    @Override
    public EventPriorities getPrio(EventTypes event) {
        if (event == EventTypes.MouseInputEvent)
            return EventPriorities.HIGHEST;
        return super.getPrio(event);
    }

    @Override
    public void reloadTextures() {

    }
}
