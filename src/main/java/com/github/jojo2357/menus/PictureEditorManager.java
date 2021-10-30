package com.github.jojo2357.menus;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.events.EventManager;
import com.github.jojo2357.events.EventPriorities;
import com.github.jojo2357.events.EventTypes;
import com.github.jojo2357.events.events.MouseInputEvent;
import com.github.jojo2357.imageediting.ImageMenu;
import com.github.jojo2357.rendering.OpenScreen;
import com.github.jojo2357.util.PixelData;
import com.github.jojo2357.util.fileutilis.FileObject;
import com.github.jojo2357.util.fileutilis.ImageObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PictureEditorManager extends BasicMenu {
    static final ArrayList<ImageObject> openFiles = new ArrayList<>();
    static final float MAX_ZOOM = 32f;
    static final HashMap<ImageObject, PictureEditorMenu> menus = new HashMap<>();
    public static PictureEditorMenu activeMenu;
    static int imageIndex = -1;
    private static PictureEditorManager PICTURE_EDITOR;

    private static HashMap<PixelData, PixelData> transformations;

    public static void copyTransformations() {
        transformations = activeMenu.transformations;
    }

    public static void pasteTransformations() {
        if (transformations != null) {
            activeMenu.transformations = transformations;
            activeMenu.updateReplacement();
        }
    }

    public static boolean hasCopyableTransformations() {
        return activeMenu != null && activeMenu.transformations.keySet().size() > 0;
    }

    public static boolean hasPasteableTransformations() {
        return activeMenu != null && transformations != null && transformations.keySet().size() > 0 && activeMenu.transformations != transformations;
    }

    public static int getNumFiles() {
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
        }
    }

    public static void changeSelection(int newIndex) {
        if (imageIndex == newIndex)
            return;
        imageIndex = newIndex;
        if (menus.containsKey(openFiles.get(newIndex)))
            activeMenu = menus.get(openFiles.get(newIndex));
        else
            menus.put(openFiles.get(newIndex), activeMenu = new PictureEditorMenu(openFiles.get(newIndex)));
        activeMenu.calculateDims();
        //activeMenu.translation = (new Point(ScreenManager.windowSize).multiply(0.5f)).subtract(new Point(PictureEditorMenu.currentImage.width / 2f * PictureEditorMenu.zoom, PictureEditorMenu.currentImage.heigth / 2f * PictureEditorMenu.zoom));
    }

    public static PictureEditorManager createMainMenu() {
        if (PICTURE_EDITOR == null)
            PICTURE_EDITOR = new PictureEditorManager();
        return PICTURE_EDITOR;
    }

    public PictureEditorManager() {
        registerAllListeners();
    }

    @Override
    public <T extends EventBase> boolean notify(T event) {
        switch (event.getEventType()) {
            case RenderEvent:
                if (EventManager.currentScreen == OpenScreen.PICTURE_EDITING) {
                    switch (EventManager.currentPhase) {
                        case SECOND_RENDER:
                            ImageMenu.renderOtherImages(PictureEditorManager.openFiles);
                            break;
                    }
                }
                break;
            case MouseInputEvent:
                if (EventManager.currentScreen == OpenScreen.PICTURE_EDITING) {
                    return ImageMenu.handleMouse((MouseInputEvent) event);
                }
        }
        return false;
    }

    @Override
    public EventPriorities getPrio(EventTypes event) {
        if (event == EventTypes.RenderEvent)
            return EventPriorities.MIDDLE;
        if (event == EventTypes.MouseInputEvent)
            return EventPriorities.MIDDLE;
        return super.getPrio(event);
    }

    @Override
    public void reloadTextures() {

    }

    public void copyRequested() {

    }
}
