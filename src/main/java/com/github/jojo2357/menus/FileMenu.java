package com.github.jojo2357.menus;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.events.EventManager;
import com.github.jojo2357.events.events.KeyInputEvent;
import com.github.jojo2357.events.events.MouseInputEvent;
import com.github.jojo2357.events.events.RenderEvent;
import com.github.jojo2357.rendering.OpenScreen;
import com.github.jojo2357.rendering.ScreenManager;
import com.github.jojo2357.rendering.typeface.Colors;
import com.github.jojo2357.rendering.typeface.TextRenderer;
import com.github.jojo2357.util.Point;
import com.github.jojo2357.util.fileutilis.FileManager;
import com.github.jojo2357.util.fileutilis.FileObject;
import com.github.jojo2357.util.fileutilis.FolderObject;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FileMenu extends BasicMenu {
    private static final int tileSize = 50;
    private static final int tileSpacing = 5;
    private static FileMenu FILE_MENU;
    private int index = 0;
    private String cwd = "C:\\Users\\Joey\\Documents\\GitHub\\Picture-color-replacer";

    private FileObject[] files;
    private FolderObject[] folders;

    public static FileMenu createMainMenu() {
        if (FILE_MENU == null)
            FILE_MENU = new FileMenu();
        return FILE_MENU;
    }

    public static void loadMenu() {
        FILE_MENU.loadMenuSpecific();
    }

    public void loadMenuSpecific() {
        files = FileManager.getFiles(cwd, "^.*.((.jpg)|(.jpeg)|(.png))$");
        folders = FileManager.getFolders(cwd);
    }

    private FileMenu() {
        registerAllListeners();
    }

    @Override
    public void reloadTextures() {

    }

    @Override
    public <T extends EventBase> boolean notify(T event) {
        if (event instanceof MouseInputEvent) {
            handleMouse((MouseInputEvent) event);
        } else if (event instanceof RenderEvent) {
            handleRender((RenderEvent) event);
        } else if (event instanceof KeyInputEvent) {
            handleKeys((KeyInputEvent) event);
        }
        return false;
    }

    private void handleMouse(MouseInputEvent event) {
        if (EventManager.currentScreen == OpenScreen.FILE_CHOOSER) {
            if (event.wheelClicks() != 0) {
                //System.out.println(index + " : " + files.length + " : " + folders.length + " : " + (index - event.wheelClicks()) + " : " + (files.length + folders.length - ScreenManager.windowSize.getHeight() / tileSize - 1) + " : " + Math.max(Math.min(index - event.wheelClicks(), files.length + folders.length - ScreenManager.windowSize.getHeight() / tileSize - 1), 0));
                index = Math.max(Math.min(index - event.wheelClicks(), files.length + folders.length - ScreenManager.windowSize.getHeight() / tileSize), 0);
            }
            if (event.justReleased(MouseInputEvent.MouseButtons.LEFT)) {
                if ((int) event.getPosition().getY() / tileSize + index >= files.length) {
                    if ((int) event.getPosition().getY() / tileSize + index - files.length < folders.length) {
                        cwd = folders[index + (int) event.getPosition().getY() / tileSize - files.length].file.getAbsolutePath();
                        loadMenuSpecific();
                        index = 0;
                    }
                } else {
                    files[(int) event.getPosition().getY() / tileSize + index].selected = !files[(int) event.getPosition().getY() / tileSize + index].selected;
                }
            }
        }
    }

    private void handleRender(RenderEvent event) {
        if (EventManager.currentScreen == OpenScreen.FILE_CHOOSER) {
            int renders = 0;
            int i;
            for (i = index; renders < ScreenManager.windowSize.getHeight() / tileSize && i < files.length; i++, renders++) {
                ScreenManager.drawBoxFilled(new Point(5, renders * tileSize + tileSpacing), new Point(ScreenManager.windowSize.getWidth() - 5, (renders + 1) * tileSize - tileSpacing), files[i].selected ? Colors.GREEN : Colors.ORANGE);
                TextRenderer.render(files[i].getRenderName(), new Point(20, renders * tileSize + 25), 1000, files[i].selected ? Colors.ORANGE : Colors.GREEN);
            }
            for (i = Math.max(index - files.length, 0); renders < ScreenManager.windowSize.getHeight() / tileSize && i < folders.length; i++, renders++) {
                ScreenManager.drawBoxFilled(new Point(5, (renders) * tileSize + tileSpacing), new Point(ScreenManager.windowSize.getWidth() - 5, (renders + 1) * tileSize - tileSpacing), Colors.YELLOW);
                TextRenderer.render(folders[i].getRenderName(), new Point(20, (renders) * tileSize + 25), 1000, Colors.GREEN);
            }
        }
    }

    private void handleKeys(KeyInputEvent event) {
        //System.out.println("I hear " + (int) event.KEY);
        if (EventManager.currentScreen == OpenScreen.FILE_CHOOSER) {
            if (event.KEY == 256) {
                EventManager.currentScreen = OpenScreen.MAIN_MENU;
                //Enter
            } else if (event.KEY == 335 || event.KEY == 257) {
                EventManager.currentScreen = OpenScreen.PICTURE_EDITING;
                PictureEditorManager.addFiles(Arrays.stream(files).filter(fileObject -> fileObject.selected).collect(Collectors.toList()));
            } else if (event.KEY == 259) {
                cwd = new File(cwd).getParent();
                loadMenuSpecific();
                index = 0;
            }
        }
    }
}
