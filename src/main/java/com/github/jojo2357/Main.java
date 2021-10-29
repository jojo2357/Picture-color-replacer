package com.github.jojo2357;

import com.github.jojo2357.events.EventManager;
import com.github.jojo2357.events.events.RenderEvent;
import com.github.jojo2357.menus.FileMenu;
import com.github.jojo2357.menus.MainMenu;
import com.github.jojo2357.menus.PictureEditorMenu;
import com.github.jojo2357.rendering.ScreenManager;
import com.github.jojo2357.rendering.typeface.JojoFont;

public class Main {
    public static final long serialVersionUID = 1;
    public static double fps = 60;
    public static double frameLength = (1000.0 / fps);

    public static void main(String[] args) {
        long[] lastTimes = new long[100];
        int index = 0;
        int totalSkips = 0;
        ScreenManager.init();
        JojoFont.init();
        EventManager.init();
        long timeIn = System.currentTimeMillis();
        long last;
        int loops = 0;
        MainMenu.createMainMenu();
        FileMenu.createMainMenu();
        PictureEditorMenu.createMainMenu();
        do {
            last = System.currentTimeMillis();
            //loops++;
            EventManager.sendTickEvent();
            EventManager.sendEvents();
            if (EventManager.notify(new RenderEvent())) break;
            try {
                if ((long) (frameLength - (System.currentTimeMillis() - last)) > 0) {
                    Thread.sleep((long) (frameLength - (System.currentTimeMillis() - last)));
                } else {
                    //System.out.println("SKIPPED FRAME (" + (frameLength - (System.currentTimeMillis() - last)) + ") " + (++totalSkips));
                }
            } catch (Exception e) {
                System.out.println("FAILED FRAME " + (long) (frameLength - (System.currentTimeMillis() - last)));
            }
            //fps logger
            /*lastTimes[index] = System.currentTimeMillis() - last;
            if (++index == 100){
                int sum = 0;
                for (long q : lastTimes)
                    sum += q;
                index = 0;
                System.out.println("100 frames in " + sum + "ms averaging " + 100000/sum + "fps");
            }*/
        } while (true);
    }
}
