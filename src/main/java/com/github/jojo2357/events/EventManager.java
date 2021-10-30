package com.github.jojo2357.events;

import com.github.jojo2357.events.events.RenderEvent;
import com.github.jojo2357.events.events.TickEvent;
import com.github.jojo2357.rendering.IRecievesEvent;
import com.github.jojo2357.rendering.OpenScreen;
import com.github.jojo2357.rendering.ScreenManager;
import com.github.jojo2357.util.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventManager {
    public static final TickEvent staticTickEvent = new TickEvent();
    private static final List<EventBase> events = new ArrayList<EventBase>();
    private static final HashMap<EventTypes, List<IRecievesEvent>> registeredListeners = new HashMap<>();
    public static GameTimes currentPhase = GameTimes.WAITING;
    public static OpenScreen currentScreen = OpenScreen.MAIN_MENU;
    public static boolean configured = false;

    public static void init() {
        for (EventTypes event : EventTypes.values())
            registeredListeners.put(event, new ArrayList<>());
        configured = true;
    }

    public static <T extends IRecievesEvent> void addListeningObject(T listeningObject, EventTypes eventToListenFor) {
        if (!registeredListeners.get(eventToListenFor).contains(listeningObject))
            registeredListeners.get(eventToListenFor).add(listeningObject);
        else
            System.out.println("Duplicate " + eventToListenFor.name() + " at index " + registeredListeners.get(eventToListenFor).indexOf(listeningObject));
    }

    public static <T extends IRecievesEvent> void disposeListener(T listeningObject) {
        for (EventTypes event : EventTypes.values())
            registeredListeners.get(event).remove(listeningObject);
    }

    public static void sendTickEvent() {
        notify(new TickEvent());
    }

    public static <T extends EventBase> boolean notify(T event) {
        //System.out.println(event.getEventType().getName() + " gotten!");
        if (event instanceof RenderEvent) {
            boolean toClose = ScreenManager.tick();
            ScreenManager.drawBox(new Point(10, 10), new Point(30, 30), 255, 255, 255);
            //ScreenManager.drawLine(new Point(100, 100), new Point(200, 200), 255, 255, 255);
            for (GameTimes gameTime : GameTimes.values()) {
                if (gameTime.name().contains("RENDER")) {
                    currentPhase = gameTime;
                    //MiscRenderer.render();
                    events.add(event);
                    EventManager.sendEvents();
                }
            }
            ScreenManager.finishRender();
            currentPhase = GameTimes.WAITING;
            return toClose;
        }

        events.add(event);
        return false;
    }

    public static void sendEvents() {
        int maxLoops = 10 * events.size();
        int loopsMade = 0;
        boolean terminateLoops = false;
        while (events.size() > 0) {
            /*if (events.get(0).getEventType() != EventTypes.RenderEvent) {*/
            allFors:
            {// cant use for...each because events.remove(0); crashes it
                for (int eventLooper = 0; eventLooper < EventPriorities.values().length; eventLooper++) {
                    EventPriorities prio = EventPriorities.values()[eventLooper];
                    for (int objectLooper = 0; objectLooper < registeredListeners.get(events.get(0).getEventType()).size(); objectLooper++) {
                        IRecievesEvent target = registeredListeners.get(events.get(0).getEventType()).get(objectLooper);
                        if (target.getPrio(events.get(0).getEventType()) == prio && target.notify(events.get(0)) && events.get(0).getEventType().canBeConsumed) {
                            //System.out.println(target.getClass().toString() + " has consumed a " + events.get(0).getEventType().name());
                            break allFors;
                        }
                    }
                }
            }
            events.remove(0);
            loopsMade++;
            if (loopsMade > maxLoops)// debug because reasons
                throw new RuntimeException("Infinite loop?");
        }
    }
}
