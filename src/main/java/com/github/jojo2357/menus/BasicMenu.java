package com.github.jojo2357.menus;

import com.github.jojo2357.rendering.RenderableObject;
import com.github.jojo2357.util.InteractableObject;

import java.util.ArrayList;

public abstract class BasicMenu extends RenderableObject {
    protected ArrayList<InteractableObject> buttons = new ArrayList<>();

    protected abstract void drawMenu();
}
