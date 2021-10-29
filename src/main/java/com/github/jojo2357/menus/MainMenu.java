package com.github.jojo2357.menus;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.events.EventManager;
import com.github.jojo2357.events.events.MouseInputEvent;
import com.github.jojo2357.events.events.RenderEvent;
import com.github.jojo2357.rendering.OpenScreen;
import com.github.jojo2357.rendering.ScreenManager;
import com.github.jojo2357.rendering.typeface.Colors;
import com.github.jojo2357.rendering.typeface.TextRenderer;
import com.github.jojo2357.util.Button;
import com.github.jojo2357.util.Dimensions;
import com.github.jojo2357.util.Point;

public class MainMenu extends BasicMenu {
    private static MainMenu MAIN_MENU;

    public static MainMenu createMainMenu() {
        if (MAIN_MENU == null)
            MAIN_MENU = new MainMenu();
        return MAIN_MENU;
    }

    private MainMenu() {
        registerAllListeners();
        buttons.add(new Button(new Point(100, 100), new Dimensions(50, 50), (MouseInputEvent event) -> {
            return EventManager.currentScreen == OpenScreen.MAIN_MENU && event.justReleased(MouseInputEvent.MouseButtons.LEFT) && event.getPosition().isInBoundingBox(new Point(100, 100), new Dimensions(50, 50), 1);
        }, (MouseInputEvent event) -> {
            System.out.println("Passed");
            return false;
        }, (RenderEvent event) -> {
            if (EventManager.currentScreen == OpenScreen.MAIN_MENU) {
                ScreenManager.drawBoxFilled(new Point(75, 75), new Point(125, 125), Colors.GREEN);
                TextRenderer.renderJustified("Welcome", new Point(100, 100), 1200, Colors.ORANGE);
            }
        }));

        buttons.add(new Button(new Point(200, 100), new Dimensions(50, 50), (MouseInputEvent event) -> {
            return EventManager.currentScreen == OpenScreen.MAIN_MENU && event.justReleased(MouseInputEvent.MouseButtons.LEFT) && event.getPosition().isInBoundingBox(new Point(200, 100), new Dimensions(50, 50), 1);
        }, (MouseInputEvent event) -> {
            EventManager.currentScreen = OpenScreen.FILE_CHOOSER;
            FileMenu.loadMenu();
            return true;
        }, (RenderEvent event) -> {
            if (EventManager.currentScreen == OpenScreen.MAIN_MENU) {
                ScreenManager.drawBoxFilled(new Point(175, 75), new Point(225, 125), Colors.GREEN);
                TextRenderer.renderJustified("Open Images", new Point(200, 100), 1200, Colors.ORANGE);
            }
        }));
    }

    @Override
    protected void drawMenu() {
        //ScreenManager.renderTexture();
    }

    @Override
    public void reloadTextures() {

    }

    @Override
    public <T extends EventBase> boolean notify(T event) {
        return false;
    }
}
